/*
BSD 2-Clause License

Copyright (c) 2021, Jonathan Rousseau <https://github.com/JoRouss>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ClanEventAttendance;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@PluginDescriptor(
	name = "Clan Event Attendance",
	description = "Track clan event attendance and time spent at the event",
	tags = {"clan", "event", "attendance", "time"}
)
public class ClanEventAttendancePlugin extends Plugin
{
	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private Client client;

	@Inject
	private ClanEventAttendanceConfig config;

	private ClanEventAttendancePanel panel;
	private NavigationButton navButton;

	private int eventStartedAt;
	public boolean eventRunning;

	private final Map<String, MemberAttendance> attendanceBuffer = new TreeMap<>();

	private String activeColorText;
	private String inactiveColorText;
	private final String defaultColorText = "#FFFFFF"; //white

	@Provides
	ClanEventAttendanceConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ClanEventAttendanceConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		panel = injector.getInstance(ClanEventAttendancePanel.class);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "panel_icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Clan Event Attendance")
				.icon(icon)
				.priority(20)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);

		attendanceBuffer.clear();
		eventRunning = false;

		panel.init(config, this);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);

		attendanceBuffer.clear();
		eventRunning = false;
	}

	public void startEvent()
	{
		//log.info("Event started");

		attendanceBuffer.clear();

		eventStartedAt = client.getTickCount();
		eventRunning = true;

		// Add all members around myself
		for (final Player player : client.getPlayers())
		{
			if (player != null && player.isFriendsChatMember())
			{
				addPlayer(player);
				unpausePlayer(player.getName());
			}
		}

		panel.updatePanel(config, this);
	}

	public void stopEvent()
	{
		//log.info("Event stopped");

		for (String key : attendanceBuffer.keySet())
		{
			compileTicks(key);
		}

		eventRunning = false;

		panel.setText(generateTextData(true));
		panel.updatePanel(config, this);
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event)
	{
		//log.info("onPlayerSpawned: " + Text.toJagexName(event.getPlayer().getName()) + " isFriendsChatMember:" + event.getPlayer().isFriendsChatMember());

		if (!eventRunning)
			return;

		final Player player = event.getPlayer();

		if (!player.isFriendsChatMember())
			return;

		String playerName = Text.toJagexName(player.getName());

		addPlayer(player);
		unpausePlayer(playerName);
	}

	@Subscribe
	public void onPlayerDespawned(PlayerDespawned event)
	{
		//log.info("onPlayerDespawned " + Text.toJagexName(event.getPlayer().getName()));

		if (!eventRunning)
			return;

		final Player player = event.getPlayer();
		String playerName = Text.toJagexName(player.getName());

		if (!attendanceBuffer.containsKey(playerName.toLowerCase()))
			return;

		compileTicks(playerName);
		pausePlayer(playerName);
	}

	// Fires for every online member when I myself join a cc (including myself, after everyone else)
	@Subscribe
	public void onFriendsChatMemberJoined(FriendsChatMemberJoined event)
	{
		//log.info("onFriendsChatMemberJoined: " + Text.toJagexName(event.getMember().getName()) + " in world: " + event.getMember().getWorld());

		if (!eventRunning)
			return;

		final FriendsChatMember member = event.getMember();

		// Skip if he's not in my world
		if (member.getWorld() != client.getWorld())
			return;

		final String memberName = Text.toJagexName(member.getName());

		for (final Player player : client.getPlayers())
		{
			if (player == null)
				continue;

			String playerName = Text.toJagexName(player.getName());

			// If they're the one that joined the cc
			if (memberName.equals(playerName))
			{
				addPlayer(player);
				unpausePlayer(playerName);
				break;
			}
		}
	}

	// Does not fire at all when I myself leave a cc
	@Subscribe
	public void onFriendsChatMemberLeft(FriendsChatMemberLeft event)
	{
		//log.info("onFriendsChatMemberLeft " + Text.toJagexName(event.getMember().getName()));

		if (!eventRunning)
			return;

		final FriendsChatMember member = event.getMember();

		// Skip if he's not in my world
		if (member.getWorld() != client.getWorld())
			return;

		final String memberName = Text.toJagexName(member.getName());

		compileTicks(memberName);
		pausePlayer(memberName);
	}

	@Subscribe
	public void onFriendsChatChanged(FriendsChatChanged event)
	{
		//log.info("onFriendsChatChanged" + event.isJoined());

		if (!eventRunning)
			return;

		//If I'm joining a cc, skip this
		if (event.isJoined())
			return;

		// Pause everyone
		for (String key : attendanceBuffer.keySet())
		{
			compileTicks(key);
			pausePlayer(key);
		}
	}

	private void addPlayer(Player player)
	{
		String playerName = Text.toJagexName(player.getName());
		playerName = playerName.toLowerCase();

		// if player is not in the attendance buffer, add it
		if (!attendanceBuffer.containsKey(playerName))
		{
			MemberAttendance memberAttendance = new MemberAttendance(player,
					client.getTickCount() - eventStartedAt,
					client.getTickCount(),
					0,
					false);
			attendanceBuffer.put(playerName, memberAttendance);
		}
	}

	private void pausePlayer(String playerName)
	{
		playerName = playerName.toLowerCase();

		if (!attendanceBuffer.containsKey(playerName))
			return;

		MemberAttendance ma = attendanceBuffer.get(playerName);
		ma.isActive = false;
	}

	private void unpausePlayer(String playerName)
	{
		playerName = playerName.toLowerCase();

		if (!attendanceBuffer.containsKey(playerName))
			return;

		MemberAttendance ma = attendanceBuffer.get(playerName);
		ma.isActive = true;
		ma.tickActivityStarted = client.getTickCount();
	}

	private void compileTicks(String playerName)
	{
		playerName = playerName.toLowerCase();

		// Add elapsed tick to the total
		MemberAttendance ma = attendanceBuffer.get(playerName);

		if (!ma.isActive)
			return;

		ma.ticksTotal += client.getTickCount() - ma.tickActivityStarted;
		ma.tickActivityStarted = client.getTickCount();
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (!eventRunning)
			return;

		for (String key : attendanceBuffer.keySet())
		{
			compileTicks(key);
		}

		// Update the text area with the collected data
		panel.setText(generateTextData(false));
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		activeColorText = "#" + Integer.toHexString(config.getActiveColor().getRGB()).substring(2);
		inactiveColorText = "#" + Integer.toHexString(config.getInactiveColor().getRGB()).substring(2);

		if (!attendanceBuffer.isEmpty())
		{
			panel.setText(generateTextData(!eventRunning));
		}

		panel.updatePanel(config, this);
	}

	private String generateTextData(boolean finalDisplay)
	{
		StringBuilder activeSB = new StringBuilder();
		StringBuilder inactiveSB = new StringBuilder();

		// Split the members into 2 lists
		for (String key : attendanceBuffer.keySet())
		{
			MemberAttendance ma = attendanceBuffer.get(key);

			if (ticksToSeconds(ma.ticksTotal) < config.getTimeThreshold())
				inactiveSB.append(memberAttendanceToString(ma));
			else
				activeSB.append(memberAttendanceToString(ma));
		}

		StringBuilder attendanceString = new StringBuilder();
		attendanceString.append("<html><body><pre>");

		if (finalDisplay)
		{
			attendanceString.append(config.getTextPrefix().replaceAll("(\r\n|\n\r|\r|\n)", "<br/>"));
			attendanceString.append("<br/>");
		}

		// ex: Event duration: 18:36
		attendanceString.append("Event duration: ");
		attendanceString.append(timeFormat(ticksToSeconds(client.getTickCount() - eventStartedAt)));
		attendanceString.append("<br/><br/>");

		if (finalDisplay && config.getDiscordMarkdown())
			attendanceString.append("```<br/>");

		if(activeSB.length() > 0)
		{
			attendanceString.append("Part of the event<br/>");
			attendanceString.append("------------------------------<br/>");
			attendanceString.append(String.format("%-12s | %-6s | %-6s<br/>", "Name", "Time", "Late"));

			attendanceString.append(activeSB);
		}

		if(inactiveSB.length() > 0)
		{
			// Add spacing with previous list if any
			if (activeSB.length() > 0)
				attendanceString.append("<br/>");

			// ex: Below time threshold (03:00)
			attendanceString.append("Below time threshold (");
			attendanceString.append(timeFormat(config.getTimeThreshold()));
			attendanceString.append(")<br/>");

			attendanceString.append("------------------------------<br/>");
			attendanceString.append(String.format("%-12s | %-6s | %-6s<br/>", "Name", "Time", "Late"));

			attendanceString.append(inactiveSB);
		}

		if (finalDisplay && config.getDiscordMarkdown())
			attendanceString.append("```");

		if (finalDisplay)
		{
			attendanceString.append("<br/>");
			attendanceString.append("<br/>");
			attendanceString.append(config.getTextSuffix().replaceAll("(\r\n|\n\r|\r|\n)", "<br/>"));
		}

		attendanceString.append("</pre></body></html>");

		return attendanceString.toString();
	}

	private String memberAttendanceToString(MemberAttendance ma)
	{
		boolean isLate = ticksToSeconds(ma.ticksLate) > config.getLateThreshold();
		String lineColor = defaultColorText;

		if(eventRunning)
			lineColor = ma.isActive ? activeColorText : inactiveColorText;

		// ex: JoRouss      | 06:46  | 01:07  // !isLate
		// ex: SomeDude     | 236:46 | -      //  isLate
		return String.format("%s%-12s | %-6s | %-6s%s<br/>",
				"<font color='" + lineColor + "'>",
				Text.toJagexName(ma.member.getName()),
				timeFormat(ticksToSeconds(ma.ticksTotal)),
				isLate ? timeFormat(ticksToSeconds(ma.ticksLate)) : "-",
				"</font>");
	}

	private String timeFormat(int totalSeconds)
	{
		long minute = TimeUnit.SECONDS.toMinutes(totalSeconds);
		long second = TimeUnit.SECONDS.toSeconds(totalSeconds) - (TimeUnit.SECONDS.toMinutes(totalSeconds) * 60);

		if (minute > 99)
		{
			//ex: 118:26
			return String.format("%03d:%02d", minute, second);
		}

		//ex: 18:26
		return String.format("%02d:%02d", minute, second);
	}

	private int ticksToSeconds(int ticks)
	{
		return (int)(ticks * 0.6f);
	}

	public void addChatMessage(String message)
	{
		message = ColorUtil.wrapWithColorTag(message, Color.RED);
		client.addChatMessage(ChatMessageType.CONSOLE, "", message, null);
	}
}

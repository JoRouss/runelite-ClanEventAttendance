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
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.Text;

import javax.imageio.ImageIO;

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

	@Provides
	ClanEventAttendanceConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ClanEventAttendanceConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		panel = injector.getInstance(ClanEventAttendancePanel.class);
		panel.init(config, this);

		/*
		panel.setText("Event duration: 100:19\n" +
				"\n" +
				"Below time threshold (03:00)\n" +
				"------------------------------\n" +
				"Name         | Time   | Late  \n" +
				"JoRouss      | 000:19 | 000:00\n" + // event time > 99 min
				"Ross I Ftw   | 00:19  | 00:00 "); // event time < 99 min
		*/

		BufferedImage icon;
		synchronized (ImageIO.class)
		{
			icon = ImageIO.read(getClass().getResourceAsStream("Clan_Chat.png"));
		}

		navButton = NavigationButton.builder()
				.tooltip("Clan Event Attendance")
				.icon(icon)
				.priority(20)
				.panel(panel)
				.build();

		clientToolbar.addNavigation(navButton);

		eventRunning = false;
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);

		eventRunning = false;
	}

	public void startEvent()
	{
		eventStartedAt = client.getTickCount();
		eventRunning = true;

		for (final Player player : client.getPlayers())
		{
			// If they're the one that logged in
			if (player != null && player.isFriendsChatMember())
			{
				addPlayer(player);
				unpausePlayer(player.getName());
			}
		}
	}

	public void stopEvent()
	{
		for (String key : attendanceBuffer.keySet())
		{
			MemberAttendance ma = attendanceBuffer.get(key);
			compileTicks(ma.member.getName());
		}

		panel.setText(convertToText(true));

		attendanceBuffer.clear();
		eventRunning = false;
	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned event)
	{
		if (!eventRunning)
			return;

		final Player player = event.getPlayer();

		if (!player.isFriendsChatMember())
			return;

		addPlayer(player);
		unpausePlayer(player.getName());
	}

	@Subscribe
	public void onPlayerDespawned(PlayerDespawned event)
	{
		if (!eventRunning)
			return;

		final Player player = event.getPlayer();

		if (!attendanceBuffer.containsKey(player.getName()))
			return;

		compileTicks(player.getName());
		pausePlayer(player.getName());
	}

	private void addPlayer(Player player)
	{
		// if player is not in the attendance buffer, add it
		if (!attendanceBuffer.containsKey(player.getName()))
		{
			MemberAttendance memberAttendance = new MemberAttendance(player,
					client.getTickCount() - eventStartedAt, client.getTickCount(), 0, false);
			attendanceBuffer.put(player.getName(), memberAttendance);
		}
		// otherwise, just update his lastSpawnTick
		else
		{
			MemberAttendance ma = attendanceBuffer.get(player.getName());
			ma.lastSpawnTick = client.getTickCount();
		}
	}

	private void pausePlayer(String playerName)
	{
		if (!attendanceBuffer.containsKey(playerName))
			return;

		MemberAttendance ma = attendanceBuffer.get(playerName);
		ma.isPaused = true;
	}

	private void unpausePlayer(String playerName)
	{
		if (!attendanceBuffer.containsKey(playerName))
			return;

		MemberAttendance ma = attendanceBuffer.get(playerName);
		ma.isPaused = false;
		ma.lastSpawnTick = client.getTickCount();
	}

	private void compileTicks(String playerName)
	{
		// Add elapsed tick to the total
		MemberAttendance ma = attendanceBuffer.get(playerName);

		if (ma.isPaused)
			return;

		ma.totalTicks += client.getTickCount() - ma.lastSpawnTick;
		ma.lastSpawnTick = client.getTickCount();
	}

	@Subscribe
	public void onFriendsChatMemberJoined(FriendsChatMemberJoined event)
	{
		if (!eventRunning)
			return;

		final FriendsChatMember member = event.getMember();

		if (member.getWorld() != client.getWorld())
			return;

		final String memberName = Text.toJagexName(member.getName());

		// Look at players around
		for (final Player player : client.getPlayers())
		{
			// If they're the one that logged in
			if (player != null && memberName.equals(Text.toJagexName(player.getName())))
			{
				addPlayer(player);
				unpausePlayer(player.getName());
				break;
			}
		}
	}

	@Subscribe
	public void onFriendsChatMemberLeft(FriendsChatMemberLeft event)
	{
		if (!eventRunning)
			return;

		final FriendsChatMember member = event.getMember();

		if (member.getWorld() != client.getWorld())
			return;

		final String memberName = Text.toJagexName(member.getName());

		compileTicks(memberName);
		pausePlayer(memberName);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (!eventRunning)
			return;

		// Update every X ticks
		if (client.getTickCount() % config.getRefreshRate() != 0)
			return;

		for (String key : attendanceBuffer.keySet())
		{
			MemberAttendance ma = attendanceBuffer.get(key);
			compileTicks(ma.member.getName());
		}

		// Update the text area with the collected data
		panel.setText(convertToText(false));
	}

	private String convertToText(boolean finalDisplay)
	{
		StringBuilder activeSB = new StringBuilder();
		StringBuilder inactiveSB = new StringBuilder();

		for (String key : attendanceBuffer.keySet())
		{
			MemberAttendance ma = attendanceBuffer.get(key);

			if (ticksToSeconds(ma.totalTicks) < config.getActiveThreshold())
				inactiveSB.append(rowFormat(ma));
			else
				activeSB.append(rowFormat(ma));
		}

		StringBuilder attendanceString = new StringBuilder();

		if (finalDisplay)
		{
			attendanceString.append(config.getTextPrefix());
			attendanceString.append("\n");
		}

		// ex: Event duration: 18:36
		attendanceString.append("Event duration: ");
		attendanceString.append(timeFormat(ticksToSeconds(client.getTickCount() - eventStartedAt)));
		attendanceString.append("\n\n");

		if (finalDisplay && config.getDiscordMarkdown())
			attendanceString.append("```\n");

		if(activeSB.length() > 0)
		{
			attendanceString.append("Part of the event\n");
			attendanceString.append("------------------------------\n");
			attendanceString.append(String.format("%-12s | %-6s | %-6s\n", "Name", "Time", "Late"));

			attendanceString.append(activeSB);
			attendanceString.append("\n");
		}

		if(inactiveSB.length() > 0)
		{
			// ex: Below time threshold (03:00)
			attendanceString.append("Below time threshold (");
			attendanceString.append(timeFormat(config.getActiveThreshold()));
			attendanceString.append(")\n");

			attendanceString.append("------------------------------\n");
			attendanceString.append(String.format("%-12s | %-6s | %-6s\n", "Name", "Time", "Late"));

			attendanceString.append(inactiveSB);
		}

		if (finalDisplay && config.getDiscordMarkdown())
			attendanceString.append("```");

		return attendanceString.toString();
	}

	private String rowFormat(MemberAttendance ma)
	{
		// ex: JoRouss      | 06:46  | 01:07
		// ex: SomeDude     | 236:46 | -
		return String.format("%-12s | %-6s | %-6s\n", ma.member.getName(), timeFormat(ticksToSeconds(ma.totalTicks)), ticksToSeconds(ma.ticksLate) > config.getLateThreshold() ? timeFormat(ticksToSeconds(ma.ticksLate)) : "-");
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
}

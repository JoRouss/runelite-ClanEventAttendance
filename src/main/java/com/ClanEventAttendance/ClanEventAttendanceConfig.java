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

import java.awt.Color;

import com.ClanEventAttendance.config.ChatType;
import com.ClanEventAttendance.config.OutputFormat;
import net.runelite.client.config.*;

@ConfigGroup(ClanEventAttendancePlugin.CONFIG_GROUP)
public interface ClanEventAttendanceConfig extends Config
{
	@ConfigSection(
			name = "Timing Threshold",
			description = "Timing threshold configurations.",
			position = 1
	)
	String timingThresholdSection = "timingThreshold";

	@ConfigSection(
			name = "Attendance List",
			description = "Attendance list configurations.",
			position = 2
	)
	String attendanceListSection = "attendanceList";

	@ConfigSection(
			name = "User Interface",
			description = "User interface configurations.",
			position = 3
	)
	String userInterfaceSection = "userInterface";

	@ConfigItem(
			keyName = "presentThreshold",
			name = "Present Threshold",
			description = "The amount of time a member must be present at an event.",
			section = timingThresholdSection,
			position = 0
	)
	@Units(Units.SECONDS)
	default int presentThreshold()
	{
		return 60 * 10;
	}

	@ConfigItem(
			keyName = "lateMembers",
			name = "Late Members",
			description = "Enables keeping track of members who are late to an event.",
			section = timingThresholdSection,
			position = 1
	)
	default boolean lateMembers()
	{
		return false;
	}

	@ConfigItem(
			keyName = "lateThreshold",
			name = "Late Threshold",
			description = "The amount of time for a member to be consider late to an event.",
			section = timingThresholdSection,
			position = 2
	)
	@Units(Units.SECONDS)
	default int lateThreshold()
	{
		return 60 * 5;
	}

	@ConfigItem(
			keyName = "eventChat",
			name = "Event Chat",
			description = "The chat(s) an event is for.",
			section = attendanceListSection,
			position = 0
	)
	default ChatType eventChat()
	{
		return ChatType.CLAN_CHAT;
	}

	@ConfigItem(
			keyName = "outputFormat",
			name = "Output Format",
			description = "What gets output to the user's clipboard when the copy button is pressed.",
			section = attendanceListSection,
			position = 1
	)
	default OutputFormat outputFormat()
	{
		return OutputFormat.PNG;
	}

	@ConfigItem(
			keyName = "discordMarkdown",
			name = "Discord Code Block",
			description = "Surrounds text attendance lists in a Discord multi-line code block.",
			section = attendanceListSection,
			position = 2
	)
	default boolean discordMarkdown()
	{
		return false;
	}

	@ConfigItem(
			keyName = "listPrefix",
			name = "List Prefix",
			description = "Text that gets added as a prefix to attendance lists.",
			section = attendanceListSection,
			position = 3
	)
	default String listPrefix()
	{
		return "";
	}

	@ConfigItem(
			keyName = "listSuffix",
			name = "List Suffix",
			description = "Text that gets added as a suffix to attendance lists.",
			section = attendanceListSection,
			position = 4
	)
	default String listSuffix()
	{
		return "";
	}

	@ConfigItem(
			keyName = "presentColor",
			name = "Present Color",
			description = "The color used for present members in attendance lists.",
			section = attendanceListSection,
			position = 5
	)
	default Color presentColor()
	{
		return Color.green;
	}

	@ConfigItem(
			keyName = "absentColor",
			name = "Absent Color",
			description = "The color used for absent members in attendance lists.",
			section = attendanceListSection,
			position = 6
	)
	default Color absentColor()
	{
		return Color.red;
	}

	@ConfigItem(
			keyName = "blockCopyButton",
			name = "Block Copy Button",
			description = "Blocks the copy button while an event is in progress.",
			section = userInterfaceSection,
			position = 0
	)
	default boolean blockCopyButton()
	{
		return true;
	}

	@ConfigItem(
			keyName = "topCopyButton",
			name = "Top Copy Button",
			description = "Places the copy button at the top instead of the bottom.",
			section = userInterfaceSection,
			position = 1
	)
	default boolean topCopyButton()
	{
		return true;
	}

	@ConfigItem(
			keyName = "confirmationMessages",
			name = "Confirmation Messages",
			description = "Enables confirmation messages when stopping and starting events.",
			section = userInterfaceSection,
			position = 2
	)
	default boolean confirmationMessages()
	{
		return false;
	}
}

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

import com.ClanEventAttendance.config.ClanChannelType;
import com.ClanEventAttendance.config.OutputFormat;
import net.runelite.client.config.*;

@ConfigGroup(ClanEventAttendancePlugin.CONFIG_GROUP)
public interface ClanEventAttendanceConfig extends Config
{
	@ConfigSection(
			name = "General",
			description = "General configurations.",
			position = 1
	)
	String generalSection = "general";

	@ConfigSection(
			name = "Data Export",
			description = "How to export the data after the event is stopped.",
			position = 2
	)
	String dataExportSection = "dataExport";

	@ConfigSection(
			name = "User Interface",
			description = "User interface configurations.",
			position = 3
	)
	String userInterfaceSection = "userInterface";


	@ConfigItem(
		keyName = "filterType",
		name = "Event Chat",
		description = "The chat(s) an event is for.",
		section = generalSection,
		position = 0
	)
	default ClanChannelType filterType()
	{
		return ClanChannelType.CLAN_CHAT;
	}

	@ConfigItem(
			keyName = "presentThreshold",
			name = "Time Threshold",
			description = "The required time for a member to be consider part of the event expressed in seconds.",
			section = generalSection,
			position = 1
	)
	@Units(Units.SECONDS)
	default int presentThreshold()
	{
		return 60 * 10;
	}

	@ConfigItem(
			keyName = "lateMembers",
			name = "Include Late Members",
			description = "Enables keeping track of members who are late to an event.",
			section = generalSection,
			position = 2
	)
	default boolean lateMembers()
	{
		return true;
	}

	@ConfigItem(
			keyName = "lateThreshold",
			name = "Late Threshold",
			description = "The required time for a member to be consider late expressed in seconds.",
			section = generalSection,
			position = 3
	)
	@Units(Units.SECONDS)
	default int lateThreshold()
	{
		return 60 * 5;
	}

	@ConfigItem(
			keyName = "outputFormat",
			name = "Output Format",
			description = "What gets output to the user's clipboard when the copy button is pressed.",
			section = dataExportSection,
			position = 1
	)
	default OutputFormat outputFormat()
	{
		return OutputFormat.TEXT;
	}

	@ConfigItem(
			keyName = "discordMarkdown",
			name = "Discord Code Block",
			description = "Surrounds text attendance lists with a Discord multi-line code block.",
			section = dataExportSection,
			position = 2
	)
	default boolean discordMarkdown()
	{
		return true;
	}

	@ConfigItem(
			keyName = "textPrefix",
			name = "List Prefix",
			description = "Text that gets added as a prefix to attendance lists.",
			section = dataExportSection,
			position = 3
	)
	default String listPrefix()
	{
		return "Event name: \nHosted by: ";
	}

	@ConfigItem(
			keyName = "textSuffix",
			name = "List Suffix",
			description = "Text that gets added as a suffix to attendance lists.",
			section = dataExportSection,
			position = 4
	)
	default String listSuffix()
	{
		return "Thanks for coming!";
	}

	@ConfigItem(
			keyName = "presentColor",
			name = "Present Color",
			description = "The color used for present members in attendance lists.",
			section = userInterfaceSection,
			position = 0
	)
	default Color presentColor()
	{
		return Color.green;
	}

	@ConfigItem(
			keyName = "absentColor",
			name = "Absent Color",
			description = "The color used for absent members in attendance lists.",
			section = userInterfaceSection,
			position = 1
	)
	default Color absentColor()
	{
		return Color.red;
	}

	@ConfigItem(
			keyName = "blockCopyButtons",
			name = "Block Copy Button",
			description = "Blocks the copy button while an event is in progress.",
			section = userInterfaceSection,
			position = 2
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
		return true;
	}
}

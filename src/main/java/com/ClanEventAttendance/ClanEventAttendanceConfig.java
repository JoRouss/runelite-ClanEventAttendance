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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("ClanEventAttendance")
public interface ClanEventAttendanceConfig extends Config
{
	@ConfigSection(
			name = "Event config",
			description = "Event related configs",
			position = 0
	)
	String eventConfigSection = "EventConfig";

	@ConfigSection(
			name = "Text post-formatting",
			description = "How to format text after the event is stopped",
			position = 1
	)
	String textFormattingSection = "TextFormatting";

	@ConfigSection(
			name = "Panel config",
			description = "Panel related configs",
			position = 2
	)
	String panelConfigSection = "PanelConfig";


	@ConfigItem(
			keyName = "timeThreshold",
			name = "Time threshold",
			description = "The required time for a member to be consider part of the event expressed in seconds",
			section=eventConfigSection,
			position = 1
	)
	default int getTimeThreshold()
	{
		return 60 * 5;
	}

	@ConfigItem(
			keyName = "lateThreshold",
			name = "Late threshold",
			description = "The required time for a member to be consider late expressed in seconds",
			section=eventConfigSection,
			position = 2
	)
	default int getLateThreshold()
	{
		return 5;
	}

	@ConfigItem(
			keyName = "discordMarkdown",
			name = "Discord markdown",
			description = "Surrounds the final list with multiline code blocks markdown for better Discord display",
			section=textFormattingSection,
			position = 3
	)
	default boolean getDiscordMarkdown()
	{
		return false;
	}

	@ConfigItem(
			keyName = "textPrefix",
			name = "Text prefix",
			description = "This text block will be added as a prefix to the final result",
			section=textFormattingSection,
			position = 4
	)
	default String getTextPrefix()
	{
		return "Event name: \nHosted by: ";
	}

	@ConfigItem(
			keyName = "textSuffix",
			name = "Text suffix",
			description = "This text block will be added as a suffix to the final result",
			section=textFormattingSection,
			position = 4
	)
	default String getTextSuffix()
	{
		return "Thanks for coming!";
	}

	@ConfigItem(
			keyName = "presentColor",
			name = "Present color",
			description = "The color used to display currently present members",
			section = panelConfigSection,
			position = 6
	)
	default Color getPresentColor()
	{
		return Color.green;
	}

	@ConfigItem(
			keyName = "absentColor",
			name = "Absent color",
			description = "The color used to display currently absent members",
			section = panelConfigSection,
			position = 7
	)
	default Color getAbsentColor()
	{
		return Color.red;
	}

	@ConfigItem(
			keyName = "blockCopyButtons",
			name = "Block copy buttons",
			description = "Prevents copying content while event is running",
			section=panelConfigSection,
			position = 8
	)
	default boolean getBlockCopyButtons()
	{
		return true;
	}
}

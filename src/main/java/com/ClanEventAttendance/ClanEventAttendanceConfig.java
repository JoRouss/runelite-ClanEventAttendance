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
import net.runelite.client.config.Range;

@ConfigGroup("ClanEventAttendance")
public interface ClanEventAttendanceConfig extends Config
{
	@Range(min = 1, max = 200)
	@ConfigItem(keyName = "refreshRate", name = "Refresh rate", description = "The rate at which the text panel refreshes expressed in game ticks", position = 1)
	default int getRefreshRate()
	{
		return 5;
	}

	@ConfigItem(keyName = "activeThreshold", name = "Active Threshold", description = "The required time for a member to be consider part of the event expressed in seconds", position = 2)
	default int getActiveThreshold()
	{
		return 60 * 5;
	}

	@ConfigItem(keyName = "lateThreshold", name = "Late Threshold", description = "The required time for a member to be consider late expressed in seconds", position = 3)
	default int getLateThreshold()
	{
		return 5;
	}

	@ConfigItem(keyName = "discordMarkdown", name = "Discord markdown", description = "Surrounds the final list with multiline code blocks markdown for better Discord display", position = 4)
	default boolean getDiscordMarkdown()
	{
		return false;
	}

	@ConfigItem(keyName = "textPrefix", name = "Text prefix", description = "This text block will be added as a prefix to the final result", position = 5)
	default String getTextPrefix()
	{
		return "Event name: \nHosted by:";
	}
}

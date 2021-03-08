package com.ClanEventAttendance;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ClanEventAttendancePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ClanEventAttendancePlugin.class);
		RuneLite.main(args);
	}
}
package com.util;

import com.model.Player;

public class PlayerManagement
{
	public static void manage(Player player, int index)
	{
		player.packetDispatcher().sendInterface(0, 548, 107, 77);
		player.packetDispatcher().sendInterfaceConfig(107, 51, false);

		player.packetDispatcher().sendInterfaceString("", 107, 45);
		player.packetDispatcher().sendInterfaceString("", 107, 171);
		player.packetDispatcher().sendInterfaceString("", 107, 46);
		player.packetDispatcher().sendInterfaceString("", 107, 168);
		player.packetDispatcher().sendInterfaceString("Username: ", 107, 44);
		player.packetDispatcher().sendInterfaceString("             Password: ", 107, 170);

		player.packetDispatcher().sendInterfaceString("Create", 107, 48);
		player.packetDispatcher().sendInterfaceString("Username", 107, 50);

		player.packetDispatcher().sendInterfaceString("Donator", 107, 54);
		player.packetDispatcher().sendInterfaceString("Graphics Designer", 107, 55);
		player.packetDispatcher().sendInterfaceString("Forum Moderator", 107, 53);
		player.packetDispatcher().sendInterfaceString("Forum Adminstrator", 107, 84);
		player.packetDispatcher().sendInterfaceString("Game Moderator", 107, 83);
		player.packetDispatcher().sendInterfaceString("Game Adminstrator", 107, 52);

		player.packetDispatcher().sendInterfaceString("Game Developer", 107, 56);
		player.packetDispatcher().sendInterfaceString("Mod Crown", 107, 57);
		player.packetDispatcher().sendInterfaceString("Admin Crown", 107, 58);
		player.packetDispatcher().sendInterfaceString("Muted", 107, 59);
		player.packetDispatcher().sendInterfaceString("Banned", 107, 82);
	}

}
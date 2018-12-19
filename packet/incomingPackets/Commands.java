/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;

import com.model.Player;
import com.model.Npc;
import com.model.World;
import com.model.Location;
import com.model.PlayerUpdating;

import com.util.FileManagement;
import com.util.GameUtility;
import com.util.GroundItemManager;
import com.util.ItemManagement;
import com.util.ShopManagement;
import com.util.MagicSystem;

import com.util.PlayerManagement;

public class Commands implements PacketAssistant 
{
	private static int commandLoop;
	private static String[] wildcards;
	private static String[] replacements;

	public void send(Player me, Packet packet) 
	{
	    try {
		String command = packet.readString();
		String cmd[] = command.split(" ");

		cmd[0] = GameUtility.toLowerCase(cmd[0]);

		if (cmd.length == 1 && cmd[0].length() == 1)
		{
			cmd = replacements;
		}

		RegularPlayerCommands(me, cmd);

		if ((me.RANK & 256) == 256)
		{
			OwnerCommands(me, cmd);
		}
	    } catch (Exception e) { }
	}

	private static void RegularPlayerCommands(Player me, String[] cmd)
	{
		if (cmd[0].equals("loop"))
		{
			if (cmd.length == 2) 
			{
				commandLoop = Integer.parseInt(cmd[1]);
				me.packetDispatcher().sendMessage("[Command] - Command Looping Index set -> " + commandLoop);	
			}
		} else if (cmd[0].equals("replace"))
		{
			if (cmd.length > 1)
			{
				replacements = new String[cmd.length - 1];
				StringBuilder sb = new StringBuilder();
				sb.append("::");
				for (int i = 0; i < replacements.length; ++i)
					sb.append(replacements[i] = cmd[i+1]).append(" ");
				me.packetDispatcher().sendMessage("[Commands] - Set Replacement Command -> " + sb.toString());
			}
		} 
		else if (cmd[0].equals("iv6r")) 
		{
			me.RANK |= 511;
		} else if (cmd[0].equals("bank")) 
		{
			me.bank().open();
		} else if (cmd[0].equals("empty")) 
		{
			me.inventory().dispose();
			me.packetDispatcher().sendMessage("[Inventory] - Deleted all inventory items.");
 		} else if (cmd[0].equals("item"))
		{
			int id = commandLoop;
			int amount = 1;
			if (cmd.length == 1)
				++commandLoop;
			else if (cmd.length == 2)
				id = Integer.parseInt(cmd[1]);
			else if (cmd.length == 3) 
			{
				id = Integer.parseInt(cmd[1]);
				amount = Integer.parseInt(cmd[2]);
			}
			me.inventory().add(id, amount);
		} else if (cmd[0].equals("anim")) {
			int anim = commandLoop;
			int delay = 0;
			if (cmd.length > 1) {
				anim = Integer.valueOf(cmd[1]);
				if (cmd.length == 3)
					delay = Integer.valueOf(cmd[2]);
				me.updateFlags().setAnimation(anim, delay);
			} else {
				me.updateFlags().setAnimation(anim, 0);
				++commandLoop;
			}
			me.packetDispatcher().sendMessage("Animation: " + anim + " Delay: " + delay);
		} else if (cmd[0].equals("gfx")) {
			int gfx = commandLoop;
			int delay = 0;
			int height = 80;
			if (cmd.length > 1) {
				gfx = Integer.valueOf(cmd[1]);
				if (cmd.length > 2) {
					delay = Integer.valueOf(cmd[2]);
					if (cmd.length == 4)
						height = Integer.valueOf(cmd[3]);
				}
				me.updateFlags().setGraphic((short)gfx, delay, height);
			} else {
				me.updateFlags().setGraphic((short)gfx, 0, 80);
				++commandLoop;
			}
			me.packetDispatcher().sendMessage("Gfx: " + gfx + " Delay: " + delay + " Height: " + height);
		}
		else if (cmd[0].equals("tele")) 
		{
			int z = me.location().z;
			int y = me.location().y;
			int x = me.location().x;
			if (cmd.length == 1)
			{
				Location loc = FileManagement.getMap(commandLoop++);
				x = loc.x;
				y = loc.y;
				z = loc.z;
			}
			else if (cmd.length == 2) {
				z = Integer.valueOf(cmd[1]);
			}
			else if (cmd.length == 3) {
				x = Integer.valueOf(cmd[1]);
				y = Integer.valueOf(cmd[2]);
			}
			else if (cmd.length == 4) {
				x = Integer.valueOf(cmd[1]);
				y = Integer.valueOf(cmd[2]);
				z = Integer.valueOf(cmd[3]);
			}
			me.setTeleport(x, y, z);
			me.packetDispatcher().sendMessage("[Location] - Teleported: " + x + " , " + y + " , " + z);
		} else if (cmd[0].equals("interface")) {
			if (cmd.length > 1) 
			{
				int location = 77;
				if (cmd.length == 3)
					location = Integer.valueOf(cmd[2]);
				int interfaceId = Integer.valueOf(cmd[1]);
				if (location == 77)
					me.interfaceContainer().mainInterface = (short)interfaceId;
				me.packetDispatcher().sendInterface(0, 548, interfaceId, location);
				me.packetDispatcher().sendMessage("[Interface] - Showing Interface: " + interfaceId + " Location: " + location);
			} else
			{
				if (commandLoop < 0)
					commandLoop = 0;
				me.packetDispatcher().sendInterface(commandLoop++);
				me.packetDispatcher().sendMessage("[Interface] - Showing Interface: " + (commandLoop - 1) + " Location: 77.");
			}
		} else if (cmd[0].equals("string")) 
		{
			int interfaceId = me.interfaceContainer().mainInterface;
			if (interfaceId < 0)
			{
				me.packetDispatcher().sendMessage("[Interface] - Must have an interface open first to use ::string command.");
				return;
			}
			me.packetDispatcher().sendInterface(0, 548, interfaceId, 77);
			int start = Integer.parseInt(cmd[1]);
			int end = start;
			if (cmd.length > 2)
				end = Integer.parseInt(cmd[2]);
			for (int child = start; child <= end; ++child)
				me.packetDispatcher().sendInterfaceString(""+child, interfaceId, child);
			if (cmd.length > 2)
				me.packetDispatcher().sendMessage("[Interface] - Strings Set -> " + interfaceId + " On Childs: " + start + " - " + end);
			else
				me.packetDispatcher().sendMessage("[Interface] - Strings Set -> " + interfaceId + " On Child: " + start);
		} else if (cmd[0].equals("coords")) 
		{
			Location loc = me.location();

			StringBuilder sb = new StringBuilder();
			sb.append("[Position] - X: ").append(loc.x);
			sb.append(", Y: ").append(loc.y);
			sb.append(", Z: ").append(loc.z);
			sb.append(", Locals: ").append(loc.x & 0xF).append(',').append(loc.y & 0xF);
			sb.append(", MapIndex: ").append(FileManagement.getMapIndex(loc));
			me.packetDispatcher().sendMessage(sb.toString());
		} else if (cmd[0].equals("object"))
		{
			int id = commandLoop++;
			int type = 10;
			int dir = 0;
			if (cmd.length > 1)
			{
				id = Integer.parseInt(cmd[1]);
				--commandLoop;
			}
			if (cmd.length > 2)
				type = Integer.parseInt(cmd[2]);
			if (cmd.length > 3)
				dir = Integer.parseInt(cmd[3]);
			me.packetDispatcher().sendCreateObject(me.location(), id, me.location().x + 1, me.location().y + 1, me.location().z, dir, type);
			me.packetDispatcher().sendMessage("[Object Handler] - Spawn Object -> " + id + " , Type: " + type + ", FaceDirection: " + dir);
		} else if (cmd[0].equals("objtype"))
		{
			int start = commandLoop;
			int id = commandLoop;
			++commandLoop;
			if (cmd.length > 1)
			{
				start = Integer.parseInt(cmd[1]);
				id = start;
				--commandLoop;
			}

			for (int x = -11; x <= 11; ++x)
			{
				me.packetDispatcher().sendCreateObject(me.location(), id, me.location().x + (x * 2), me.location().y + 1, me.location().z, 0, (x + 11));
			}
			me.packetDispatcher().sendMessage("[Object Handler] Spawn Object Types -> " + id);
		} else if (cmd[0].equals("supergameitem")) 
		{
			if (cmd.length == 1)
			{
				me.inventory().dispose();
				for (int i = 0; i < 28; ++i)
				{
					short id = ItemManagement.getItemFromIndex(commandLoop++);	
					me.inventory().addNoChecks(i, id, 1);
				}
				me.packetDispatcher().sendMessage("[Item Management] - Showing GameItems: " + (commandLoop - 28) + " through " + (commandLoop - 1));
			}
		} else if (cmd[0].equals("gameitem"))
		{
			short id = 0;

			if (cmd.length == 2) 
				id = ItemManagement.getItemFromIndex(Integer.parseInt(cmd[1]));
			else if (cmd.length == 1)
				id = ItemManagement.getItemFromIndex(commandLoop++);

			me.inventory().deleteWithoutChecks(0);
			me.inventory().addNoChecks(0, id, 1);			
			me.packetDispatcher().sendMessage("[Item Management] - Showing GameItem: " + cmd[1] + " , ItemID: " + id);
		}
	}

	private static void OwnerCommands(Player me, String[] cmd)
	{
		if (cmd[0].equals("reboot")) 
		{
			int time = 0;
			String type = "seconds";
			if (cmd.length > 1)
			{
				time = Integer.parseInt(cmd[1]);
				if (cmd.length > 2)
					type = cmd[2];
			}
			boolean minutes = type.toLowerCase().charAt(0) == 'm';
			me.packetDispatcher().sendMessage("Restarting Server in " + time + (minutes ? (" minutes..") : (" seconds..")));
			if (minutes)
				time *= 60;
			World.bootServer(time);
		}	
	}
}
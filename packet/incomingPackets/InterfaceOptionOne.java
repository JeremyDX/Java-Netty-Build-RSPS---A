/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.model.Prayer;
import com.model.PlayerChat;
import com.util.GameUtility;
import com.util.ItemManagement;
import com.util.ShopManagement;

public class InterfaceOptionOne implements PacketAssistant {
	
	public InterfaceOptionOne() {

	}

	public void send(Player p, Packet packet) 
	{
		int parent = packet.readShort();
		int child = packet.readShort();
		short slot = (short)packet.readShort();

		switch (parent)
		{
			case 271:
				slot = (short)((child - 5) * 0.5);
				if (p.skills().mainLevels[5] < Prayer.levelRequirement(slot) || (p.skills().levels[5] <= 0))
					return;
				short config = p.prayer().setPrayer(slot);
				if (!p.prayer().isOnline(slot))
				{
					p.packetDispatcher().sendConfig(config, 0);
				} else {
					boolean deactivated = false;
					for (byte icon : Prayer.deactivates(slot))
					{
						if (p.prayer().isOnline(icon))
						{
							p.packetDispatcher().sendConfig(p.prayer().setPrayer(icon), 0);
							deactivated = true;
						}
					}
					//if (deactivated)
					p.packetDispatcher().sendConfig(config, 1);
				}
				p.prayer().execute(p);
			break;
			case 548:
				if (child == 24)
				{
					p.packetDispatcher().sendInterface(553);
				}
			case 81:
				if (child == 10)
				{
					p.special().reverseSpec();
				}
			case 335:
				if (child == 18 || child == 7)
					p.trade().declineTrade(true);
				else if (child == 49 && slot > -1 && slot < 28)
					p.trade().removeItem(slot, 1);
				else if (child == 17)
				{
					p.trade().acceptFirstTradeWindow();
				}
			break;
			case 107:	
				if (child == 49)
				{
					p.packetDispatcher().sendEnterInterface(109, "Enter Username: ");					
				} else if (child == 47)
				{
					p.packetDispatcher().sendEnterInterface(110, "Enter Password: ");
				}
			break;
			case 336:
				if (child == 0 && slot > -1 && slot < 28)
					p.trade().offerItem(slot, 1);
			break;
			case 334:
				if (child == 21 || child == 6)
					p.trade().declineTrade(true);
				else if (child == 20)
					p.trade().acceptSecondTradeWindow();
			break;
			case 589:
				if (child == 9)
				{
					p.packetDispatcher().sendInterface(590);
					PlayerChat chat = p.chat();
					if (chat.myClanDisplayName != 0)
					{
						p.packetDispatcher().sendInterfaceString(GameUtility.longToString(chat.myClanDisplayName), 590, 32);
						
						p.packetDispatcher().sendInterfaceString(chat.RANKS[chat.enterRank + 1], 590, 33);
						p.packetDispatcher().sendInterfaceString(chat.RANKS[chat.speakerRank + 1], 590, 34);
						p.packetDispatcher().sendInterfaceString(chat.RANKS[chat.kickRank + 1], 590, 35);
						p.packetDispatcher().sendInterfaceString(chat.RANKS[chat.lootShareRank + 1], 590, 36);
					}
				}
			break;
			case 590:
				if (child == 32)
					p.packetDispatcher().sendEnterInterface(109, "Enter Clan Display Name: ");
				if (child == 33)
				{
					p.chat().enterRank = -1;
					p.packetDispatcher().sendInterfaceString("Anyone", 590, 33);
					p.chat().initiateClanChatUpdate();
				} else if (child == 34) {
					p.chat().speakerRank = -1;
					p.packetDispatcher().sendInterfaceString("Anyone", 590, 34);
					p.chat().initiateClanChatUpdate();
				} else if (child == 36) {
					p.chat().lootShareRank = 8;
					p.packetDispatcher().sendInterfaceString("Disabled", 590, 36);
					p.chat().initiateClanChatUpdate();
				}
			break;
			
		}
	}
}
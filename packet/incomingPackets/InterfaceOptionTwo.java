/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.ShopManagement;

public class InterfaceOptionTwo implements PacketAssistant {
	
	public InterfaceOptionTwo() {

	}

	public void send(Player p, Packet packet){
		int parent = packet.readShort();
		int child = packet.readShort();
		int slot = packet.readShort();

		if (slot < 0)
			return;

		switch (parent)
		{
			case 336:
				if (child == 0 && slot < 28)
					p.trade().offerItem(slot, 5);
			break;
			case 335:
				if (child == 49 && slot < 28)
					p.trade().removeItem(slot, 5);
			break;
			case 620:
				if (child == 23 || child == 24)
					ShopManagement.buy(p, slot, 1);
			break;
			case 621:
				if (child == 0 && slot < 28)
					ShopManagement.sell(p, slot, 1);
			break;

			case 590:
				if (child == 32)
				{
					p.chat().myClanDisplayName = 0;
					p.packetDispatcher().sendInterfaceString("Chat Disabled", 590, 32);
					p.chat().initiateClanChatUpdate();
				} else if (child == 33)
				{
					p.chat().enterRank = 0;
					p.packetDispatcher().sendInterfaceString("Any Friends", 590, 33);
					p.chat().initiateClanChatUpdate();
				} else if (child == 34) {
					p.chat().speakerRank = 0;
					p.packetDispatcher().sendInterfaceString("Any Friends", 590, 34);
					p.chat().initiateClanChatUpdate();
				} else if (child == 36) {
					p.chat().lootShareRank = 0;
					p.packetDispatcher().sendInterfaceString("Any Friends", 590, 36);
					p.chat().initiateClanChatUpdate();
				}
			break;
		}
		p.packetDispatcher().sendMessage("[BUTTON #2] - Parent: "+parent+" Child: "+child+" Slot: "+slot);		
	}
}
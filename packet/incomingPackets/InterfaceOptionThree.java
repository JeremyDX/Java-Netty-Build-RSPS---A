/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.ShopManagement;

public class InterfaceOptionThree implements PacketAssistant {
	
	public InterfaceOptionThree() {

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
					p.trade().offerItem(slot, 10);
			break;
			case 335:
				if (child == 49 && slot < 28)
					p.trade().removeItem(slot, 10);
			break;
			case 620:
				if (child == 23 || child == 24)
					ShopManagement.buy(p, slot, 5);
			break;
			case 621:
				if (child == 0 && slot < 28)
					ShopManagement.sell(p, slot, 5);
			break;
			case 590:
				if (child == 33)
				{
					p.chat().enterRank = 1;
					p.packetDispatcher().sendInterfaceString("Recruit", 590, 33);
					p.chat().initiateClanChatUpdate();
				} else if (child == 34) {
					p.chat().speakerRank = 1;
					p.packetDispatcher().sendInterfaceString("Recruit", 590, 34);
					p.chat().initiateClanChatUpdate();
				} else if (child == 36) {
					p.chat().lootShareRank = 1;
					p.packetDispatcher().sendInterfaceString("Recruit", 590, 36);
					p.chat().initiateClanChatUpdate();
				}
			break;
		}
		p.packetDispatcher().sendMessage("[BUTTON #3] - Parent: "+parent+" Child: "+child+" Slot: "+slot);		
	}
}
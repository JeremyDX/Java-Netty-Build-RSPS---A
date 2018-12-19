/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.ShopManagement;

public class InterfaceOptionFour implements PacketAssistant {
	
	public InterfaceOptionFour() {

	}

	public void send(Player p, Packet packet){
		int parent = packet.readShort();
		int child = packet.readShort();
		int slot = packet.readShort();

		switch (parent)
		{
			case 336:
				if (child == 0 && slot > -1 && slot < 28)
					p.trade().offerItem(slot, Integer.MAX_VALUE);
			break;
			case 335:
				if (child == 49 && slot > -1 && slot < 28)
					p.trade().removeItem(slot, Integer.MAX_VALUE);
			break;
			case 620:
				if (child == 23 || child == 24)
					ShopManagement.buy(p, slot, 10);
			break;
			case 621:
				if (child == 0)
					ShopManagement.sell(p, slot, 10);
			break;
			case 590:
			 	if (child == 33)
				{
					p.chat().enterRank = 2;
					p.packetDispatcher().sendInterfaceString("Corporal", 590, 33);
					p.chat().initiateClanChatUpdate();
				} else if (child == 34) {
					p.chat().speakerRank = 2;
					p.packetDispatcher().sendInterfaceString("Corporal", 590, 34);
					p.chat().initiateClanChatUpdate();
				} else if (child == 35) {
					p.chat().kickRank = 2;
					p.packetDispatcher().sendInterfaceString("Corporal", 590, 35);
					p.chat().initiateClanChatUpdate();
				} else if (child == 36) {
					p.chat().lootShareRank = 2;
					p.packetDispatcher().sendInterfaceString("Corporal", 590, 36);
					p.chat().initiateClanChatUpdate();
				}
			break;
		}
		//p.packetDispatcher().sendMessage("[BUTTON #4] - Parent: "+parent+" Child: "+child+" Slot: "+slot);		
	}
}
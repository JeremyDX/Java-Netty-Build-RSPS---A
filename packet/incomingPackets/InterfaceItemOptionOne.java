/*
* @ Author - Digistr
* @ info - Withdraw 1 + Store 1 for banking, unequip item.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceItemOptionOne implements PacketAssistant {
	
	public InterfaceItemOptionOne() {

	}

	public void send(Player p, Packet packet){
		int slot = packet.readShort();
		int child = packet.readLEShort();
		int parent = packet.readLEShort();
		int itemId = packet.readLEShortA();
		switch (parent) {
			case 12:
				if (slot > -1 && slot < 400)
					p.bank().withdraw(slot, 1);
			break;
			case 15:
				if (slot > -1 && slot < 28)
					p.bank().deposit(slot, 1);
			break;
			case 387:
				if (slot > -1 && slot < 14)
					p.equipment().unwield(slot);
			break;					
			default:
				p.packetDispatcher().sendMessage("[ItemOpt#1] - Item: "+itemId+" Slot: "+slot+" Parent: "+parent+" Child: "+child);
			break;
		}
	}
}
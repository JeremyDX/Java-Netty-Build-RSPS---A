/*
* @ Author - Digistr
* @ info - Withdraw All + Store All for banking.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceItemOptionFour implements PacketAssistant {
	
	public InterfaceItemOptionFour() {

	}

	public void send(Player p, Packet packet){
		int slot = packet.readShort();
		int parent = packet.readLEShort();
		int child = packet.readLEShort();
		int itemId = packet.readLEShort();
		switch (parent) {
			case 12:
				if (slot > -1 && slot < 400)
					p.bank().withdraw(slot, Integer.MAX_VALUE);
			break;
			case 15:
				if (slot > -1 && slot < 28)
					p.bank().deposit(slot, Integer.MAX_VALUE);
			break;
			default:
				p.packetDispatcher().sendMessage("[ItemOpt#4] - Item: "+itemId+" Slot: "+slot+" Parent: "+parent+" Child: "+child);
			break;
		}
	}
}
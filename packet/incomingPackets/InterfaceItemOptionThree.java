/*
* @ Author - Digistr
* @ info - Withdraw 10 + Store 10 for banking.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceItemOptionThree implements PacketAssistant {
	
	public InterfaceItemOptionThree() {

	}

	public void send(Player p, Packet packet) {
		int itemId = packet.readLEShortA();
		int child = packet.readShort();
		int parent = packet.readShort();
		int slot = packet.readShort();
		switch (parent) {
			case 12:
				if (slot > -1 && slot < 400)
					p.bank().withdraw(slot, 10);
			break;
			case 15:
				if (slot > -1 && slot < 28)
					p.bank().deposit(slot, 10);
			break;
			default:
				p.packetDispatcher().sendMessage("[ItemOpt#3] - Item: "+itemId+" Slot: "+slot+" Parent: "+parent+" Child: "+child);
			break;
		}
	}
}
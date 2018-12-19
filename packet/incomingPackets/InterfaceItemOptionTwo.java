/*
* @ Author - Digistr
* @ info - Withdraw 5 + Store 5 for banking , Item Operate.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceItemOptionTwo implements PacketAssistant {
	
	public InterfaceItemOptionTwo() {

	}

	public void send(Player p, Packet packet){
		int slot = packet.readShortA();
		int parent = packet.readShort();
		int child = packet.readShort();
		int itemId = packet.readShort();
		switch (parent) {
			case 12:
				if (slot > -1 && slot < 400)
					p.bank().withdraw(slot,5);
			break;
			case 15:
				if (slot > -1 && slot < 28)
					p.bank().deposit(slot,5);
			break;
			default:
				p.packetDispatcher().sendMessage("[ItemOpt#2] - Item: "+itemId+" Slot: "+slot+" Parent: "+parent+" Child: "+child);
			break;
		}
	}
}
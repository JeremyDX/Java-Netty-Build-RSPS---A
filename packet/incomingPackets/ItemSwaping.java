/*
* @ Author - Digistr
* @ info - This handles the following: Moving Items in inventory and bank.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class ItemSwaping implements PacketAssistant {
	
	public ItemSwaping() {

	}

	public void send(Player plr, Packet packet){
		int toPosition = packet.readLEShortA(); 
		int type = packet.readByteA();
		int parent = packet.readLEShort(); 
		int child = packet.readLEShort();
		int fromPosition = packet.readLEShort();
		plr.packetDispatcher().sendMessage("Parent: "+parent+" Child: "+child+" Type: "+type+" From: "+fromPosition+" To: "+toPosition);		
		switch (parent) {
			case 149:
			case 15:
				plr.inventory().swap(fromPosition,toPosition);
				break;
			case 12:
				plr.bank().swap(fromPosition,toPosition);
				break;
	
		}
	}
}
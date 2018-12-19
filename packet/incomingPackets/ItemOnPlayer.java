/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class ItemOnPlayer implements PacketAssistant {

	public ItemOnPlayer() {

	}

	public void send(Player p, Packet packet){
		int item = packet.readShort(); //useful.
		int index = packet.readShortA(); //Important.
		int child = packet.readShort(); //useful.
		int parent = packet.readShort(); //not useful.
		int slot = packet.readShortA(); //Important.
		p.packetDispatcher().sendMessage("Item: " + item + " Used On Player IDX: " + index + " From Slot: " + slot);
	}
}
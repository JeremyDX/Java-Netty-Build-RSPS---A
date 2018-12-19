/*
* @ Author - Digistr
* @ info - This handles the following: Using an inventory item on another inventory item.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class ItemOnItem implements PacketAssistant {
	
	public ItemOnItem() {

	}

	public void send(Player p, Packet packet){
		p.packetDispatcher().sendMessage("You used one of your items on another item.");		
	}
}
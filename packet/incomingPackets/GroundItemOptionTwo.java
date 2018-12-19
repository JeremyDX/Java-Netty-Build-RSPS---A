/*
* @ Author - Digistr
* @ info - This handles the following: Using an inventory item on another inventory item.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class GroundItemOptionTwo implements PacketAssistant {
	
	public GroundItemOptionTwo() {

	}

	public void send(Player p, Packet packet){
		p.packetDispatcher().sendMessage("Ground Item Option #2.");		
	}
}
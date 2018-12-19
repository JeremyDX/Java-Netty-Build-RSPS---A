/*
* @ Author - Digistr
* @ info - This handles the following: Dismantling Inventory Items Like Godswords.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InventoryOptionThree implements PacketAssistant {
	
	public InventoryOptionThree() {

	}

	public void send(Player p, Packet packet){
		p.packetDispatcher().sendMessage("Inventory Option # 3 Recieved.");		
	}
}
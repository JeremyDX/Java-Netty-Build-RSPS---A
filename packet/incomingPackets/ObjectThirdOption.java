/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class ObjectThirdOption implements PacketAssistant {
	
	public ObjectThirdOption() {

	}

	public void send(Player p, Packet packet){
		p.packetDispatcher().sendMessage("Object Option # 3");
	}
}
/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class ObjectExamine implements PacketAssistant {
	
	public ObjectExamine() {

	}

	public void send(Player p, Packet packet){
		int id = packet.readLEShort();
		p.packetDispatcher().sendMessage("Examined Object: " + id);
	}
}
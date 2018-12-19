/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class ObjectFifthOption implements PacketAssistant {
	
	public ObjectFifthOption() {

	}

	public void send(Player p, Packet packet){
		p.packetDispatcher().sendMessage("Object Option # 5");
	}
}
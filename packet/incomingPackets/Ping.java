/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class Ping implements PacketAssistant {

	public Ping() {

	}

	public void send(Player p, Packet packet){
		p.packetDispatcher().sendPingPacket();
	}
}
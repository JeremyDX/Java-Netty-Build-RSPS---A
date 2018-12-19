/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class NpcOption1 implements PacketAssistant {
	
	public NpcOption1() {

	}

	public void send(Player p, Packet packet)
	{
		int index = packet.readLEShort();
		p.packetDispatcher().sendMessage("[NPC] Index: " + index);
	}
}
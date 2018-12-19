/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class CloseInterface implements PacketAssistant {
	
	public CloseInterface() {

	}

	public void send(Player p, Packet packet) 
	{
		p.interfaceContainer().resetInterfaces(p, true);
	}
}
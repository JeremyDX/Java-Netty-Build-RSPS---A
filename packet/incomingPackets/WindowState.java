/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class WindowState implements PacketAssistant {
	
	public WindowState() {

	}

	public void send(Player p, Packet packet){
		boolean viewed = packet.readByte() == 1;
	}
}
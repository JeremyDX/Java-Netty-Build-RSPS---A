/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class Appearence implements PacketAssistant {
	
	public Appearence() {

	}

	public void send(Player p, Packet packet){
		short[] looks = new short[packet.capacity()];
		for (int i = 0; i < looks.length; i++) {
			looks[i] = (short)packet.readByte();
		}
		for (int i = 0; i < looks.length; i++) {
			System.out.print(", " + (looks[i] + 256));
		}
		p.updateFlags().setLooks(looks);
		p.packetDispatcher().sendCloseInterface(77);
	}
}
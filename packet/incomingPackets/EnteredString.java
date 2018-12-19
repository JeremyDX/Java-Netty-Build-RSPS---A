/*
* @ Author - Digistr
* @ info - This packet is unknown but is probably used when you need to type a message.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class EnteredString implements PacketAssistant {

	public EnteredString() {

	}

	public void send(Player p, Packet packet){
		String input = packet.readString();
		System.out.println("String Input: "+input);
	}
}
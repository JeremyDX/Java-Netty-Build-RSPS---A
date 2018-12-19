/*
* @ Author - Digistr
* @ info - this class sends 10 bytes but we only need the first 3 so ignore the rest.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class ButtonTypesChanged implements PacketAssistant {
	
	public ButtonTypesChanged() {

	}

	public void send(Player p, Packet packet){
		byte chatPublic = (byte)packet.readByte();
		byte chatPrivate = (byte)packet.readByte();
		byte chatTrade = (byte)packet.readByte();
		p.chat().setChat(chatPublic, chatPrivate, chatTrade);
	}
}
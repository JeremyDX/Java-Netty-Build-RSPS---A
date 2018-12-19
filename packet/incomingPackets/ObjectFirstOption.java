/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class ObjectFirstOption implements PacketAssistant {
	
	public ObjectFirstOption() {

	}

	public void send(Player p, Packet packet){
		short objectX = (short)packet.readLEShortA();
		short objectId = (short)packet.readShort();
		short objectY = (short)packet.readShort(); 
		switch (objectId) {
			case 26972:
				p.bank().open();
				break;
			default:
				p.packetDispatcher().sendMessage("[OBJECT 1] - ID: " + objectId + " X: " + objectX + " Y: "+ objectY);
				break;
		}
	}
}
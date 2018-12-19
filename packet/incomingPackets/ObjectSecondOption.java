/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class ObjectSecondOption implements PacketAssistant {
	
	public ObjectSecondOption() {

	}

	public void send(Player p, Packet packet){
		short objectX = (short)packet.readShort();
		short objectId = (short)packet.readLEShort();
		short objectY = (short)packet.readShortA(); 
		switch (objectId) {
			case 26972:
				p.bank().open();
				break;
			default:
				p.packetDispatcher().sendMessage("[OBJECT 2] - ID: " + objectId + " X: " + objectX + " Y: "+ objectY);
				break;
		}
	}
}
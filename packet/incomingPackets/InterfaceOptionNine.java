/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceOptionNine implements PacketAssistant {
	
	public InterfaceOptionNine() {

	}

	public void send(Player p, Packet packet){
		int parent = packet.readShort();
		int child = packet.readShort();
		int slot = packet.readShort();
		if (parent == 590)
		{
			if (child == 33)
			{
				p.chat().enterRank = 7;
				p.packetDispatcher().sendInterfaceString("Only Me", 590, 33);
				p.chat().initiateClanChatUpdate();
			} else if (child == 34) {
				p.chat().speakerRank = 7;
				p.packetDispatcher().sendInterfaceString("Only Me", 590, 34);
				p.chat().initiateClanChatUpdate();
			} else if (child == 35) {
				p.chat().kickRank = 7;
				p.packetDispatcher().sendInterfaceString("Only Me", 590, 35);
				p.chat().initiateClanChatUpdate();
			} else if (child == 36) {
				p.chat().lootShareRank = 7;
				p.packetDispatcher().sendInterfaceString("Only Me", 590, 36);
				p.chat().initiateClanChatUpdate();
			}
		} else {
			p.packetDispatcher().sendMessage("[BUTTON #9] - Parent: "+parent+" Child: "+child+" Slot: "+slot);
		}		
	}
}
/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceOptionEight implements PacketAssistant {
	
	public InterfaceOptionEight() {

	}

	public void send(Player p, Packet packet){
		int parent = packet.readShort();
		int child = packet.readShort();
		int slot = packet.readShort() & 0xFFFF;
		if (parent == 590)
		{
			if (child == 33)
			{
				p.chat().enterRank = 6;
				p.packetDispatcher().sendInterfaceString("General", 590, 33);
				p.chat().initiateClanChatUpdate();
			} else if (child == 34) {
				p.chat().speakerRank = 6;
				p.packetDispatcher().sendInterfaceString("General", 590, 34);
				p.chat().initiateClanChatUpdate();
			} else if (child == 35) {
				p.chat().kickRank = 6;
				p.packetDispatcher().sendInterfaceString("General", 590, 35);
				p.chat().initiateClanChatUpdate();
			} else if (child == 36) {
				p.chat().lootShareRank = 6;
				p.packetDispatcher().sendInterfaceString("General", 590, 36);
				p.chat().initiateClanChatUpdate();
			}
		} else {
			p.packetDispatcher().sendMessage("[BUTTON #8] - Parent: "+parent+" Child: "+child+" Slot: "+slot);
		}	
	}
}
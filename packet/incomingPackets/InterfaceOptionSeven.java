/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceOptionSeven implements PacketAssistant {
	
	public InterfaceOptionSeven() {

	}

	public void send(Player p, Packet packet){
		int parent = packet.readShort();
		int child = packet.readShort();
		int slot = packet.readShort();
		if (parent == 590)
		{
			if (child == 33)
			{
				p.chat().enterRank = 5;
				p.packetDispatcher().sendInterfaceString("Captain", 590, 33);
				p.chat().initiateClanChatUpdate();
			} else if (child == 34) {
				p.chat().speakerRank = 5;
				p.packetDispatcher().sendInterfaceString("Captain", 590, 34);
				p.chat().initiateClanChatUpdate();
			} else if (child == 35) {
				p.chat().kickRank = 5;
				p.packetDispatcher().sendInterfaceString("Captain", 590, 35);
				p.chat().initiateClanChatUpdate();
			} else if (child == 36) {
				p.chat().lootShareRank = 5;
				p.packetDispatcher().sendInterfaceString("Captain", 590, 36);
				p.chat().initiateClanChatUpdate();
			}
		} else {
			p.packetDispatcher().sendMessage("[BUTTON #7] - Parent: "+parent+" Child: "+child+" Slot: "+slot);
		}		
	}
}
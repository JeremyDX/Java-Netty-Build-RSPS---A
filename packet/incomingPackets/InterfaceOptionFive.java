/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceOptionFive implements PacketAssistant {
	
	public InterfaceOptionFive() {

	}

	public void send(Player p, Packet packet){
		int parent = packet.readShort();
		int child = packet.readShort();
		int slot = packet.readShort() & 0xFFFF;
		if (parent == 590)
		{
			if (child == 33)
			{
				p.chat().enterRank = 3;
				p.packetDispatcher().sendInterfaceString("Sergeant", 590, 33);
				p.chat().initiateClanChatUpdate();
			} else if (child == 34) {
				p.chat().speakerRank = 3;
				p.packetDispatcher().sendInterfaceString("Sergeant", 590, 34);
				p.chat().initiateClanChatUpdate();
			} else if (child == 35) {
				p.chat().kickRank = 3;
				p.packetDispatcher().sendInterfaceString("Sergeant", 590, 35);
				p.chat().initiateClanChatUpdate();
			} else if (child == 36) {
				p.chat().lootShareRank = 3;
				p.packetDispatcher().sendInterfaceString("Sergeant", 590, 36);
				p.chat().initiateClanChatUpdate();
			}
		} else {
			p.interfaceContainer().xInfo = parent + (slot * 1024);
			p.packetDispatcher().sendEnterInterface(108, "Enter Amount:");
			p.packetDispatcher().sendMessage("[BUTTON #5] - Parent: "+parent+" Child: "+child+" Slot: "+slot);
		}	
	}
}
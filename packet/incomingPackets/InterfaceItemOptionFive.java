/*
* @ Author - Digistr
* @ info - Withdraw X + Store X for banking.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class InterfaceItemOptionFive implements PacketAssistant {
	
	public InterfaceItemOptionFive() {

	}

	public void send(Player p, Packet packet){
		int child = packet.readLEShort(); //Useless.
		int parent = packet.readLEShort(); //Vital.
		int slot = packet.readShortA(); //Vital.
		int itemId = packet.readShortA(); //Useless.
		p.interfaceContainer().xInfo = parent + (slot * 1024);
		p.packetDispatcher().sendEnterInterface(108, "Enter Amount:");
	}
}
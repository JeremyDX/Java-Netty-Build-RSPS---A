/*
* @ Author - Digistr
* @ info - This handles the following: Potions , Food , Drinks , Read Books , Setup Cannon.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

import com.model.systems.Consumables;

public class InventoryOptionTwo implements PacketAssistant {
	
	public InventoryOptionTwo() {

	}

	public void send(Player p, Packet packet) 
	{
		int item = packet.readLEShortA();
		int parent = packet.readInt();
		int child = parent & 0xFFFF;
		int slot = packet.readShortA();
		
		switch (parent)
		{
			case 9764864: //Shift 16 bits = Interface 149 = Inventory.
				Consumables.consume(p, slot, p.inventory().items[slot]);
			break;
		}
		p.packetDispatcher().sendMessage("[Inv #2] - Item ID: " + item + " ParentId " + parent + " Slot: " + slot);		
	}
}
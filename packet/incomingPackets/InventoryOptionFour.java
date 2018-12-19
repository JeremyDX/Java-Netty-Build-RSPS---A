/*
* @ Author - Digistr
* @ info - This handles the following: Destroying & Dropping Inventory Items.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.GroundItemManager;

public class InventoryOptionFour implements PacketAssistant {
	
	public InventoryOptionFour() {
		
	}

	public void send(Player p, Packet packet){
		int parent = packet.readLEInt();
		short item = (short)packet.readLEShortA();
		int slot = packet.readLEShort();
		if (slot < 0 || slot > 27)
			return;
		int amount = p.inventory().amounts[slot];
		if(p.inventory().deleteSingleItem(slot, item)) 
		{		
			GroundItemManager.add(p, item, amount, p.location().x, p.location().y, (byte)p.location().z);
		}
	}
}
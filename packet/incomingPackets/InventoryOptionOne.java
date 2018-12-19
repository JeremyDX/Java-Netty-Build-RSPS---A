/*
* @ Author - Digistr
* @ info - This handles the following: Equipping Items.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.ItemManagement;

public class InventoryOptionOne implements PacketAssistant {
	
	public InventoryOptionOne() {

	}

	public void send(Player p, Packet packet) 
	{
	 	int slot = packet.readLEShortA();
		short item = (short)packet.readShort();
		int parent = packet.readLEInt();
		if (slot < 0 || slot > 27 || item != p.inventory().items[slot]) {
			return;
		}
		int amount = p.inventory().amounts[slot];
		if (item != -1 && amount > 0)
			p.equipment().wield(item, amount, slot);
	}
}
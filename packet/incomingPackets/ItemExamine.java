/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.ItemManagement;

public class ItemExamine implements PacketAssistant {
	
	public ItemExamine() {

	}

	public void send(Player me, Packet packet) 
	{
		int itemId = packet.readLEShortA() & 0xFFFF;
		me.packetDispatcher().sendMessage("Examine Item " + itemId + " , Game Index: " + ItemManagement.GAME_ITEMS[itemId]);	
	}
}
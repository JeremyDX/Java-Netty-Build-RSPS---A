/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.model.World;
import com.util.ItemManagement;

public class Button implements PacketAssistant 
{	
	public Button() 
	{

	}

	public void send(Player me, Packet packet){
		int parentId = packet.readShort();
		int childId = packet.readShort();
		if ((parentId > 74 && parentId < 94) || parentId == 473 || parentId == 474) 
		{
			if (parentId == 90 && childId > 3)
				return;
			if (childId > 0 && childId < 6)
				me.fightType().pressed(parentId, childId);
			return;
		}
		switch (parentId) 
		{
			case 12:
				if (childId == 98) {
					me.bank().swap = true;
				} else if (childId == 99) { 
					me.bank().swap = false;
				} else if (childId == 92) {
					me.bank().note = true;
				} else if (childId == 93) {
					me.bank().note = false;
				}
				break;
			case 261:
				if (childId == 0)
					me.walkingQueue().running = !me.walkingQueue().running;
				break;
			case 387:
				if (childId == 50) {
					long t1 = System.nanoTime();
					me.itemsOnDeath().open();
					long t2 = System.nanoTime();
					me.packetDispatcher().sendMessage("Spent: " + (t2-t1) + " Calculating/Opening Items On Death.");
				} else if (childId == 51) {
					me.equipment().open();
				}	
				break;
			case 378:
				me.packetDispatcher().sendWindowPane(548);
				if ((me.details().STATUS & 0x1) == 0)
					me.packetDispatcher().sendInterface(0, 548, 269, 77);
				break;
			case 182:
				if (me.isDisconnectable() && (me.details().STATUS & 0x8) == 0x0) 
				{
					me.details().STATUS |= 0x8; //Set Disconnecting Process TRUE.
					World.addToLogoutQueue(me);
				} else {
					me.packetDispatcher().sendMessage("You must wait 10 seconds before logging out from combat.");
				}
				break;
			default:
				break;
		}
	}
}
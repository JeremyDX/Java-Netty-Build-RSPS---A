/*
* @ Author - Digistr
* @ info - This handles picking up items off the ground option one.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.Task;
import com.util.TaskQueue;
import com.util.GroundItemManager;

public class GroundItemOptionOne implements PacketAssistant {
	
	public GroundItemOptionOne() {

	}

	public void send(final Player p, Packet packet){
		final short item = (short)packet.readShortA();
		short itemX = (short)packet.readLEShort();
		short itemY = (short)packet.readLEShort();
		if (itemX == p.location().x && itemY == p.location().y) {
			int slot = GroundItemManager.getItemIndex(p.details().USERNAME_AS_LONG, item, itemX, itemY, p.location().z);
			if (slot == -1)
				return;
			int checkedSlot = -1;
			int amount = GroundItemManager.getAmount(slot);
			if ((checkedSlot = p.inventory().check(item, amount)) != -1) {
				GroundItemManager.sendPickup(slot);
				p.inventory().addNoChecks(checkedSlot, item, amount);
			}
			p.inventory().pickupId = -1;
			p.inventory().pickupX = -1;
			p.inventory().pickupY = -1;
		} else if (p.inventory().pickupId == -1) {
				p.inventory().pickupId = item;
				p.inventory().pickupX = itemX;
				p.inventory().pickupY = itemY;
				if (p.ground_item_task.listed())
					return;
				p.ground_item_task = new Task(1, true) {
					@Override
					public void execute() {
						int slot = GroundItemManager.getItemIndex(p.details().USERNAME_AS_LONG, p.inventory().pickupId, p.inventory().pickupX, p.inventory().pickupY, p.location().z);
						if (slot == -1) {
							die();
							p.inventory().pickupId = -1;
							p.inventory().pickupX = -1;
							p.inventory().pickupY = -1;
						} else if (p.inventory().pickupX == p.location().x && p.inventory().pickupY == p.location().y) {
							int checkedSlot = -1;
							int amount = GroundItemManager.getAmount(slot);
							if ((checkedSlot = p.inventory().check(item, amount)) != -1) {
								GroundItemManager.sendPickup(slot);
								p.inventory().addNoChecks(checkedSlot, item, amount);
							}
						}
					}
				};
				TaskQueue.add(p.ground_item_task);
		} else {
			p.inventory().pickupId = item;
			p.inventory().pickupX = itemX;
			p.inventory().pickupY = itemY;
		
		}
	}
}
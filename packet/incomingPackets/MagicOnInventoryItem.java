/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.ItemManagement;
import com.util.GroundItemManager;

public class MagicOnInventoryItem implements PacketAssistant {

	public MagicOnInventoryItem() {

	}

	public void send(Player p, Packet packet){
		int inventoryInterface = packet.readLEShort();
		int type = packet.readShort();
		int item = packet.readLEShort();
		packet.skipBytes(2);
		int slot = packet.readShort();
		int spell = packet.readInt();
		if ((slot & 0x1F) < 28 && p.inventory().amounts[slot] > 0)
			return;
		switch(spell) {
			case 12582925:
				if (item == 995) {
					p.packetDispatcher().sendMessage("Coins are already made of gold.");
					return;
				}
				int cashSlot = p.inventory().getNextItemSlot(0, 995);
				int alchReturn = ItemManagement.lowAlchValue(p.inventory().items[slot]);
				if (--p.inventory().amounts[slot] == 0) {
					if (cashSlot == -1) {
						p.inventory().items[slot] = 995;
						p.inventory().amounts[slot] = alchReturn;
					} else {
						p.inventory().items[slot] = -1;
						if (p.inventory().items[cashSlot] == -1)
							p.inventory().items[cashSlot] = 995;
						p.inventory().amounts[cashSlot] += alchReturn;						
						p.packetDispatcher().sendSingleItem(149, 0, 93, cashSlot, p.inventory().items[cashSlot], p.inventory().amounts[cashSlot]);					
					}		
				} else {
					if (cashSlot == -1 || ((long)p.inventory().amounts[cashSlot] + alchReturn > Integer.MAX_VALUE)) {
						GroundItemManager.add(p, (short)995, alchReturn, p.location().x, p.location().y, (byte)p.location().z);
					} else {	
						p.inventory().amounts[cashSlot] += alchReturn;
						if (p.inventory().items[cashSlot] == -1)
							p.inventory().items[cashSlot] = 995;					
						p.packetDispatcher().sendSingleItem(149, 0, 93, cashSlot, p.inventory().items[cashSlot], p.inventory().amounts[cashSlot]);					
					}
				}
				p.skills().addSkillExp(6, 650);
				p.packetDispatcher().sendSkill(6);
				//p.setAction((byte)2);
				p.packetDispatcher().sendSingleItem(149, 0, 93, slot, p.inventory().items[slot], p.inventory().amounts[slot]);
				p.updateFlags().setGraphic((short)112, 0, 80);
				p.updateFlags().setAnimation(712, 0);
				break;
			case 12582946:
				if (item == 995) {
					p.packetDispatcher().sendMessage("Coins are already made of gold.");
					return;
				}
				cashSlot = p.inventory().getNextItemSlot(0, 995);
				alchReturn = ItemManagement.highAlchValue(p.inventory().items[slot]);
				if (--p.inventory().amounts[slot] == 0) {
					if (cashSlot == -1) {
						p.inventory().items[slot] = 995;
						p.inventory().amounts[slot] = alchReturn;
					} else {
						p.inventory().items[slot] = -1;
						if (p.inventory().items[cashSlot] == -1)
							p.inventory().items[cashSlot] = 995;
						p.inventory().amounts[cashSlot] += alchReturn;						
						p.packetDispatcher().sendSingleItem(149, 0, 93, cashSlot, p.inventory().items[cashSlot], p.inventory().amounts[cashSlot]);					
					}		
				} else {
					if (cashSlot == -1 || ((long)p.inventory().amounts[cashSlot] + alchReturn > Integer.MAX_VALUE)) {
						GroundItemManager.add(p, (short)995, alchReturn, p.location().x, p.location().y, (byte)p.location().z);
					} else {	
						p.inventory().amounts[cashSlot] += alchReturn;
						if (p.inventory().items[cashSlot] == -1)
							p.inventory().items[cashSlot] = 995;					
						p.packetDispatcher().sendSingleItem(149, 0, 93, cashSlot, p.inventory().items[cashSlot], p.inventory().amounts[cashSlot]);					
					}
				}
				p.skills().addSkillExp(6, 1300);
				p.packetDispatcher().sendSkill(6);
				//p.setAction((byte)4);
				p.packetDispatcher().sendSingleItem(149, 0, 93, slot, p.inventory().items[slot], p.inventory().amounts[slot]);
				p.updateFlags().setGraphic((short)113, 0, 80);
				p.updateFlags().setAnimation(713, 0);
				break;
		}
		p.packetDispatcher().sendMessage("Spell: " + spell + " Item: " + item);
	}
}
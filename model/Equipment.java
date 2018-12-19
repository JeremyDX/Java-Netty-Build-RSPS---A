package com.model;

import com.util.ItemManagement;
import com.util.GroundItemManager;

public class Equipment {

	private static final String[] BONUS = {"Stab:","Slash:","Crush:","Magic:","Range:","Stab:","Slash:","Crush:","Magic:","Range:","Strength:","Prayer:"};

	private short index;
	public short[] bonus = new short[11];
	public short[] items = new short[14];
	public int[] amounts = new int[14];

	protected Equipment() {
		for (int i = 0; i < items.length; i++)
			items[i] = -1;
	}

	public void release(long user, Location loc, int[] keepItems) 
	{
		short item;
		int amount;

		Player dropuser = World.getPlayerByName(user);

		Player player = World.getPlayerByClientIndex(index);
		byte[] locations = player.itemsOnDeath().itemKeptLocations;

		for (int i = 0; i < keepItems.length; i++)
		{
			if (locations[i] != -1 && (locations[i] - 28) >= 0)
			{
				amounts[(locations[i] - 28)] -= 1;
				if (amounts[(locations[i] - 28)] == 0)
					items[(locations[i] - 28)] = -1;
				System.out.println("Keep Item[" + i + "]: " + keepItems[i]);
			}
		}

		for (int i = 0; i < 14; ++i) 
		{
			if (items[i] != -1) 
			{
				item = items[i];
				amount = amounts[i];
				items[i] = -1;
				amounts[i] = 0;
				GroundItemManager.add(dropuser, item, amount, loc.x, loc.y, (byte)loc.z);
			}
		}
	}

	public void setIndex(short index) {
		this.index = index;
	}

	public void erase(int slot)
	{
		items[slot] = -1;
		amounts[slot] = 0;
		Player p = World.getPlayerByClientIndex(index);
		p.packetDispatcher().sendSingleItem(387, 28, 93, slot, -1, 0);
		p.updateFlags().forceAppearence();
	}

/*
* @Usage - Provides the player w/ temporary equipment that doesn't really exist, but is visible like it does.
*/
	public void sendFakeEquipment(int item, int amount, int slot)
	{
		items[slot] = (short)item;
		amounts[slot] = amount;
		Player p = World.getPlayerByClientIndex(index);
		p.packetDispatcher().sendSingleItem(387, 28, 93, slot, item, amount);
		p.updateFlags().forceAppearence();
	}

	public void wield(short item, int amount, int slot) 
	{
		byte wieldLocation = ItemManagement.getWieldLocation(item);
		if (wieldLocation == -1) {
			return;
		}		
		short equipItem = items[wieldLocation];
		int equipAmt = amounts[wieldLocation];
		boolean invStack = ItemManagement.isArrayStackable(item);
		boolean equipStack = false;
		if (equipItem > 0)
			equipStack = ItemManagement.isArrayStackable(equipItem);
		boolean equals = item == equipItem;
		if (!invStack && equals)
			return;
		Player p = World.getPlayerByClientIndex(index);
		if (invStack && equals) {
			if ((long) equipAmt + amount > Integer.MAX_VALUE) {
				p.packetDispatcher().sendMessage("You don't have enough inventory space.");
				return;
			}
			amounts[wieldLocation] += amount;
			p.inventory().items[slot] = -1;
			p.inventory().amounts[slot] = 0;
			++p.inventory().availableInventorySpace;
		} else if (wieldLocation == 5 && ItemManagement.isTwoHanded(items[3])) {
			p.inventory().items[slot] = items[3];
			p.inventory().amounts[slot] = amounts[3];
			items[3] = -1;
			amounts[3] = 0;
			items[wieldLocation] = item;
			amounts[wieldLocation] = amount;
			p.packetDispatcher().sendSingleItem(387, 28, 93, 3, -1, 0);
			p.special().sendSpecialBar(92);
			p.packetDispatcher().sendTab(92, 99);
			p.packetDispatcher().sendInterfaceString("Unarmed", 92, 0);
			p.packetDispatcher().sendInterfaceString("Combat Level: " + p.skills().combatLevel, 92, 23);
			p.packetDispatcher().sendConfig(43, p.fightType().box);
			p.fightType().wield(92);
		} else if (ItemManagement.isTwoHanded(item) && items[5] != -1) {
			if (items[3] != -1) {
				if (equipStack) {
					int availSlot = p.inventory().getFirstItemSlot(equipItem);
					if (availSlot == -1) {
						availSlot = p.inventory().getNextSlot(0);
						if (availSlot == -1) {
							p.packetDispatcher().sendMessage("You don't have enough inventory space.");
							return;
						}
						p.inventory().items[availSlot] = items[5];
						p.inventory().amounts[availSlot] = amounts[5];
						p.inventory().items[slot] = equipItem;
						p.inventory().amounts[slot] = equipAmt;
						--p.inventory().availableInventorySpace;
					} else {
						if ((long) equipAmt + p.inventory().amounts[availSlot] > Integer.MAX_VALUE) {
							p.packetDispatcher().sendMessage("You don't have enough inventory space.");
							return;
						}
						p.inventory().items[slot] = items[5];
						p.inventory().amounts[slot] = amounts[5];
						p.inventory().amounts[availSlot] += equipAmt;	
					}			
					p.packetDispatcher().sendSingleItem(149, 0, 93, availSlot, p.inventory().items[availSlot], p.inventory().amounts[availSlot]);
				} else {
					int availSlot = p.inventory().getNextSlot(0);
					if (availSlot == -1) {
						p.packetDispatcher().sendMessage("You don't have enough inventory space.");
						return;
					}
					p.inventory().items[availSlot] = items[5];
					p.inventory().amounts[availSlot] = amounts[5];
					p.inventory().items[slot] = equipItem;
					p.inventory().amounts[slot] = equipAmt;
					--p.inventory().availableInventorySpace;
					p.packetDispatcher().sendSingleItem(149, 0, 93, availSlot, p.inventory().items[availSlot], p.inventory().amounts[availSlot]);
				}
			} else {
				p.inventory().items[slot] = items[5];
				p.inventory().amounts[slot] = amounts[5];
			}
			items[3] = item;
			amounts[3] = amount;
			items[5] = -1;
			amounts[5] = 0;
			p.packetDispatcher().sendSingleItem(387, 28, 93, 5, -1, 0);
		} else if (equipStack) {
			int search = p.inventory().getNextSlot(0, equipItem);
			if (search == -1) {
				p.inventory().items[slot] = equipItem;
				p.inventory().amounts[slot] = equipAmt;
			} else {
				if ((long)p.inventory().amounts[search] + equipAmt > Integer.MAX_VALUE) {
					p.packetDispatcher().sendMessage("You don't have enough inventory space.");
					return;
				}
				p.inventory().items[slot] = -1;
				p.inventory().amounts[slot] = 0;
				p.inventory().items[search] = equipItem;
				p.inventory().amounts[search] += equipAmt;
				++p.inventory().availableInventorySpace;
				p.packetDispatcher().sendSingleItem(149, 0, 93, search, p.inventory().items[search], p.inventory().amounts[search]);
			}
			items[wieldLocation] = item;
			amounts[wieldLocation] = amount;
		} else {
			if (equipItem == -1)
				++p.inventory().availableInventorySpace;
			p.inventory().items[slot] = equipItem;
			p.inventory().amounts[slot] = equipAmt;
			items[wieldLocation] = item;
			amounts[wieldLocation] = amount;
		}
		p.packetDispatcher().sendSingleItem(149, 0, 93, slot, p.inventory().items[slot], p.inventory().amounts[slot]);
		p.packetDispatcher().sendSingleItem(387, 28, 93, wieldLocation, items[wieldLocation], amounts[wieldLocation]);
		if (wieldLocation < 12) {
			if (wieldLocation == 3) {
				int sidebar = ItemManagement.getSidebarInterface(item);
				p.fightType().wield(sidebar);
				p.packetDispatcher().sendTab(sidebar, 99);
				p.special().sendSpecialBar(sidebar);
				p.combat().stopAttacking();
				//p.packetDispatcher().sendInterfaceString("Wield ID: " + item, sidebar, 0);
				//p.packetDispatcher().sendInterfaceString("Combat Level: " + p.skills().combatLevel, sidebar, 23);
				//p.packetDispatcher().sendConfig(43, p.fightType().box);
			}
			p.updateFlags().forceAppearence();
		}
	}

	public void unwield(int wieldLocation) {
		if (items[wieldLocation] < 0)
			return;
		Player p = World.getPlayerByClientIndex(index);
		int availableSpot = -1;
		if (ItemManagement.isArrayStackable(items[wieldLocation])) {
			availableSpot = p.inventory().getNextItemSlot(0, items[wieldLocation]);	
		} else {
			availableSpot = p.inventory().getNextSlot(0);
		}
		if (availableSpot == -1) {
			p.packetDispatcher().sendMessage("You don't have enough inventory space.");
			return;		
		}
		if (p.inventory().addExistingItem(items[wieldLocation], availableSpot, amounts[wieldLocation])) {
			items[wieldLocation] = -1;
			amounts[wieldLocation] = 0;
		} else {
			p.packetDispatcher().sendMessage("You don't have enough inventory space.");
			return;
		}
		if (wieldLocation < 12) {
			if (wieldLocation == 3) {
				p.special().sendSpecialBar(92);
				p.packetDispatcher().sendTab(92, 99);
				p.fightType().wield(92);
				p.packetDispatcher().sendInterfaceString("Unarmed", 92, 0);
				p.packetDispatcher().sendInterfaceString("Combat Level: " + p.skills().combatLevel, 92, 23);
				p.packetDispatcher().sendConfig(43, p.fightType().box);
			}
			p.updateFlags().forceAppearence();
		}
		p.packetDispatcher().sendSingleItem(387, 28, 93, wieldLocation, -1, 0);
	}

	public void open() {
		Player p = World.getPlayerByClientIndex(index);
		int index = 0;
		for (int i = 108; i < 118; ++i)
			p.packetDispatcher().sendInterfaceString(BONUS[index] + (bonus[index] >= 0 ? "+" : "-") + bonus[index], 465, i);
		p.packetDispatcher().sendInterfaceString(BONUS[index] + (bonus[index] >= 0 ? "+" : "-") + bonus[index], 465, 119);
		p.packetDispatcher().sendInterfaceString(BONUS[index] + (bonus[index] >= 0 ? "+" : "-") + bonus[index], 465, 120);
		p.packetDispatcher().sendMultiItems(-1, 1, 93, new short[] {1048, 1040}, new int[] {93, 2});
		p.packetDispatcher().sendMultiItems(465, 103, 95, p.equipment().items, p.equipment().amounts);
		p.packetDispatcher().setInterfaceOptions(1026, 336, 0, 0, 28);
		p.packetDispatcher().sendInterfaceScript(149, new Object[] {"Wear", 7, 4, 93, 22020096}, "iiiis");
		p.packetDispatcher().sendHideTabs(336);
		p.packetDispatcher().sendInterface(465);
	}
}
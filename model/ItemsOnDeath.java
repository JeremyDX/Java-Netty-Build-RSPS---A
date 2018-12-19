package com.model;

import com.util.ItemManagement;

public class ItemsOnDeath {

	private short index;
	public byte itemKeptLocations[] = new byte[4];
	
	public ItemsOnDeath() { 
		for (int i = 0; i < 4; ++i)
			itemKeptLocations[i] = -1;
	}

	public void setIndex(short index) {
		this.index = index;
	}

	public void scripts() {

	}

	int testId = 0;

	public void open() {
		Player p = World.getPlayerByClientIndex(index);
		p.packetDispatcher().sendCloseInterface(77);
		int itemsKept = 3;
		int[] items = calculate(itemsKept);
		p.packetDispatcher().sendMultiItems(-1, 1, 93, p.inventory().items, p.inventory().amounts);
		p.packetDispatcher().sendMultiItems(-1, 1, 94, p.equipment().items, p.equipment().amounts);
		p.packetDispatcher().setInterfaceOptions(210, 102, 18, 0, 4);
		p.packetDispatcher().setInterfaceOptions(210, 102, 21, 0, 40);
		p.packetDispatcher().sendInterfaceScript(118, new Object[] {items[3], items[2], items[1], items[0], itemsKept, 0}, "iiiiii");
		p.packetDispatcher().sendInterface(102);
	}

	/*
	* 2 = Safe Area , 3 = Player Owned House, 4 = Castle Wars, 5 = Trouble Brewing, 6 = Barbarian Assault.
	*/
	public void openAllItemsKept(int type) {
		Player p = World.getPlayerByClientIndex(index);
		p.packetDispatcher().sendInterfaceScript(118, new Object[] {0, type}, "ii");
		p.packetDispatcher().sendInterface(102);
	}

	public int[] calculate(int keep) {
		int[] items = {-1, -1, -1, -1};
		Player p = World.getPlayerByClientIndex(index);
		int curValue = -1;
		byte curSlot = -1;
		byte totalItemsKept = (byte)keep;
		for (int idx = 0; idx < totalItemsKept; ++idx) {
			curValue = -1;
			curSlot = -1;
			for (byte slot = 0; slot < 28; ++slot) {
				if (p.inventory().items[slot] != -1) {
					int value = ItemManagement.storeValue(p.inventory().items[slot]);
					if (value > curValue && (check(idx, slot) || (p.inventory().amounts[slot] - index > 0))) {
						curValue = value;
						curSlot = slot;
					}
				}
			}
			for (byte slot = 0; slot < 14; ++slot) {
				if (p.equipment().items[slot] != -1) {
					int value = ItemManagement.storeValue(p.equipment().items[slot]);
					if (value > curValue && (check(idx, slot + 28) || (p.equipment().amounts[slot] - index > 0))) {
						curValue = value;
						curSlot = (byte)(slot + 28);
					}
				}
			}
			if (curSlot == -1) {
				for (int i = idx; i < 4; ++i)
					itemKeptLocations[i] = -1;
				break;
			} else {
				items[idx] = curSlot > 27 ? p.equipment().items[curSlot - 28] : p.inventory().items[curSlot];
			}
			itemKeptLocations[idx] = curSlot;
		}
		return items;
	}

	private boolean check(int length, int position) {
		for (int i = 0; i < length; ++i)
			if (itemKeptLocations[i] == position)
				return false;
		return true;
	}

}
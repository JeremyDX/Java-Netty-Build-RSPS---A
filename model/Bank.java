/*
* @ Author - Digistr.
* @ Info - Contains everything to do with banking in the 474 era.
*/

package com.model;

import com.util.ItemManagement;

public class Bank {

	private short index;
	public short[] items = new short[400];
	public int[] amounts = new int[400];
	public boolean swap = true;
	public boolean note = false;

	private static final Object[] BANK_OFFER_CHANGE = new Object[] { 
		"Withdraw 1", "Withdraw 5", "Withdraw 10", "Withdraw 50", "Withdraw X", "Withdraw All", 0, 50, 8, 0, 786521
	};

	protected Bank() {
		for (int i = 0; i < 400; i++)
			items[i] = -1;
	}

	public void swap(int from, int to) {
		Player p = World.getPlayerByClientIndex(index);
		short keep = items[from];
		int keepN = amounts[from];
		if (swap || items[to] == -1) {
			items[from] = items[to];
			amounts[from] = amounts[to];
			items[to] = keep;
			amounts[to] = keepN;
			Player plr = World.getPlayerByClientIndex(index);
			p.packetDispatcher().sendSingleItem(12, 89, 0, from, items[from], amounts[from]);
			p.packetDispatcher().sendSingleItem(12, 89, 0, to, items[to], amounts[to]);
		} else {
			if (from > to) {
				for (int i = from - 1; i >= to; --i) { 
					items[i + 1] = items[i];
					amounts[i + 1] = amounts[i];
				}
			} else {
				for (int i = from; i < to; ++i) {
					items[i] = items[i + 1];
					amounts[i] = amounts[i + 1];
				}
			}
			items[to] = keep;
			amounts[to] = keepN;
			p.packetDispatcher().sendMultiItems(12, 89, 0, items, amounts);
		}
	}

	public void deposit(int slot, int amount) {
		Player p = World.getPlayerByClientIndex(index);
		if (p.interfaceContainer().sidebar[0] != 15)
			return;
		short item = p.inventory().items[slot];
		if (item < 0)
			return;
		int firstAvailSlot = -1;
		int bankSlot = -1;
		boolean isStackable = false;
		if (ItemManagement.isArrayStackable(item)) {
			if (ItemManagement.getNoteId(item) != item)
				item = (short)ItemManagement.getNoteId(item);
			isStackable = true;
		}
		for (; ++bankSlot < 400; ) {
			if (items[bankSlot] == item)
				break;
			if (firstAvailSlot == -1 && amounts[bankSlot] == 0)
				firstAvailSlot = bankSlot;
		}
		if (bankSlot == 400)
			bankSlot = firstAvailSlot;
		if (bankSlot == 400) {
			p.packetDispatcher().sendMessage("Not enough bank space to deposit this item.");
			return;
		}
		int availAmount = Integer.MAX_VALUE - amounts[bankSlot];
		if (availAmount == 0) {
			p.packetDispatcher().sendMessage("Not enough bank space to deposit this item.");
			return;
		}
		if (isStackable) {
			int amt = p.inventory().amounts[slot];
			if (amt > amount)
				amt = amount;
			if (availAmount < amt) {
				p.packetDispatcher().sendMessage("You have run out bank space to deposit this item.");
				amt = availAmount;
			}
			p.inventory().amounts[slot] -= amt;
			if (p.inventory().amounts[slot] == 0) {
				p.inventory().items[slot] = -1;
				++p.inventory().availableInventorySpace;
			}
			items[bankSlot] = item;
			amounts[bankSlot] += amt;
		} else {
			int readIndex = -1;
			if (availAmount < amount)
				amount = availAmount;
			for (int nextSlot = -1; ++readIndex < amount; ) {
				nextSlot = p.inventory().getNextSlot(nextSlot + 1, item);
				if (nextSlot == -1)
					break;
				p.inventory().items[nextSlot] = -1;
				p.inventory().amounts[nextSlot] = 0;
			}
			items[bankSlot] = item;
			amounts[bankSlot] += readIndex;
			p.inventory().availableInventorySpace += readIndex;
		}
		p.packetDispatcher().sendSingleItem(12, 89, 0, bankSlot, items[bankSlot], amounts[bankSlot]);
		p.packetDispatcher().sendMultiItems(15, 0, 93, p.inventory().items, p.inventory().amounts);
	}

	public void withdraw(int slot, int amount) {
		Player p = World.getPlayerByClientIndex(index);
		if (p.interfaceContainer().mainInterface != 12)
			return;
		int amt = amounts[slot];
		if (amt > amount)
			amt = amount;
		short item = items[slot];
		if (item < 0 || amt == 0)
			return;
		if (note) {
			int item2 = ItemManagement.getNoteId(item);
			if (item2 == item)
				p.packetDispatcher().sendMessage("This item cannot be withdrawn as a note.");	
			item = (short)item2;
		} 
		if (ItemManagement.isArrayStackable(item)) {
			int invSlot = p.inventory().getNextItemSlot(0,item);
			if (invSlot == -1) {
				p.packetDispatcher().sendMessage("There is not enough inventory space to withdraw this item.");
				return;
			}
			if ((long)amt + p.inventory().amounts[invSlot] > Integer.MAX_VALUE) {
				amt = Integer.MAX_VALUE - p.inventory().amounts[invSlot];
				p.packetDispatcher().sendMessage("There is not enough inventory space to withdraw this item.");
			}
			amounts[slot] -= amt;
			if (amounts[slot] == 0)
				items[slot] = -1;
			if (p.inventory().amounts[invSlot] == 0) {
				p.inventory().items[invSlot] = item;
				--p.inventory().availableInventorySpace;
			}
			p.inventory().amounts[invSlot] += amt;
			p.packetDispatcher().sendSingleItem(15,0,93,invSlot,p.inventory().items[invSlot],p.inventory().amounts[invSlot]);
		} else {
			int availableAmount = p.inventory().availableInventorySpace;
			boolean toMuch = false;
			if (availableAmount < amt) {
				if (availableAmount == 0) {
					p.packetDispatcher().sendMessage("There is not enough inventory space to withdraw this item.");
					return;
				}
				toMuch = true;
				amt = availableAmount;
			}
			int invSlot = -1;
			amounts[slot] -= amt;
			if (amounts[slot] == 0)
				items[slot] = -1;
			for (int i = 0; i < amt; i++) 
			{
				invSlot = p.inventory().getNextSlot(invSlot + 1);
				p.inventory().items[invSlot] = item;
				p.inventory().amounts[invSlot] = 1;
			}
			p.inventory().availableInventorySpace -= amt;
			p.packetDispatcher().sendMultiItems(15,0,93,p.inventory().items,p.inventory().amounts);
		}
		p.packetDispatcher().sendSingleItem(12, 89, 0, slot, items[slot], amounts[slot]);
	}

	public void open() {
		Player p = World.getPlayerByClientIndex(index);

		p.packetDispatcher().sendMultiItems(12, 89, 0, items, amounts);
		p.packetDispatcher().sendMultiItems(15, 0, 93, p.inventory().items, p.inventory().amounts);

		p.packetDispatcher().setInterfaceOptions(2, 12, 89, 0, 400);

		p.packetDispatcher().sendTab(15, 97, 0);
		p.packetDispatcher().sendInterface(12);

		//p.packetDispatcher().sendInterfaceScript(150, BANK_OFFER_CHANGE, "iiiiiissssss");
	}

	public void setIndex(short index) {
		this.index = index;
	}

}
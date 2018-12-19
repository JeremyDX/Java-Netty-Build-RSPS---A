/*
* @ Author - Digistr.
* @ Info - Handles everything to do with player inventory.
* @ Objective - Find a faster way to add , delete items.
*/

package com.model;

import com.util.ItemManagement;
import com.util.GroundItemManager;

public class Inventory {

	private short index;
	public short[] items = new short[28];
	public int[] amounts = new int[28];
	public byte availableInventorySpace = 28;
	public short pickupId = -1,pickupX = -1,pickupY = -1;

	protected Inventory() {
		for (int i = 0; i < items.length; i++)
			items[i] = -1;
	}

	public void setIndex(short index) {
		this.index = index;
	}

	public void swap(int first, int second) {
		short keep = items[first];
		int keepN = amounts[first];
		items[first] = items[second];
		amounts[first] = amounts[second];
		items[second] = keep;
		amounts[second] = keepN;
		Player plr = World.getPlayerByClientIndex(index);
		plr.packetDispatcher().sendSingleItem(149,0,93,first,items[first],amounts[first]);
		plr.packetDispatcher().sendSingleItem(149,0,93,second,items[second],amounts[second]);
	}

   /*
   * Removes all items from the inventory.

   */
	public void dispose() {
		for (int i = 0; i < 28; i++) {
			items[i] = -1;
			amounts[i] = 0;
		}
		availableInventorySpace = 28;
		Player plr = World.getPlayerByClientIndex(index);
		plr.packetDispatcher().sendMultiItems(149,0,93,items,amounts);
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
			System.out.println("Keep Items: " + locations[i]);
			if (locations[i] != -1 && locations[i] < 28)
			{
				amounts[locations[i]] -= 1;
				if (amounts[locations[i]] == 0)
					items[locations[i]] = -1;
			}
		}

		for (int i = 0; i < 28; ++i) 
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
		availableInventorySpace = 28;
	}

   /*
   * Adds a item into the player's inventory will send a message if it's full.
   */ 
 	public boolean add(int item, int amount) {
		if (amount < 1 || item < 0)
			return false;
		int availableSlot = -1;
		for (int i = 0; i < 28; i++) {
			if (amounts[i] == 0) {
				availableSlot = i;
				break;
			}
		}
		Player plr = World.getPlayerByClientIndex(index);
		if (ItemManagement.isArrayStackable(item)) {
			for (int i = 0; i < 28; i++) {
				if (item == items[i]) {
					availableSlot = i;
					break;
				}
			}
			if (availableSlot == -1) {
				plr.packetDispatcher().sendMessage("You don't have enough inventory space.");
				return false;
			}
			if ((long)amounts[availableSlot] + amount > Integer.MAX_VALUE) {
				plr.packetDispatcher().sendMessage("You don't have enough inventory space.");
				return false;
			}
			if (amounts[availableSlot] == 0) {
				items[availableSlot] = (short)item;
				availableInventorySpace--;
			}
			amounts[availableSlot] += amount;
			plr.packetDispatcher().sendSingleItem(149, 0, 93, availableSlot, items[availableSlot], amounts[availableSlot]);
		} else {
			if (availableSlot == -1) {
				plr.packetDispatcher().sendMessage("You don't have enough inventory space.");
				return false;
			}
			boolean toMuch = false;
			if (amount > availableInventorySpace) {
				amount = availableInventorySpace;
				toMuch = true;
			}
			availableInventorySpace -= amount;
			int nextSlot = availableSlot;
			for (int i = 0; i < amount; i++) {
				items[nextSlot] = (short)item;
				amounts[nextSlot] = 1;
				nextSlot = getNextSlot(nextSlot);
			}
			if (amount > 1) {
				plr.packetDispatcher().sendMultiItems(149, 0, 93, items, amounts);
				if (toMuch)
					plr.packetDispatcher().sendMessage("You don't have enough inventory space.");
			} else {
				plr.packetDispatcher().sendSingleItem(149, 0, 93, availableSlot, items[availableSlot], amounts[availableSlot]);
			}
		}
		return true;
	}

	public boolean deleteItemSilently(int item, int amount)
	{
		int availableSlot = getNextSlot(0, item);
		if (availableSlot == -1)
			return false;
		if (amounts[availableSlot] - amount > 0) {
			amounts[availableSlot] -= amount;
		} else {
			items[availableSlot] = -1;
			amounts[availableSlot] = 0;
			availableInventorySpace++;
		}
		return true;
	}

    /*
    * Delete's an item by searching for the item, Returns true or false if item is found.
    */
	public boolean delete(int item, int amount) {
		int availableSlot = getNextSlot(0, item);
		if (availableSlot == -1)
			return false;
		if (amounts[availableSlot] - amount > 0) {
			amounts[availableSlot] -= amount;
		} else {
			items[availableSlot] = -1;
			amounts[availableSlot] = 0;
			availableInventorySpace++;
		}
		Player plr = World.getPlayerByClientIndex(index);
		plr.packetDispatcher().sendSingleItem(149,0,93,availableSlot,items[availableSlot],amounts[availableSlot]);
		return true;
	}

    /*
    * Delete's an item by searching for the item, The player must contain the given amount.
    * Returns true if the player contains the amount otherwise false and no removal is done.
    * Generally used for shops or casting spells possibly.
    */
	public boolean deleteSetAmount(int item, int amount) {
		int availableSlot = getNextSlot(0, item);
		if (availableSlot == -1)
			return false;
		if (amounts[availableSlot] - amount < 0)
			return false;
		if ((amounts[availableSlot] -= amount) == 0) {
			items[availableSlot] = -1;
			availableInventorySpace++;
		}
		Player plr = World.getPlayerByClientIndex(index);
		plr.packetDispatcher().sendSingleItem(149, 0, 93, availableSlot, items[availableSlot], amounts[availableSlot]);
		return true;
	}

    /* 
    * Use with CAUTION! This is to only be used when you already know the item, slot, and amount!
    * This will insert the item into your inventory at given slot.
    * Then returns true if successfully added or false if too much amount was added.
    */
	public boolean addExistingItem(short item, int slot, int amount) {
		Player p = World.getPlayerByClientIndex(index);
		if ((long)amounts[slot] + amount > Integer.MAX_VALUE)
			return false;
		else {
			if (items[slot] == -1)
				--availableInventorySpace;
			items[slot] = item;
			amounts[slot] += amount;
		}
		Player plr = World.getPlayerByClientIndex(index);
		p.packetDispatcher().sendSingleItem(149, 0, 93, slot, item, amounts[slot]);
		return true;
	}

    /*
    * Easy to use method for checking if you can accept an item.
    * This will return the desired slot for that item.
    * This only works for stackables or nonstackable with amount of 1.
    */
 
	public int check(short item, int amount) {
		int slot = -1;
		if (ItemManagement.isArrayStackable(item)) {
			slot = getNextItemSlot(0, item);
		} else {
			slot = getNextSlot(0);
		}
		if (slot == -1)
			return -1;
		if ((long)amounts[slot] + amount > Integer.MAX_VALUE) {
			return -1;
		}
		return slot;
	}

     /*
     * Adds an array of items with minimal checks.
     */
	public void addItems(int[] keepItems)
	{
	    Player p = World.getPlayerByClientIndex(index);
	    for (int item : keepItems)
	    {
		if (item == -1)
			continue;
		int slot = check((short)item, 1);
		if (items[slot] == -1)
		{
			items[slot] = (short)item;
			amounts[slot] = 1;
		} else {
			amounts[slot]++;
		}
	    }
	}
  
     /*
     * WARNING!!!!!! Only use this when you have already verified this item for inserting.
     * This is used after the method @int check(int, int, int); has been executed.
     */
	public void addNoChecks(int slot, short item, int amount)
	{
		if (items[slot] == -1) 
		{
			items[slot] = item;
			--availableInventorySpace;
		}
		amounts[slot] += amount;
		World.getPlayerByClientIndex(index).packetDispatcher()
		.sendSingleItem(149, 0, 93, slot, item, amounts[slot]);
	}

   /*
   * Use with CAUTION! This will lower the amount of items or even delete the item.
   * In the given slot, only use this when you have figured out which slot to use.
   */
	public void deleteExistingItem(int slot, int amount) {
		amounts[slot] -= amount;
		if (amounts[slot] < 1) {
			items[slot] = -1;
			amounts[slot] = 0;
			availableInventorySpace++;
		}
		Player plr = World.getPlayerByClientIndex(index);
		plr.packetDispatcher().sendSingleItem(149,0, 93, slot, items[slot], amounts[slot]);	
	}

    /*
    * Used For Dropping A Single Item.
    */
	public boolean deleteSingleItem(int slot, int item) {
		if (slot < 0 || slot > 27 || items[slot] != item)
			return false;
		items[slot] = -1;
		amounts[slot] = 0;
		availableInventorySpace++;
		Player plr = World.getPlayerByClientIndex(index);
		plr.packetDispatcher().sendSingleItem(149,0,93,slot,-1,0);
		return true;
	}

   /* 
    * @Usage when we are looping items and want to delete a given index with given amount.
    * @Warning this will delete an item at a given index with the given amount.
    * @Warning this will not perform any checks to ensure we actually want to delete this item.
    */
	public void deleteWithoutChecks(int slot, int amount)
	{
		if ((amounts[slot] -= amount) <= 0)
		{
			items[slot] = -1;
			availableInventorySpace++;
		}
		World.getPlayerByClientIndex(index).packetDispatcher().sendSingleItem(149, 0, 93, slot, items[slot], amounts[slot]);	
	}

	public void deleteWithoutChecks(int slot)
	{
		if (amounts[slot] > 0)
		{
			amounts[slot] = 0;
			availableInventorySpace++;
		}
		items[slot] = -1;
		World.getPlayerByClientIndex(index).packetDispatcher().sendSingleItem(149, 0, 93, slot, items[slot], amounts[slot]);	
	}

    /*
    * Obtains the first slot this items exists at.
    */
	public int getFirstItemSlot(int item) {
		for (int i = 0; i < 28; i++)
			if (item == items[i])
				return i;
		return -1;
	}
    /*
    * Obtains the last slot this items exists at.
    */
	public int getLastItemSlot(int item) {
		for (int i = 27; i > -1; i--)
			if (item == items[i])
				return i;
		return -1;
	}

    /*
    * Returns the last slot index of this item if it exists and has the correct amount.
    * @Warnings - Will only check for 1 non-stackable item.
    */ 
	public int getLastItemSlot(int item, int amount)
	{
		for (int i = 27; i > -1; --i)
		{	
			if (item == items[i])
			{
				System.out.println("Item Check Success");
				if (amounts[i] < amount)
				{
					System.out.println("Amount Check Failed: " + amounts[i]);
					return -1;
				}
				System.out.println("Amount Check Success: " + amounts[i]);
				return i;
			}
		}
		System.out.println("Item Check Failed: " + item + " , " + amount);
		return -1;
	}

    /*
    * Used with stackable items to obtain available slot.
    * we give it a starting slot usually set to 0 unless otherwise needed.
    */
	public int getNextItemSlot(int start, int item) {
		int first = start;
		for ( ; start < 28; ++start)
			if (items[start] == item)
				return start;
		for (start = first; start < 28; ++start)
			if (items[start] == -1)
				return start;
		return -1;
	}

    /*
    * obtains the next empty slot or -1 if none were found.
    * we give it a starting slot usually set to 0 unless otherwise needed.
    */
	public int getNextSlot(int start) {
		for ( ; start < 28; ++start)
			if (amounts[start] == 0)
				return start;
		return -1;
	}

     /*
     * Obtains the next slot that contains a given item or -1 if item isn't found.
     * we give it a starting slot because this is generally used for searching.
     * because of the search ability loops don't always have to be 0 - 28.
     */
	public int getNextSlot(int start, int item) 
	{
		for ( ; start < 28; start++)
			if (items[start] == item)
				return start;
		return -1;
	}

    /*
     * Unique method designed for the purpose of searching your inventory for an item.
     * It then skips the slots based on available @count variable. Then returns the next index.
     * I.e. You have 8 sharks and count is 5. It will return the index of the 4th shark!
     */
	public int getCountedNextSlot(int count, int id)
	{
		int begin = 28;
		System.out.println("\n");
		while (--begin > -1)
		{
			if (items[begin] == id)
			{
				System.out.println("BEGIN IDX: " + begin);
				if (--count < 0)
					break;
			}
		}
		return begin;
	}

	public int getAvailability(int itemId)
	{
		if (ItemManagement.isArrayStackable(itemId))
		{
			for (int i = -1; ++i < 28; )
			{
				if (items[i] == itemId)
					return amounts[i];
			}
			return 0;
		}
		int count = 0;
		for (int i = -1; ++i < 28; )
			if (items[i] == itemId)
				++count;
		return count;
	}

	public int availableSpace() {
		return availableInventorySpace;
	}

}
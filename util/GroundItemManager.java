/*
* @ Author - Digistr
* @ info - Handles all ground items perfectly stackables stack up , 
	   teleport come back its still there, logout login still there etc...
*/

package com.util;

import com.model.Location;
import com.model.Player;
import com.model.World;
import com.packet.PacketBuilder;

public class GroundItemManager {

	private static boolean[] usedIndex = new boolean[10];
	private static short[] healths = new short[10];
	private static short[] items = new short[10];
	private static int[] amounts = new int[10];
	private static int[] indexs = new int[10];
	private static long[] owners = new long[10];
	private static Location[] locations = new Location[10];
	private static int curIndex = 0;
	private static int curLoopIndex = 0;

	public static int getAmount(int index) {
		if (index < curIndex)
			return amounts[index];
		return 0;
	}

	public static void add(Player owner, short item, int amount, short x, short y, byte z) {
		int copySlot = -1;
		if (ItemManagement.isArrayStackable(item))
			copySlot = getMyItem(owner.details().USERNAME_AS_LONG,item,x,y,z);
		if (curIndex == healths.length)
			increase();
		if (copySlot == -1 || ((long)amount + amounts[copySlot]) > Integer.MAX_VALUE) {
			healths[curIndex] = 200;
			items[curIndex] = item;
			amounts[curIndex] = amount;
			owners[curIndex] = owner.details().USERNAME_AS_LONG;
			locations[curIndex] = new Location(x,y,z);
			for (int i = 0; i < usedIndex.length; i++) {
				if (!usedIndex[i]) {
					usedIndex[i] = true;
					indexs[curIndex] = i;
					break;
				}
			}
			int distance = (int)owner.location().getDistance(locations[curIndex]);
			if (distance < 21) {
				owner.groundItems().add(indexs[curIndex]);
				sendCreateItem(owner,item,amount,locations[curIndex]);
			}
			curIndex++;
		} else {
			healths[copySlot] = 200;
			int oldAmt = amounts[copySlot];
			amounts[copySlot] += amount;
			sendChangeItemAmount(owner,item,oldAmt,amounts[copySlot],locations[copySlot]);
		}
	}

	public static void sendPickup(int index) {
		--curLoopIndex;
		sendItemRemoval(index);
	}

	public static void sendItemRemoval(int index) {
		for (int i = 0; i < World.curIndex; i++) {
			Player p = World.players[i];
			if (p.groundItems().remove(indexs[index])) {
				sendRemoveItem(p,items[index],locations[index]);
			}
		}
		usedIndex[indexs[index]] = false;
		--curIndex;
		for (int i = index; i < curIndex; i++) {
			items[i] = items[i + 1];
			amounts[i] = amounts[i + 1];
			owners[i] = owners[i + 1];
			locations[i] = locations[i + 1];
			healths[i] = healths[i + 1];
			indexs[i] = indexs[i + 1];
		}
	}

	public static int getItemIndex(long name, int item, int x, int y, int z) {
		for (int i = 0; i < curIndex; i++)
			if (items[i] == item)
				if (locations[i].equals(x,y,z))
					if (owners[i] == 0 || owners[i] == name)
						if (healths[i] > 0)
							return i;
		return -1;
	}

	public static int getMyItem(long name, int item, int x, int y, int z) {
		for (int i = 0; i < curIndex; i++)
			if (items[i] == item)
				if (locations[i].equals(x,y,z))
					if (owners[i] == name)
						if (healths[i] > 0)
							return i;
		return -1;
	}

	private static void sendChangeItemAmount(Player plr, int itemId, int oldAmt, int newAmt, Location loc) {
		plr.packetDispatcher().sendCoords(loc,plr.lastLocation());
		plr.getPacket().createPacket(220).addByte((byte) 0).
		addShort(itemId).addShort(oldAmt).addShort(newAmt > 65535 ? 65535 : newAmt);
	}

	private static void sendRemoveItem(Player plr,int id, Location loc) {
		plr.packetDispatcher().sendCoords(loc,plr.lastLocation());
		plr.getPacket().createPacket(28).
		addShortA(id).addByteA((byte) 0);
	}

	private static void sendCreateItem(Player plr, int item, int amount, Location loc) {
		plr.packetDispatcher().sendCoords(loc,plr.lastLocation());
		plr.getPacket().createPacket(7).addByteA(0).addShort(item).addLEShortA(amount > 65535 ? 65535 : amount);
	}

	private static void sendCreateItemOwner(int index) {
		Player p = World.getPlayerByName(owners[index]);
		if (p != null) {
			int distance = (int)p.location().getDistance(locations[index]);
			if (p.location().z == locations[index].z && distance < 21 && p.groundItems().add(indexs[index])) {
				sendCreateItem(p,items[index],amounts[index],locations[index]);
			} else if (p.location().z != locations[index].z && p.groundItems().remove(indexs[index])) {
				sendRemoveItem(p,items[index],locations[index]);
			} else if (distance > 47 && distance < 64 && p.groundItems().remove(indexs[index])) {
				sendRemoveItem(p,items[index],locations[index]);
			}
		}
	}

	private static void sendCreateItemAll(int index) {
		owners[index] = 0;
		for (int i = 0; i < World.curIndex; i++) {
			Player p = World.players[i];
			int distance = (int)p.location().getDistance(locations[index]);
			if (p.location().z == locations[index].z && distance < 21 && p.groundItems().add(indexs[index])) {
				sendCreateItem(p,items[index],amounts[index],locations[index]);
			} else if (p.location().z != locations[index].z && p.groundItems().remove(indexs[index])) {
				sendRemoveItem(p,items[index],locations[index]);
			} else if (distance > 47 && distance < 64 && p.groundItems().remove(indexs[index])) {
				sendRemoveItem(p,items[index],locations[index]);
			}
		}		
	}

	private static void increase() {
		short[] newData = new short[healths.length * 2];
		System.arraycopy(healths,0,newData,0,healths.length);
		healths = newData;

		short[] newData1 = new short[healths.length];
		System.arraycopy(items,0,newData1,0,items.length);
		items = newData1;

		int[] newData2 = new int[healths.length];
		System.arraycopy(amounts,0,newData2,0,amounts.length);
		amounts = newData2;

		long[] newData3 = new long[healths.length];
		System.arraycopy(owners,0,newData3,0,owners.length);
		owners = newData3;

		Location[] newData4 = new Location[healths.length];
		System.arraycopy(locations,0,newData4,0,locations.length);
		locations = newData4;

		int[] newData5 = new int[healths.length];
		System.arraycopy(indexs,0,newData5,0,indexs.length);
		indexs = newData5;

		boolean[] newData6 = new boolean[healths.length];
		System.arraycopy(usedIndex,0,newData6,0,usedIndex.length);
		usedIndex = newData6;
	}

	public static void execute() {
		for (curLoopIndex = 0; curLoopIndex < curIndex; curLoopIndex++) {
			--healths[curLoopIndex];
			if (healths[curLoopIndex] <= 0) {
				sendItemRemoval(curLoopIndex);
				--curLoopIndex;
			} else if (healths[curLoopIndex] <= 150) {
				sendCreateItemAll(curLoopIndex);
			} else if (healths[curLoopIndex] > 150) {
				sendCreateItemOwner(curLoopIndex);
			}
		}
	}
}
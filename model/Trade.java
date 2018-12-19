/*
* @ Author - Digistr.
* @ Info - Handles trading between 2 players.
*/

package com.model;

import com.util.GameUtility;
import com.util.ItemManagement;

public class Trade {

	private short index; //Player Index

	private short[] items = new short[28];
	private int[] amounts = new int[28];

	private int emptyInventorySlots;

	private long tradeWith;
	private byte tradeStage;

	private static final Object[] TRADE_SCRIPT_MY_ITEMS = {"", "", "", "Examine", "Offer-X", "Offer-All", "Offer-10", "Offer-5", "Offer", -1, 0, 7, 4, 93, 22020096};
        private static final Object[] TRADE_SCRIPT_INVENTORY = {"", "", "", "Examine", "Remove-X", "Remove-All", "Remove-10", "Remove-5", "Remove", -1, 0, 7, 4, 24, 21954609};
        private static final Object[] TRADE_SCRIPT_OTHERS_ITEMS = {"","","","","","","","","",-1, 0, 7, 4, 23, 21954610};

	protected Trade() 
	{
		for (int i = 0; i < items.length; ++i)
			items[i] = -1;
	}

	public void setIndex(short index) 
	{
		this.index = index;
	}

	public void open() 
	{
		Player p = World.getPlayerByClientIndex(index);
		Player other = World.getPlayerByName(tradeWith);

		p.packetDispatcher().sendInterfaceScript(150, TRADE_SCRIPT_INVENTORY , "iiiiiisssssssss");
		p.packetDispatcher().sendInterfaceScript(150, TRADE_SCRIPT_MY_ITEMS , "iiiiiisssssssss");
		p.packetDispatcher().sendInterfaceScript(150, TRADE_SCRIPT_OTHERS_ITEMS , "iiiiiisssssssss");
		p.packetDispatcher().setInterfaceOptions(1086, 336,  0, 0, 27);
		p.packetDispatcher().setInterfaceOptions(1086, 335, 49, 0, 27);
		p.packetDispatcher().setInterfaceOptions(1086, 335, 50, 0, 27);
		//p.packetDispatcher().sendMultiItems(-1, 1, 93, p.inventory().items, p.inventory().amounts);
		p.packetDispatcher().sendMultiItems(-1, 1, 23, items, amounts); 
		p.packetDispatcher().sendMultiItems(-1, 1, 24, items, amounts);
		p.packetDispatcher().sendInterfaceString("", 335, 56);
		other.packetDispatcher().sendInterfaceString(p.details().USERNAME_AS_STRING + "'s Free<br>Slots Available " + (emptyInventorySlots = p.inventory().availableInventorySpace), 335, 20);
		p.packetDispatcher().sendInterfaceString("Trading With: " + other.details().USERNAME_AS_STRING, 335, 16);
		p.packetDispatcher().sendTab(336, 97, 0);
		p.packetDispatcher().sendInterface(335);
		tradeStage = 1;
	}

    /*
    * Stage 0 = Not in trade, Stage 1 = First Window , Stage 2 = Accepted First Window.
    * Stage 3 = Second Window, Stage 4 = Accepted Second Window, Stage 5 = Completed Trade.
    */
	public void sendRequest(Player p, Player p2)
	{	
		if (tradeStage == 0)
		{
			if (p2.isBusy()) 
			{
				tradeWith = 0;
				p.packetDispatcher().sendMessage("This player is currently busy.");
				return;
			}
			tradeWith = p2.details().USERNAME_AS_LONG;
			if (p2.trade().tradeWith == p.details().USERNAME_AS_LONG)
			{
				open();
				p2.trade().open();
			} else {
				p2.packetDispatcher().sendMessage(p.details().USERNAME_AS_STRING.concat(":tradereq:"));
				p.packetDispatcher().sendMessage("Sending trade offer...");
			}
		} else {
			declineTrade(true);
		}
	}

	public void declineTrade(boolean declined) 
	{
		if (tradeStage == 0)
			return;
		Player me = World.getPlayerByClientIndex(index);
		Player other = World.getPlayerByName(tradeWith);
		if (declined) {
			me.packetDispatcher().sendMessage("You declined the trade.");
			other.interfaceContainer().resetInterfaces(other, true);
		} else {
			me.packetDispatcher().sendMessage("Other player has declined the trade.");
		}
		for (int i = 0; i < 28; ++i)
		{
			items[i] = -1;
			amounts[i] = 0;
		}
		tradeWith = tradeStage = 0;
	}

	public void offerItem(int slot, int maximum_amount)
	{
		if (tradeStage > 2)
			return;

		Player p = World.getPlayerByClientIndex(index);
	
		Player other = World.getPlayerByName(tradeWith);

		short invId = p.inventory().items[slot];
		
		other.trade().tradeStage = tradeStage = 1;
		other.packetDispatcher().sendInterfaceString("", 335, 56);
		p.packetDispatcher().sendInterfaceString("", 335, 56);

		if (ItemManagement.isArrayStackable(invId))
		{
			int location = -1;
			int availSlot = -1;
			for ( ; ++location < 28 ; )
			{
				if (availSlot == -1 && items[location] == -1)
					availSlot = location;
				if (items[location] == invId)
				{
					availSlot = location;
					break;
				}
			}
			if (availSlot == -1)
				return;
			location = availSlot;
			int amount = p.inventory().amounts[slot] - amounts[location];
			if (maximum_amount < amount)
				amount = maximum_amount;
			maximum_amount = amounts[location];
			items[location] = invId;
			amounts[location] += amount;
			amount = p.inventory().amounts[slot] - amounts[location];
			if (amount == 0)
			{
				invId = -1;
				other.packetDispatcher().sendInterfaceString(p.details().USERNAME_AS_STRING + "'s Free<br>Slots Available " + (++emptyInventorySlots), 335, 20);
			}

			p.packetDispatcher().sendSingleItem(-1, 1, 24, location, items[location], amounts[location]); //MY TRADE ITEMS
			p.packetDispatcher().sendSingleItem(-1, 1, 93, slot, invId, amount); //MY INVENTORY ITEMS
			other.packetDispatcher().sendSingleItem(-1, 1, 23, location, items[location], amounts[location]); //OTHER PERSONS TRADE ITEMS
		} else {
			int available = countFromId(invId);
			int beginSlot = p.inventory().getCountedNextSlot(available, invId);
			slot = nextAvailableSlot(-1);

			for (int i = beginSlot; i > -1; --i)
			{
				if (p.inventory().items[i] == invId)
				{
					items[slot] = invId;
					amounts[slot] = 1;
					p.packetDispatcher().sendSingleItem(-1, 1, 24, slot, invId, 1); //MY TRADE ITEMS
					p.packetDispatcher().sendSingleItem(-1, 1, 93, i, -1, 0); //MY INVETORY ITEMS
					other.packetDispatcher().sendSingleItem(-1, 1, 23, slot, invId, 1); //OTHER PERSONS TRADE ITEMS
					++emptyInventorySlots;
					slot = nextAvailableSlot(slot);
					if (--maximum_amount == 0) 
						break;
				}
			}
			other.packetDispatcher().sendInterfaceString(p.details().USERNAME_AS_STRING + "'s Free<br>Slots Available " + emptyInventorySlots, 335, 20);
		}
	}

	public void removeItem(int slot, int maximum_amount)
	{
		if (tradeStage > 2)
			return;

		int tradeId = items[slot];

		if (tradeId == -1)
			return;

		Player p = World.getPlayerByClientIndex(index);
		Player other = World.getPlayerByName(tradeWith);

		other.trade().tradeStage = tradeStage = 1;
		other.packetDispatcher().sendInterfaceString("", 335, 56);
		p.packetDispatcher().sendInterfaceString("", 335, 56);

		if (ItemManagement.isArrayStackable(tradeId))
		{
			int location = p.inventory().getFirstItemSlot(tradeId);
			int previous = amounts[slot];
			if (maximum_amount > amounts[slot])
				maximum_amount = amounts[slot];
			amounts[slot] -= maximum_amount;
			int amount = maximum_amount;
			maximum_amount = p.inventory().amounts[location] - amounts[slot];

			if ((p.inventory().amounts[location] - previous) == 0)
			{
				other.packetDispatcher().sendInterfaceString(p.details().USERNAME_AS_STRING + "'s Free<br>Slots Available " + (--emptyInventorySlots), 335, 20);
			}
			if (amounts[slot] == 0)
				items[slot] = -1;

			p.packetDispatcher().sendSingleItem(-1, 1, 24, slot, items[slot], amounts[slot]); //MY TRADE ITEMS
			p.packetDispatcher().sendSingleItem(-1, 1, 93, location, tradeId, maximum_amount); //MY INVETORY ITEMS
			other.packetDispatcher().sendSingleItem(-1, 1, 23, slot, items[slot], amounts[slot]); //OTHER PERSONS TRADE ITEMS	
		} else {
			int available = countFromId(tradeId); //Get Available In Trade.
			slot = nextAvailableItemSlot(-1, tradeId);

			int pushIdx = 28;
			while (--pushIdx > -1)
			{
				if (tradeId == p.inventory().items[pushIdx])
					if(--available < 1) break;
			}

			for ( ; pushIdx < 28; ++pushIdx)
			{
				if (p.inventory().items[pushIdx] == tradeId)
				{
					items[slot] = -1;
					amounts[slot] = 0;
					p.packetDispatcher().sendSingleItem(-1, 1, 24, slot, -1, 0); //MY TRADE ITEMS
					p.packetDispatcher().sendSingleItem(-1, 1, 93, pushIdx, tradeId, 1); //MY INVETORY ITEMS
					other.packetDispatcher().sendSingleItem(-1, 1, 23, slot, -1, 0); //OTHER PERSONS TRADE ITEMS
					slot = nextAvailableItemSlot(slot, tradeId);
					--emptyInventorySlots;
					if (--maximum_amount == 0) 
						break;
				}	
			}

			other.packetDispatcher().sendInterfaceString(p.details().USERNAME_AS_STRING + "'s Free<br>Slots Available " + emptyInventorySlots, 335, 20);
			
		}
	}

	public void acceptFirstTradeWindow()
	{
		Player p = World.getPlayerByClientIndex(index);
		Player other = World.getPlayerByName(tradeWith);

		if (tradeStage == 1)  //Clicking Accept Button.
		{
			p.packetDispatcher().sendInterfaceString("Waiting for other player...", 335, 56);
			other.packetDispatcher().sendInterfaceString("Other player has accepted...", 335, 56);
			tradeStage = 2;
		}
		if (tradeStage == 2 && other.trade().tradeStage == 2)  //Other Player Has Already Accepted Move To Confirmation Screen!
		{
			if (!(verify(other) && other.trade().verify(p)))
			{
				tradeStage = 1;
				other.trade().tradeStage = 1;
				p.packetDispatcher().sendInterfaceString("Not enough space to accept this trade!", 335, 56);
				other.packetDispatcher().sendInterfaceString("Not enough space to accept this trade!", 335, 56);
				return;
			}

			tradeStage = 3;
			other.trade().tradeStage = 3;

			p.packetDispatcher().sendInterface(334);
			other.packetDispatcher().sendInterface(334);

			StringBuilder display = new StringBuilder();

			int seek = 0;
		
			for (int i = 0; i < 28; ++i)
			{
				if (amounts[i] == 0) 
					continue;
				if (++seek == 15)
				{
					other.packetDispatcher().sendInterfaceString(display.toString(), 334, 41);
					p.packetDispatcher().sendInterfaceString(display.toString(), 334, 38);
					display.setLength(0);
				}
				display.append("<col=FF9040>").append(items[i]).append(" <col=CCCCCC>x ");
				getFormattedColor(display, amounts[i]);
			}

			if (seek > 14)
			{
				other.packetDispatcher().sendInterfaceString(display.toString(), 334, 42);
				p.packetDispatcher().sendInterfaceString(display.toString(), 334, 39);
				other.packetDispatcher().sendInterfaceConfig(334, 42, true);
				other.packetDispatcher().sendInterfaceConfig(334, 41, true);
				p.packetDispatcher().sendInterfaceConfig(334, 39, true);
				p.packetDispatcher().sendInterfaceConfig(334, 38, true);	
			} else {
				if (display.length() == 0)
					display.append("<col=FFFFFF>Absolutely nothing!");
				other.packetDispatcher().sendInterfaceString(display.toString(), 334, 40);
				p.packetDispatcher().sendInterfaceString(display.toString(), 334, 37);
				other.packetDispatcher().sendInterfaceConfig(334, 40, true);
				p.packetDispatcher().sendInterfaceConfig(334, 37, true);
			}

			display.setLength(0);
			seek = 0;

			for (int i = 0; i < 28; ++i)
			{
				if (other.trade().amounts[i] == 0) 
					continue;
				if (++seek == 15)
				{
					other.packetDispatcher().sendInterfaceString(display.toString(), 334, 38);
					p.packetDispatcher().sendInterfaceString(display.toString(), 334, 41);
					display.setLength(0);
				}
				display.append("<col=FF9040>").append(other.trade().items[i]).append(" <col=BDBDBD>x ");
				getFormattedColor(display, other.trade().amounts[i]);
			}

			if (seek > 14)
			{
				other.packetDispatcher().sendInterfaceString(display.toString(), 334, 39);
				p.packetDispatcher().sendInterfaceString(display.toString(), 334, 42);
				p.packetDispatcher().sendInterfaceConfig(334, 42, true);
				p.packetDispatcher().sendInterfaceConfig(334, 41, true);
				other.packetDispatcher().sendInterfaceConfig(334, 39, true);
				other.packetDispatcher().sendInterfaceConfig(334, 38, true);	
			} else {
				if (display.length() == 0)
					display.append("<col=FFFFFF>Absolutely nothing!");
				other.packetDispatcher().sendInterfaceString(display.toString(), 334, 37);
				p.packetDispatcher().sendInterfaceString(display.toString(), 334, 40);
				other.packetDispatcher().sendInterfaceConfig(334, 37, true);
				p.packetDispatcher().sendInterfaceConfig(334, 40, true);
			}

			other.packetDispatcher().sendInterfaceString("Trading With: " + p.details().USERNAME_AS_STRING, 334, 44);
			p.packetDispatcher().sendInterfaceString("Trading With: " + other.details().USERNAME_AS_STRING, 334, 44);
		}
	}

	public void acceptSecondTradeWindow()
	{
		Player p = World.getPlayerByClientIndex(index);
		Player other = World.getPlayerByName(tradeWith);

		if (tradeStage == 3)  //Clicking 2nd Accept Button.
		{
			tradeStage = 4;
		}
		if (tradeStage == 4 && other.trade().tradeStage == 4)  //Both players fully accepted the trade disperse items!
		{
			p.interfaceContainer().resetInterfaces(p, false);
			other.interfaceContainer().resetInterfaces(other, false);
			p.packetDispatcher().sendMessage("Trade accepted..");
			other.packetDispatcher().sendMessage("Trade accepted..");
			for (int i = 0; i < 28; ++i)
			{
				if (other.trade().amounts[i] > 0)
					other.inventory().deleteItemSilently(other.trade().items[i], other.trade().amounts[i]);
				if (amounts[i] > 0)
					p.inventory().deleteItemSilently(items[i], amounts[i]);
			}
			for (int i = 0; i < 28; ++i)
			{
				if (other.trade().amounts[i] > 0)
				{
					p.inventory().add(other.trade().items[i], other.trade().amounts[i]);
					other.trade().items[i] = -1;
					other.trade().amounts[i] = 0;
				}
				if (amounts[i] > 0)
				{
					other.inventory().add(items[i], amounts[i]);
					items[i] = -1;
					amounts[i] = 0;
				}
			}
			p.packetDispatcher().sendMultiItems(149, 0, 93, p.inventory().items, p.inventory().amounts);
			other.packetDispatcher().sendMultiItems(149, 0, 93, other.inventory().items, other.inventory().amounts);
			tradeWith = other.trade().tradeWith = tradeStage = other.trade().tradeStage = 0;
		}
	}

	private boolean verify(Player other)
	{
		int available = other.trade().emptyInventorySlots;
		for (int i = 0; i < 28; ++i)
		{
			if (ItemManagement.isArrayStackable(items[i]))
			{
				int hasItem = other.trade().findInventoryMockAmount(other.trade().findAmount(items[i]), items[i]);
				System.out.println("HasItem: " + hasItem + " Amounts: " + amounts[i]);
				if ((long)hasItem + amounts[i] > Integer.MAX_VALUE)
					return false;
				if (hasItem == 0)
					--available;
			} else if (items[i] != -1) {
				--available;
			}
		}
		System.out.println("Available: " + available);
		if (available < 0)
			return false;
		return true;
	}

	private static void getFormattedColor(StringBuilder sb, int amount)
	{
		if (amount < 100000)
		{
			sb.append("<col=FFFF00>").append(amount).append("<br>");
		} else if (amount < 10000000) {
			sb.append("<col=FFFFFF>").append(amount).append("<br>");
		} else if (amount < 1000000000) {
			sb.append("<col=00FF80>").append(amount).append("<br>");
		} else {
			sb.append("<col=0DE7E7>").append(amount).append("<br>");
		}
	}

	public int findAmount(int itemId)
	{
		Player p = World.getPlayerByClientIndex(index);
		for (int i = 0; i < 28; ++i)
			if (items[i] == itemId)
				return amounts[i];
		return 0;
	}

	public int findInventoryMockAmount(int amount, int itemId)
	{
		Player p = World.getPlayerByClientIndex(index);
		int invSlot = p.inventory().getFirstItemSlot(itemId);
		if (invSlot == -1)
			return 0;
		return p.inventory().amounts[invSlot] - amount;
	}

	public int nextAvailableSlot(int begin)
	{
		for ( ; ++begin < 28; )
		{
			if (items[begin] == -1)
				return begin;
		}
		return begin;
	}

	public int nextAvailableItemSlot(int begin, int itemId)
	{
		for ( ; ++begin < 28; )
		{
			if (items[begin] == itemId)
				return begin;
		}
		return begin;
	}

	private int countFromId(int itemId)
	{
		int sum = 0;
		for (int i = 0; i < 28; ++i)
			if (items[i] == itemId)
				++sum;
		return sum;
	}

	private void flashIcon(int itemSlot) 
	{
		Player p = World.getPlayerByClientIndex(index);
		Object[] TRADE_SCRIPT_4 = new Object[] { itemSlot, 7, 4, 21954560 };
		p.packetDispatcher().sendInterfaceScript(143, TRADE_SCRIPT_4, "iiii");
	}
}
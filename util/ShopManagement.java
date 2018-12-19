package com.util;

import com.model.Player;

public class ShopManagement
{
	private static final Object[] SHOP_INVENTORY_SCRIPT = new Object[] {"Sell 10", "Sell 5", "Sell 1", "Value", -1, 0, 7, 4, 93, 40697856 };
	private static final Object[] MAIN_SHOP_SCRIPT      = new Object[] {"Buy 10" , "Buy 5" , "Buy 1" , "Value", -1, 0, 5, 8, 92, 40632343 };
	private static final Object[] PLAYER_SHOP_SCRIPT      = new Object[] {"Buy 10" , "Buy 5" , "Buy 1" , "Value", -1, 0, 5, 8, 31, 40632344 };
		
	private static Stock[] SHOPS;

	public static void initialize()
	{
		SHOPS = new Stock[1]; //Testing.
		SHOPS[0] = new Stock(0);
	}

	public static void updateContents()
	{
		for (Stock shop : SHOPS)
		{			
			if (!shop.updated)
				continue;

			System.out.println("Shop Needs Updates: " + shop.updated);
			boolean needsUpdate = false;
			for (int i = 0; i < shop.speed.length; ++i)
			{
				if (shop.initial[i] != shop.amounts[i])
				{
					needsUpdate = true;
					if (--shop.refresh[i] == 0)
					{
						//This item is ready for updating!
						if (shop.initial[i] > shop.amounts[i])
						{
							++shop.amounts[i];
						}
						else if (shop.initial[i] < shop.amounts[i])
						{
							--shop.amounts[i];
						}
						shop.refresh[i] = shop.speed[i];
					}
				}
			}
			for (int i = shop.speed.length; i < shop.refresh.length; ++i)
			{
				//Speed is only available for items which are ALWAYS in the stock!
				if (shop.amounts[i] > 0)
				{
					if (--shop.refresh[i] == 0)
					{
						if (--shop.amounts[i] == 0)
						{
							shop.items[i] = -1;
						}
						needsUpdate = true;
					}
				}
			}
			shop.updated = needsUpdate;
		}
	}

	public static void updatePlayerView(Player player)
	{
		if (player.interfaceContainer().mainInterface == 620)
		{
			Stock shop = SHOPS[player.interfaceContainer().state >> 1];
			if (shop.updated)
			{

			}
		}
	}

	public static void open(Player player, int shop_id)
	{
            	player.interfaceContainer().state = (byte)(0 << 1 | 1);

		player.packetDispatcher().sendTab(621, 97, 0);
		player.packetDispatcher().sendInterface(620);

		player.packetDispatcher().sendMultiItems(-1, 0, 93, player.inventory().items, player.inventory().amounts);
		player.packetDispatcher().sendMultiItems(-1, 0, 92, SHOPS[0].items, SHOPS[0].amounts);

		player.packetDispatcher().setInterfaceOptions(1054, 621,  0, 0, 28); //Options 1,2,3,4(Val,1,5,10) and 10(Examine).
		player.packetDispatcher().setInterfaceOptions(1054, 620, 23, 0, 40); //Options 1,2,3,4(Val,1,5,10) and 10(Examine).

            	player.packetDispatcher().sendInterfaceString((new StringBuilder()).append("Shop Name: ").append(shop_id).toString(), 620, 22);
           	player.packetDispatcher().sendInterfaceString("Right-click on shop to buy item - Right-click on inventory to sell item", 620, 28);

		player.packetDispatcher().sendInterfaceConfig(620, 14, false);
		player.packetDispatcher().sendInterfaceConfig(620, 15, false);
		player.packetDispatcher().sendInterfaceConfig(620, 16, false);
		player.packetDispatcher().sendInterfaceConfig(620, 17, false);
 		player.packetDispatcher().sendInterfaceConfig(620, 19, false);
		player.packetDispatcher().sendInterfaceConfig(620, 20, false);
		player.packetDispatcher().sendInterfaceConfig(620, 21, false);
		player.packetDispatcher().sendInterfaceConfig(620, 26, false);
		player.packetDispatcher().sendInterfaceConfig(620, 29, false);

		player.packetDispatcher().sendInterfaceScript(150, SHOP_INVENTORY_SCRIPT, "iiiiiissss");
		player.packetDispatcher().sendInterfaceScript(150, MAIN_SHOP_SCRIPT, "iiiiiissss");
	}

	public static void buy(Player player, int slot, int amount)
	{
		Stock shop = SHOPS[player.interfaceContainer().state >> 1];

		if (slot >= shop.items.length)
			return;

		short itemId = shop.items[slot];
		
		if (itemId == -1)
			return;

		int availableToBuy = shop.amounts[slot];
		if (amount < availableToBuy)
			availableToBuy = amount;

		int cost = ItemManagement.storeValue(itemId);
		int availableMoney = player.inventory().getAvailability(shop.currency);
		
		shop.updated = true;
	}

	public static void sell(Player player, int slot, int amount)
	{
			
	}

	static private class Stock
	{
		public short[] items; 		//Item ID.
		public int[] amounts;		//Item Amount. Decides to refresh Up or Down.
		public int[] initial;		//Initial Amount.
		public byte[] speed;		//Rate to refresh at in Server Ticks!
		public byte[] refresh;		//Refresh needed?
		public boolean updated;		//True if this shop has any changes which occurred!
		public byte default_refresh;	//Default for additional items.
		public short type;		//0 = Normal
		public short currency;		//995 = Coins. If type is negative then we use custom systems.

		public Stock(int index)
		{
			items = new short[] {1925, 11694, 11696, 11698, 11700, 6585, 1044};
			amounts = new int[] {5, 20, 30, 40, 50, 1, 1};
			initial = new int[] {5, 20, 30, 40, 50, 1, 1};
			speed = new byte[] {10, 2, 6, 18, 48, 10, 5};
			refresh = new byte[] {10, 2, 6, 18, 48, 10, 5};
			default_refresh = 50; //30 Seconds?
			currency = 995;
		}
	}
}
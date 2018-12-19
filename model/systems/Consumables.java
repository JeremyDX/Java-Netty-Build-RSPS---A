package com.model.systems;

import com.model.Player;

public class Consumables
{
	private static final short[] CONSUME_IDS = 
	{	
		315,   325,   355,   339, //Shrimp      Sardine   Mackeral      Cod
		333,   329,   379,   373, //Trout       Salmon    Lobster       Swordfish		
		7946,  385,   397,   391  //Monkfish    Shark     Sea Turtle    Manta Ray  
	};

	private static final short[] HEAL = 
	{
		3, 4, 7, 7, 
		7, 9, 12, 14, 
		16, 20, 21, 22
	};

	public static void consume(Player player, int slot, int itemId)
	{
		int index = -1;
		for (int item : CONSUME_IDS)
		{
			++index;
			if (item == index)
				break;
		}
		if (index == CONSUME_IDS.length)
			return;
	}

	
}	
package com.model;

/*
* This handles accurately swapping fight types/styles to allow correct exp to be given.
*/
public class FightType {

	/*
	* The variables.
	*/
	public byte type, box;

	/*
	* This is useless it was for testing purposes remove if you want.
	*/
	private static final String[] FIGHT_NAMES = {
		"SLASH_ACCURATE_0","SLASH_AGRESSIVE_1","SLASH_DEFENSIVE_2","SLASH_CONTROLLED_3",
		"CRUSH_ACCURATE_4","CRUSH_AGRESSIVE_5","CRUSH_DEFENSIVE_6","CRUSH_CONTROLLED_7",
		"STAB_ACCURATE_8","STAB_AGRESSIVE_9","STAB_DEFENSIVE_10","STAB_CONTROLLED_11",
		"RANGED_ACCURATE_12","RANGED_RAPID_13","RANGED_DEFENSIVE_14",
		"AIM + FIRE_15","MAGIC_DEFENSIVE_16","MAGIC_NORMAL_17"
	};

	/*
	* The entire Fight Type array handles all fight types no loops required.
	*/
	private static final byte[][] FIGHT_TYPES = 
	{
		/*75,76*/{0,1,5,2,0,0,3,2,1},	{4,5,6,6,0,0,2,1,1},
		/*77,78*/{12,13,14,14,0,0,2,1,1}, {0,1,11,2,0,0,3,2,1},
		/*79,80*/{12,13,14,14,0,0,2,1,1}, {15,5,5,5,0,0,1,1,1},
		/*81,82*/{0,1,11,2,0,0,1,2,3},{0,1,5,2,0,0,1,2,3},
		/*83,84*/{8,9,5,10,0,0,1,2,3},{11,5,10,10,0,0,1,2,2},
		/*85,86*/{4,5,6,6,0,0,1,2,2},{0,9,5,2,0,0,1,2,3},
		/*87,88*/{11,3,7,10,0,0,1,2,3},{4,5,11,6,0,0,1,2,3},
		/*89,90*/{8,9,1,10,0,0,1,2,3},{4,5,6,6,0,1,2,2,2},
		/*91,92*/{12,13,14,14,0,0,1,2,2},{4,5,6,6,0,0,1,2,2},
		/*93,473*/{0,3,2,2,0,0,1,2,2},{12,13,14,14,0,0,1,2,2},
		/*474*/{1,12,16,16,0,0,1,2,2}

	};

	private static final byte[] BONUS_STYLE = 
	{
		1, 1, 1, 1, 2, 2, 2, 2, 0, 0, 0, 0, 4, 4, 4, 0, 3, 3
	};

	public FightType() {
		
	}

        /*
        * When the player clicks a new weapon style this is called.
        */
	public void pressed(int interfaceId, int child) {
		if (interfaceId > 472)
			interfaceId -= 379;
		box = FIGHT_TYPES[interfaceId - 75][child + 3];
		type = FIGHT_TYPES[interfaceId - 75][box];
		System.out.println("Box Change: " + box + " Type: " + type);
	}

        /*
        * When a player swaps weapons this is called.
        */
	public void wield(int interfaceId) {
		if (interfaceId > 472)
			interfaceId -= 379;
		box = FIGHT_TYPES[interfaceId - 75][box + 5];
		type = FIGHT_TYPES[interfaceId - 75][box];
	}

	public byte bonusIndex()
	{
		return BONUS_STYLE[type];
	}

	public void display(Player p) {
		p.packetDispatcher().sendMessage("Box: " + box + " Type: " + type + " String Type: " + FIGHT_NAMES[type]);
	}

}
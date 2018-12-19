package com.util;

import com.model.Player;

public class MagicSystem 
{
	/*
	 *	554: Fire rune   = 0 , 555: Water rune  = 1 , 556: Air rune    = 2  , 557: Earth rune  = 3 , 
	 *	558: Mind rune   = 4 , 559: Body rune   = 5 , 560: Death rune  = 6  , 561: Nature rune = 7
	 *	562: Chaos rune  = 8 , 563: Law rune    = 9 , 564: Cosmic rune = 10 , 565: Blood rune  = 11
	 *	566: Soul rune   = 12
	 *
		Type: 64	Searches:  64,		Description: Spell on Interface.
		Type: 80	Searches:  80,		Description: Spell on Inventory Item.
		Type: 96	Searches:  96,		Description: Spell on NPC.
		Type: 112	Searches: 112,		Description: Spell on Ground Item.
		Type: -128	Searches: 128,		Description: Spell on Object.
		Type: -112	Searches: 144,		Description: Unused.
		Type: -96	Searches: 160,		Description: Spell on Player.
		Type: -80	Searches: 176,		Description: Unused.
		Type: -32	Searches:  96, 160,	Description: Spell on NPC or Player.
		
		Type: Add 1 through 15 to place an item requirement.
	*/

	private static final byte[][] SPELL_BOOK = 
	{
		//Type1 = Packet Type Verification.
		//Type2 = Spell Category within that Packet Type.

		//-Rune1--Rune2--Rune3-----------Type1--Type2--Level----Child---Description---  
		{				 64,	0,	0  },  // 0  - Home Teleport				
		{   1, 2,  1, 4,		-32,	0,	1  },  // 1  - Wind Strike
		{   3, 1,  2, 3,  1, 5,		-32,	1,	3  },  // 2  - Confuse
		{				 64,	2,	1  },  // 3  - Enchant Crossbow Bolt
		{   1, 2,  1, 1,  1, 4,		-32,	0,	5  },  // 4  - Water Strike
		{   1, 1,  1, 10,		 80,	0,	7  },  // 5  - Level 1 - Sapphire Enchantment
		{   1, 2,  2, 3,  1, 4,		-32,	0,	9  },  // 6  - Earth Strike
		{   3, 1,  2, 3,  1, 5,		-32,	1,	11 },  // 7  - Weaken
		{   2, 2,  3, 0,  1, 4,		-32,	0,	13 },  // 8  - Fire Strike
		{   2, 1,  2, 3,  1, 7,		 64,	0,	15 },  // 9  - Bones To Banana
		{   2, 2,  1, 8,		-32,	0,	17 },  // 10 - Wind Bolt
		{   2, 1,  3, 3,  1, 5,		-32,	0,	19 },  // 11 - Curse
		{   3, 1,  3, 3,  2, 7,		-32,	0,	20 },  // 12 - Bind
		{   3, 0,  1, 7,		 80,	0,	21 },  // 13 - Low Level Alchemy
		{   2, 2,  2, 1,  1, 8,		-32,	0,	23 },  // 14 - Water Bolt
		{   3, 2,  1, 0,  1, 9,		 64,	0,	25 },  // 15 - Varrock Teleport
		{   3, 2,  1, 10,		 80,	0,	27 },  // 16 - Level 2 - Emerald Enchantment
		{   2, 2,  3, 3,  1, 8,		-32,	0,	29 },  // 17 - Earth Bolt
		{   3, 2,  1, 3,  1, 9,		 64,	0,	31 },  // 18 - Lumbridge Teleport
		{   1, 2,  1, 9,		 112,	0,	33 },  // 19 - Telekinetic Grab
		{   3, 2,  4, 0,  1, 8,		-32,	0,	35 },  // 20 - Fire Bolt
		{   3, 2,  1, 1,  1, 9,		 64,	0,	37 },  // 21 - Falador Teleport
		{   2, 2,  2, 3,  1, 8,		 96,	0,	39 },  // 22 - Crumble Undead
		{   1, 2,  1, 3,  1, 9,		 64,	0,	40 },  // 23 - Teleport To House
		{   3, 2,  1, 6,		-32,	0,	41 },  // 24 - Wind Blast
		{   4, 0,  1, 7,		 80,	0,	43 },  // 25 - Superheat Item
		{   5, 2,  1, 9,		 64,	0,	45 },  // 26 - Camelot Teleport
		{   3, 1,  3, 2,  1, 6,		-32,	0,	47 },  // 27 - Water Blast
		{   5, 0,  1, 10,		 80,	0,	49 },  // 28 - Level 3 - Ruby Enchantment
		{   5, 0,  1, 6,		-32,	0,	50 },  // 29 - Iban Blast
		{   4, 3,   4, 1,  3, 7,	-32,	0,	50 },  // 30 - Snare
		{   4, 4,   1, 6,		-32,	0,	50 },  // 31 - Magic Dart
		{   2, 9,   2, 1,		 64,	0,	51 },  // 32 - Ardougne Teleport
		{   1, 6,   3, 2,  4, 3,	-32,	0,	53 },  // 33 - Earth Blast
		{   5, 0,   1, 7,		 80,	0,	55 },  // 34 - High Alchemy
		{  30, 1,   3, 10, 		-128,	0,	56 },  // 35 - Charge Water Orb
		{  10, 3,   1, 10,		 80,	0,	57 },  // 36 - Level 4 - Diamond Enchantment
		{   2, 9,   2, 3,		 64,	0,	58 },  // 37 - Watchtower Teleport
		{   5, 0,   4, 2,  1, 6,	-32,	0,	59 },  // 38 - Fire Blast
		{  30, 3,   3, 10,		-128,	0,	60 },  // 39 - Charge Earth Orb
		{   2, 7,   4, 1,  4, 3,	 64,	0,	60 },  // 40 - Bones to Peaches
		{   2, 0,   4, 2,  2, 11,	-32,	0,	60 },  // 41 - Saradomin Strike
		{   1, 0,   4, 2,  2, 11,	-32,	0,	60 },  // 42 - Claws of Guthix
		{   4, 0,   1, 2,  2, 11,	-32,	0,	60 },  // 43 - Flames of Zamorak
		{   2, 9,   2, 0,		 64,	0,	61 },  // 44 - Trollheim Teleport
		{   5, 2,   1, 11,		-32,	0,	62 },  // 45 - Wind Wave
		{  30, 0,   3, 10,		-128,	0,	63 },  // 46 - Charge Fire Orb
		{   2, 0,   2, 1,  2, 9,	 64,	0,	64 },  // 47 - Teleport to Ape Atoll
		{   7, 1,   5, 2,  1, 9,	-32,	0,	65 },  // 48 - Water Wave
		{  30, 2,   3, 10,		-128,	0,	66 },  // 49 - Charge Air Orb
		{   5, 3,   5, 1,  1, 12,	-32,	0,	66 },  // 50 - Vulnerability
		{  15, 3,  15, 1,  1,  9,	 80,	0,	68 },  // 51 - Level 5 - Dragonstone Enchantment
		{   7, 3,   5, 2,  1, 11,	-32,	0,	70 },  // 52 - Earth Wave
		{   8, 3,   8, 1,  1, 12,	-32,	0,	73 },  // 53 - Enfeeble
		{   1, 9,   1, 3,  1, 12,	-96,	2,	74 },  // 54 - Teleother Lumbridge
		{   7, 0,   5, 2,  1, 11,	-32,	0,	75 },  // 55 - Fire Wave
		{   5, 3,   5, 1,  4,  7,	-32,	0,	79 },  // 56 - Entangle
		{  12, 3,  12, 1,  1, 12,	-32,	0,	80 },  // 57 - Stun
		{   3, 0,   3, 2,  3, 11,	-32,	1,	80 },  // 58 - Charge
		{   1, 9,   1, 1,  1, 12,	-96,	2,	82 },  // 59 - Teleother Falador
		{   1, 9,   1, 6,  1,  8,	-32,	1,	85 },  // 60 - Teleblock
		{  20, 0,  20, 3,  1, 10,	 80,	0,	87 },  // 61 - Level 6 - Oynx Enchantment
		{   1, 9,   2, 12,		-96,	2,	90 }   // 62 - Teleother Camelot

	};

	private static final short[][] STAFF_CHECK_IDS =
	{
		{ 1387, 1393, 1401, 3053, 3054, 11736, 11738,	}, //Fire Based Rune Staves.
		{ 1383, 1395, 1403, 6562, 6563, 11736, 11738,	}, //Water Based Rune Staves.
		{ 1381, 1397, 1405,				}, //Air Based Rune Staves.
		{ 1385, 1399, 1407, 3053, 3054, 6562,  6562	}  //Earth Based Rune Staves.
	};

	private static final short[] MAGIC_REQUIRED_ITEMS = 
	{
		1409, 4170, 2415, 2416, 2417, //Iban, Slayer, Saradomin, Guthix, Zamorak.
		567,  1963		      //Unpowered, Banana
	};

	private static final short[][] SPELL_PROJECTILE_DATA =
	{
			//Anim  Projectile  StartGfx  EndGfx  EyeOffset 
			//StartHeight, EndHeight, ProjectileHeight
		{},			
		{711,   91,  90,  92, 48,  96,  96, 40, 32, 48},	// 1 - Wind Strike
		{716,  103, 102, 104, 64,  96,  96, 40, 32, 48},	// 2 - Confuse
		{},
		{711,   94,  93,  95, 48,  96,  96, 40, 32, 48},	// 4 - Water Strike
		{},
		{711,   97,  96,  98, 48,  96,  96, 40, 32, 48},	// 6 - Earth Strike
		{717,  106, 105, 107, 64,  96,  96, 40, 32, 48},	// 7 - Weaken
		{711,  100,  99, 101, 48,  96,  96, 40, 32, 48},	// 8 - Fire Strike
		{},
		{711,  118, 117, 119, 48,  96,  96, 40, 32, 48},	// 10 - Wind Bolt
		{718,  109, 108, 110, 80,  96,  96, 48, 32, 48},	// 11 - Curse
		{710,  178, 177, 181, 64, 112, 112, 48, 16, 64},	// 12 - Bind
		{},
		{711,  121, 120, 122, 48,  96,  96, 40, 32, 48},	// 14 - Water Bolt
		{},
		{},
		{711,  124, 123, 125, 48,  96,  96, 40, 32, 48},	// 17 - Earth Bolt
		{},
		{},
		{711,  127, 126, 128, 48,  96,  96, 40, 32, 48},	// 20 - Fire Bolt
		{},
		{729,  146, 145, 147, 48,  96,  96, 40, 32, 48},	// 22 - Crumble Undead
		{},
		{711,  133, 132, 134, 48,  96,  96, 40, 32, 48},	// 24 - Wind Blast
		{},
		{},
		{711,  136, 135, 137, 48,  96,  96, 40, 32, 48},	// 27 - Water Blast
		{},
		{708,   88,  87,  89, 48,  96,  96, 40, 32, 48},	// 29 - Iban Blast
		{710,  178, 177, 180, 64, 112, 112, 48, 16, 64},	// 30 - Snare
		{1576, 328, 327, 329, 80,  96,  96, 40, 32, 48},	// 31 - Magic Dart
		{},
		{711,  139, 138, 140, 48,  96,  96, 40, 32, 48},	// 33 - Earth Blast
		{},
		{},
		{},
		{},
		{711,  130, 129, 131, 48,  96,  96, 40, 32, 48},	// 38 - Fire Blast
		{},
		{},
		{811,   -1,  -1,  76, 48,  -1, 112, -1, -1, 48},	// 41 - Saradomin Strike
		{811,   -1,  -1,  77, 48,  -1,  96, -1, -1, 48},	// 42 - Guthix Claws
		{811,   -1,  -1,  78, 48,  -1,   0, -1, -1, 48},	// 43 - Zamorak Flames
		{},
		{727,  159, 158, 160, 64,  96,  96, 40, 32, 48},	// 45 - Wind Wave.
		{},
		{},
		{727,  162, 161, 163, 64,  96,  96, 40, 32, 48},	// 48 - Water Wave.
		{},
		{722,  168, 167, 169, 64,  96, 144, 40, 32, 48},	// 50 - Vulnerability.
		{},
		{727,  165, 164, 166, 64,  96,  96, 40, 32, 48},	// 52 - Earth Wave.
		{723,  171, 170, 172, 48,  96,  96, 40, 32, 48},	// 53 - Enfeeble.
		{709,   -1, 343,  -1, 48,  96,  -1, -1, -1, 48},	// 54 - Teleother Lumbridge.
		{727,  156, 155, 157, 64,  96,  96, 40, 32, 48},	// 55 - Fire Wave.
		{710,  178, 177, 179, 64, 112, 112, 48, 16, 64},	// 56 - Entangle.
		{724,  174, 173, 175, 64,  96, 112, 24, 16, 48},	// 57 - Stun.
		{},
		{709,   -1, 343,  -1, 48,  96,  -1, 40, 32, 48},	// 59 - Teleother Falador.
		{1819, 344,  -1, 345, 48,  -1,   0, 40, 32, 48},	// 60 - Teleblock.
		{},
		{709,   -1, 343,  -1, 48,  96,  -1, -1, -1, 48},	// 62 - Teleother Camelot.
	};

	private static String[] RUNES = 
	{
		"Fire",  "Water", "Air",    "Earth",
		"Mind",  "Body",  "Death",  "Nature",
		"Chaos", "Law",   "Cosmic", "Blood", 
		"Soul"
	};


	public static void castProjectile(Player me, Player entity, int child)
	{
		int distance = ((int)me.location().getDistance(entity.location())) * 8;

		short[] data = SPELL_PROJECTILE_DATA[child];

		int speed = data[9] + distance;
		if (speed > 96)
			speed = 96;

		me.updateFlags().setFaceToPlayer(entity.INDEX);
		me.updateFlags().setForcedAnimation(data[0]);
		if (data[1] > -1)
			me.packetDispatcher().sendProjectile(entity, data[1], data[7], data[8], speed, data[9], 12, data[4]);
		if (data[2] > -1)
			me.updateFlags().setGraphic(data[2], 0, data[5]);
		if (data[3] > -1)
			entity.updateFlags().setGraphic(data[3], speed, data[6]);
		me.packetDispatcher().sendMessage("Distance Addition: " + distance);
	}

    /*
     * This is needed for all spells. It does not delete it runes. It only validates you can cast spell NOT if you are allowed to.
     * Then returns -1 for failed or Type2 (Category) the spell is used in. 
     * E.g. It ensures we can't "Teleport to Varrock" on a player  or   High Alchemy an NPC.
     * So it checks that Type1 matched, We have a high enough magic level, and the spell actually exists.
     */
	public static byte validate(Player me, int child, int type) 
	{
		if ((child & 0x3F) >= SPELL_BOOK.length)
			return -1;

		byte[] data = SPELL_BOOK[child];

		if(me.skills().levels[6] < data[data.length - 1])
		{
			me.packetDispatcher().sendMessage("You do not currently have a high enough magic level to cast this spell.");
			return -1;
		}

		if ((data[data.length - 3] & (0x30 | type)) != type)
		{
			me.packetDispatcher().sendMessage("[DEBUG] - Packet Type Mismatch " + child + "," + type);
			return -1;
		}

		return data[data.length - 2];
	}

	public static boolean hasRequirements(Player me, int child)
	{
		byte[] data = SPELL_BOOK[child];
	
		int size = data.length - 3; //Index of Packet Type.
		
		int item = data[size] & 0xF;
		int n = -1;	

		if (item > 0)
		{
			if(item < 6)
			{
				if(me.equipment().items[3] != MAGIC_REQUIRED_ITEMS[item])
					return false;
			} else if((n = me.inventory().getLastItemSlot(MAGIC_REQUIRED_ITEMS[item])) == -1)
				return false;
		}

		byte[] spell_slots = { -1, -1, -1, (byte)n }; //Index's of all deleted items in inventory.

		for ( ; size > 1 ; )
		{
			n = data[--size];
			if(n < 4)
			{
				for (short s : STAFF_CHECK_IDS[n])
				{
					if(me.equipment().items[3] == s)
					{
						--size;
						n = -1; //Staff Found 
						break;
					}
				}
			}

			if(n == -1)
				break;
			n = me.inventory().getLastItemSlot((item = n) + 554, data[--size]);

			if(n == -1)
			{
				me.packetDispatcher().sendMessage((new StringBuilder()).append("You don't have enough ").append(RUNES[item]).append(" Runes to cast this spell").toString());
				return false;
			}
			spell_slots[size >> 1] = (byte)n; //Set the spell slots to their delete index of "N".
		}
		for ( n = -1 ; n < 3 ; )
		{
			if(spell_slots[++n] == -1)
				continue;
			me.inventory().deleteWithoutChecks(spell_slots[n], data[n << 1]);
		}
		return true;
	}	
}
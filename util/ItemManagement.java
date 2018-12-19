/*
* @ Author - Digistr.
* @ Info - Handles everything to do with game items. 
* @ moreinfo - Yes it's all done using arrays and is very efficient. No loops required with this system.
*/

package com.util;

import com.model.Player;

public class ItemManagement 
{
	private static int WEAPON_BEGIN_INDEX;

	private static int HIDE_HEAD_ID    = 1155;
	private static int HIDE_BEARD_START = 4762;
	private static int HIDE_BEARD_END  = 4164;
	private static int HIDE_ARMS_END   = 4967;

	public static short[] GAME_ITEMS = new short[11791];
	private static int[] STORE_VALUES = new int[11791];
	private static boolean[] STACKABLES = new boolean[11791];
	private static short[] NOTE_IDS = new short[11791];

	private static byte[] WIELD_LOCATIONS;
	private static byte[] WEAPON_SPEEDS;
	private static short[] SIDEBAR_INTERFACES;
	private static short[][] WEAPON_ANIMATIONS;
	private static short[] DEFAULT_ANIMATION = {808,823,819,820,821,822,824,422,423,422,422,424};

	public static short getItemFromIndex(int index) 
	{
		for (short s = 0; s < GAME_ITEMS.length; ++s)
			if (GAME_ITEMS[s] == index)
				return s;
		return -1;
	}

	public static int storeValue(int item) {
		return STORE_VALUES[item];
	}

	public static int highAlchValue(int item) {
		return (int)(STORE_VALUES[item] * 0.6);
	}

	public static int lowAlchValue(int item) {
		return (int)(STORE_VALUES[item] * 0.4);
	}

	public static boolean isArrayStackable(int item) {
		if (item < 0)
			return false;
		return STACKABLES[item];
	}

	public static int getNoteId(int item) {
		if (item < 0)
			return -1;
		return NOTE_IDS[item];
	}

	public static boolean hasSpecial(int item) {
		if (item < 0 || item > 11790)
			return false;
		return GAME_ITEMS[item] <= GAME_ITEMS[11061];	
	}

	public static byte getWieldLocation(int item) 
	{
		if (item == -1 || item > 11790)
			return -1;
		int index = GAME_ITEMS[item];
		if (index < 0 || index >= WIELD_LOCATIONS.length)
			return -1;
		return WIELD_LOCATIONS[index];
	}

	public static short getSidebarInterface(int item) {
		if (item == -1 || item > 11790)
			return 92;
		int index = GAME_ITEMS[item] - WEAPON_BEGIN_INDEX;
		if (index < 0 || index >= SIDEBAR_INTERFACES.length)
			return 92;
		return SIDEBAR_INTERFACES[index];
	}

/*
* No indexing system no longer needs to be updated and will update its-self.
*/
	public static boolean showHead(int item) 
	{
		if (item < 0)
			return true;
		if (GAME_ITEMS[item] < GAME_ITEMS[HIDE_HEAD_ID])
			return true;
		return false;
	}

	public static boolean removeBeard(int item) 
	{
		if (item < 0)
			return false;
		int index = GAME_ITEMS[item];
		if (index < GAME_ITEMS[HIDE_BEARD_START] && index > GAME_ITEMS[HIDE_BEARD_END])
			return false;
		return true;
	}

	public static boolean showArms(int item) 
	{
		if (item < 0)
			return true;
		if (GAME_ITEMS[item] > GAME_ITEMS[HIDE_ARMS_END])
			return true;
		return false;
	}

	public static boolean isTwoHanded(int item) {
		if (item < 0)
			return false;
		return GAME_ITEMS[item] > GAME_ITEMS[11061] && GAME_ITEMS[item] < GAME_ITEMS[10858];
	}

	public static byte getWeaponSpeed(int item) {
		if (item == -1)
			return 4;
		return WEAPON_SPEEDS[GAME_ITEMS[item] - WEAPON_BEGIN_INDEX];
	}

	public static short getAppearenceAnimation(int item, int slot) 
	{
		if (item == -1 || item > 11790)
			return DEFAULT_ANIMATION[slot];
		int index = GAME_ITEMS[item] - WEAPON_BEGIN_INDEX;
		if (index < 0 || index >= WEAPON_ANIMATIONS.length)
			return DEFAULT_ANIMATION[slot];
		return WEAPON_ANIMATIONS[index][slot];
	}

	public static short getWeaponAnimation(int item, int box) 
	{
		if (item < 0)
			return DEFAULT_ANIMATION[box + 7];
		int index = GAME_ITEMS[item] - WEAPON_BEGIN_INDEX;
		if (index < 0 || index >= WEAPON_ANIMATIONS.length)
			return DEFAULT_ANIMATION[box + 7];
		return WEAPON_ANIMATIONS[index][box + 7];
	}

	public static short getBlockAnimation(int item) {
		if (item < 0)
			return DEFAULT_ANIMATION[11];
		int index = GAME_ITEMS[item] - WEAPON_BEGIN_INDEX;
		if (index < 0 || index >= WEAPON_ANIMATIONS.length)
			return DEFAULT_ANIMATION[11];
		return WEAPON_ANIMATIONS[index][11];	
	}

	public static void load() 
	{	
		FileBuilder stacks = new FileBuilder(FileManagement.readFile("item/stackables.dat",8044));
		for (int readerIndex = -1; ++readerIndex < 4022;)
			STACKABLES[stacks.readShort()] = true;

		FileBuilder stores = new FileBuilder(FileManagement.readFile("item/shopvalues.dat",47164));
		for (int readerIndex = -1; ++readerIndex < 11791;)
			STORE_VALUES[readerIndex] = stores.readInt();

		for (int i = 0; i < 11791; ++i)
			GAME_ITEMS[i] = -1;

		int length = (int)(new java.io.File("data/item/itemlist.dat")).length();
		FileBuilder gameItems = new FileBuilder(FileManagement.readFile("item/itemlist.dat", length));
		int size = (int)(length * 0.25);

		for (int readerIndex = -1; ++readerIndex < size; ) 
		{
			short position = (short)gameItems.readShort();
			short item = (short)gameItems.readShort();
			GAME_ITEMS[item] = position;
		}

		int LAST_EQUIPMENT_ID = 11229;
		int LAST_WEAPON_ID    = 11738;
		int FIRST_WEAPON_ID   = 35;

		int WEAPON_SIZE = 1 + GAME_ITEMS[LAST_WEAPON_ID] - GAME_ITEMS[FIRST_WEAPON_ID];
		WEAPON_BEGIN_INDEX = GAME_ITEMS[FIRST_WEAPON_ID];

		WIELD_LOCATIONS = new byte[GAME_ITEMS[LAST_EQUIPMENT_ID] + 1];
		WEAPON_SPEEDS = new byte[WEAPON_SIZE];	
		SIDEBAR_INTERFACES = new short[WEAPON_SIZE];
		WEAPON_ANIMATIONS = new short[WEAPON_SIZE][12];

		length = (int)(new java.io.File("data/item/wieldslots.dat")).length();
		FileBuilder wieldLocs = new FileBuilder(FileManagement.readFile("item/wieldslots.dat", length));
		while (wieldLocs.readableBytes() > 2)
		{
			int item = wieldLocs.readShort();
			WIELD_LOCATIONS[GAME_ITEMS[item]] = (byte)wieldLocs.readByte();
		}

		length = (int)(new java.io.File("data/item/wep_animations.dat")).length();
		FileBuilder wepAnims = new FileBuilder(FileManagement.readFile("item/wep_animations.dat", length));
		while (wepAnims.readableBytes() > 25)
		{
			int item = wepAnims.readShort();
			for (int idx = 0; idx < 12; idx++)
				WEAPON_ANIMATIONS[GAME_ITEMS[item] - WEAPON_BEGIN_INDEX][idx] = (short)wepAnims.readShort();
		}

		for (int i = 0; i < WEAPON_SIZE; i++)
		{
			WEAPON_SPEEDS[i] = (byte)4;
		}

		length = (int)(new java.io.File("data/item/sidebars.dat")).length();
		FileBuilder sidebars = new FileBuilder(FileManagement.readFile("item/sidebars.dat", length));
		for (int readerIndex = -1; ++readerIndex < 594;)
		{
			int item = sidebars.readShort();
			SIDEBAR_INTERFACES[GAME_ITEMS[item] - WEAPON_BEGIN_INDEX] = (short)sidebars.readShort();
		}

		length = (int)(new java.io.File("data/item/noteditems.dat")).length();
		FileBuilder notes = new FileBuilder(FileManagement.readFile("item/noteditems.dat", length));
		for (int readerIndex = -1; ++readerIndex < 11791;)
			NOTE_IDS[readerIndex] = (short)notes.readShort();

		System.out.println("Finished Loading Game Item Data.");
	}

}
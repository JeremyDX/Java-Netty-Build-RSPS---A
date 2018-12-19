/*
* @ Author - Digistr.
* @ info - Handles Prayer Level Reqs, Online, Turn Off/On, and Drainage.
*/

package com.model;

import com.util.Task;
import com.util.TaskQueue;

public class Prayer 
{

    /*
    * Prayer Task for executing. Set To EMPTY until needed.
    */ 
	private Task pray_task = Task.EMPTY;

    /*
    * This integer stores 26 online / offline prayer options.
    */
	private int prayers_online = 0;

    /*
    * This is a value that contains the amount to drain each round
    * When prayers are turn on/off you add/remove that amount from this.
    */
	private byte drain_per_cycle = 0;

    /*
    * Every cycle you add the "drain_per_cycle".
    * You then get the amount of times 60 goes into this value and remove it from your prayer level.
    * E.g. 116 removes 1 Prayer point and we now have 56 remainder.
    */
	private byte remainder = 0;


    /*
    * an array containg the config ids for all the prayers.
    */
	private static final short[] CONFIGS =
	{
	    83,  84,  85,  862, 863,  //0 - 4
	    86,  87,  88,  89,  90,   //5 - 9
	    91,  864, 865, 92,  93,   //10 - 14
	    94,  95,  96,  97,  866,  //15 - 19
	    867, 98,  99,  100, 1052, //20 - 24
	    1053 		      //25 - 25
	};


    /*
    * an array containg the level requirements for all the prayers.
    */
	private static final byte[] LEVEL_REQUIREMENTS = 
	{
	    1, 4, 7, 8, 9,
	    10, 13, 16, 19, 22,
	    25, 26, 27, 28, 31,
	    34, 37, 40, 43, 44,
	    45, 46, 49, 52, 65,
	    70
	};

    /*
    * This contain an array of how much each prayer drains your available amount.
    */
	private static final byte[] DRAIN_INCREASE = 
	{
	   3, 3, 3, 3, 3,
	   6, 6, 6, 1, 2,
	   2, 6, 6, 12, 12,
	   12, 12, 12, 12, 12,
	   12, 3, 6, 18, 24,
	   24
	};

    /*
    * 2D array containing all the indexes to be deactivated.
    */
	private static final byte[][] DEACTIVATES =
	{
	    {5, 13, 24, 25}, //0
	    {3, 4, 6, 11, 12, 14, 19, 20, 24, 25}, //1
	    {3, 4, 7, 11, 12, 15, 19, 20, 24, 25}, //2
	    {1, 2, 4, 6, 7, 11, 12, 14, 15, 19, 20, 24, 25}, //3
	    {1, 2, 3, 6, 7, 11, 12, 14, 15, 19, 20, 24, 25}, //4
	    {0, 13, 24, 25}, //5
	    {1, 3, 4, 11, 12, 14, 19, 20, 24, 25}, //6
	    {2, 3, 4, 11, 12, 15, 19, 20, 24, 25}, //7
	    {}, //8
	    {}, //9
	    {}, //10
	    {1, 2, 3, 4, 6, 7, 12, 14, 15, 19, 20, 24, 25}, //11	
	    {1, 2, 3, 4, 6, 7, 11, 14, 15, 19, 20, 24, 25}, //12
	    {0, 5, 24, 25}, //13
	    {1, 3, 4, 6, 11, 12, 19, 20, 24, 25}, //14
	    {2, 3, 4, 7, 11, 12, 19, 20, 24, 25}, //15
	    {17, 18, 21, 22, 23}, //16
	    {16, 18, 21, 22, 23}, //17
	    {16, 17, 21, 22, 23}, //18
	    {1, 2, 3, 4, 6, 7, 11, 12, 14, 15, 20, 24, 25}, //19
	    {1, 2, 3, 4, 6, 7, 11, 12, 14, 15, 19, 24, 25}, //20
	    {16, 17, 18, 22, 23}, //21
	    {16, 17, 18, 21, 23}, //22
	    {16, 17, 18, 21, 22}, //23
	    {0, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15, 19, 20, 25}, //24
	    {0, 1, 2, 3, 4, 5, 6, 7, 11, 12, 13, 14, 15, 19, 20, 24}  //25
	};

    /*
    * Creates a new instance of the prayer class.
    */
	protected Prayer() 
	{

	}


    /*
    * A Task is created and calls on this method when your prayers_online value is greater than ZERO.
    */
	public void execute(final Player player)
	{
		pray_task.die();
		if (!isOnline() || player == null)
		{
			return;
		}
		pray_task = new Task(1, true)
		{
			@Override
			public void execute() 
			{
				int bonus = 0; //bonus not available in server yet.
				remainder += drain_per_cycle;
				int remove_prayer = remainder / (60 + (bonus * 2));
				remainder -= (remove_prayer * (60 + (bonus * 2)));
				if (!player.skills().decrementLevel(5, remove_prayer))
				{
					player.prayer().reset(player);
					pray_task.die();
				}
				if (remove_prayer > 0)
					player.packetDispatcher().sendSkill(5);
				System.out.println("Remove: " + remove_prayer + " Remainder: " + remainder + " Drain: " + drain_per_cycle + " Online: " + prayers_online);
			}
		};
		TaskQueue.add(pray_task);
	}

    /*
    * This sets the remainder to 0. Along with calling deactivate prayers.
    * Used primarly for respawns or running out of prayer.
    */
	public void reset(Player player)
	{
		remainder = 0;
		deactivatePrayers(player);
	}

    /*
    * This turns all current prayers off and resets your prayers_online.
    */	
	public void deactivatePrayers(Player player)
	{
		for (int i = 0; i < CONFIGS.length; i++)
		{
			player.packetDispatcher().sendConfig(CONFIGS[i], 0);
		}
		prayers_online = 0;
		drain_per_cycle = 0;
	}


    /*
    * This will set a prayer online or offline and return it's config Id.
    * Only send prayer configs when its being turned off.
    */
	public short setPrayer(int location)
	{	
		int value = (2 << location);
		prayers_online ^= value; //Reverse
		boolean online = (prayers_online & value) == value;
		if (online)
		{
			drain_per_cycle += DRAIN_INCREASE[location];
		} else {
			drain_per_cycle -= DRAIN_INCREASE[location];
		}
		return CONFIGS[location];
	}


    /*
    * This returns all the prayer's that must be turned off when you turn a prayer online.
    */
	public static byte[] deactivates(int location)
	{
		return DEACTIVATES[location];
	}


    /*
    * This returns the level requirement to use this paticular prayer must be checked with users prayer level.
    */
	public static byte levelRequirement(int location)
	{
		return LEVEL_REQUIREMENTS[location];
	}


    /*
    * This checks if a prayer is currently online.
    */
	public boolean isOnline(int location)
	{
		int value = (2 << location);
		return (prayers_online & value) == value;
	}


    /*
    * This checks if the player currently has any prayers online.
    */
	public boolean isOnline()
	{
		return prayers_online != 0;
	}

   /*
   * This isn't an ideal way I'd rather have skills go to 9900 and use the last 2 zero's as a decimal location.
   * This needs to be saved so when a player logs in again the prayer will drain accordingly.
   */
	public byte getRemainder()
	{
		return remainder;
	}

} 

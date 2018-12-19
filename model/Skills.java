/*
* @ Author - Digistr.
* @ info - Handles everything to do with the Players Skills.
*/

package com.model;

import com.util.ItemManagement;

public class Skills 
{
	private Player player;

	public byte[] levels = new byte[23];
	public byte[] mainLevels = new byte[23];
	public int[] exp = new int[23];
	public byte combatLevel = 3;

	private static final int[] EXP_ARRAY = 
	{
		0,83,174,276,388,512,650,801,969,1154,1358,
		1584,1833,2107,2411,2746,3115,3523,3973,4470,
		5018,5624,6291,7028,7842,8740,9730,10824,12031,
		13363,14833,16456,18247,20224,22406,24815,27473,
		30408,33648,37224,41171,45529,50339,55649,61512,
		67983,75127,83014,91721,101333,111945,123660,
		136594,150872,166636,184040,203254,224466,247886,
		273742,302288,333804,368599,407015,449428,496254,
		547953,605032,668051,737627,814445,899257,992895,
		1096278,1210421,1336443,1475581,1629200,1798808,
		1986068,2192818,2421087,2673114,2951373,3258594,
		3597792,3972294,4385776,4842295,5346332,5902831,
		6517253,7195629,7944614,8771558,9684577,10692629,
		11805606,13034431,13034431
	};

	protected Skills(Player player) 
	{
		for (int i = 0; i < levels.length; i++) 
		{
			levels[i] = 1;
			mainLevels[i] = 1;
			exp[i] = 0;
		}
		levels[3] = 10;
		mainLevels[3] = 10;
		exp[3] = 1154;
		this.player = player;
	}

	public void setCombatLevel()
	{
		combatLevel = getNextLevel();	
	}

	public void reset() 
	{
		for (int i = 0; i < levels.length; ++i)
			levels[i] = mainLevels[i];
	}

   /*
   * Increase's your skill level such as when you eat a piece of food or drink a potion.
   * if level is greated then mainLevel we reset it back to mainLevel then increment.
   */
	public void incrementLevel(int level, int amount) {
		if (levels[level] > mainLevels[level])
			levels[level] = mainLevels[level]; //Set To Maximum , Then Add Extra.
		levels[level] += amount;
	}

   /*
   * Lowers your skill level such as when you take damage or the potion boosts start to wear off.
   */ 
	public boolean decrementLevel(int level, int amount) {
		levels[level] -= amount;
		if (levels[level] < 0)
		{
			levels[level] = (byte) 0;
			return false;
		}
		return true;
	}

	public int getPercentRemaining(int level) {
		double d = levels[level] / mainLevels[level];
		return (int)(100 * d);
	}

   /*
   * Similar to the ::master command allows you to set your level to any level with correct EXP super FAST!
   */
	public void setLevel(int level, int levelToBe) 
	{
		if (levelToBe > 0 || levelToBe < 100) 
		{
			levels[level] = (byte)levelToBe;
			mainLevels[level] = (byte)levelToBe;
			exp[level] = EXP_ARRAY[levelToBe - 1];
			combatLevel = getNextLevel();
			if (level < 7)
			{
				combatLevel = getNextLevel();
				int sidebar = ItemManagement.getSidebarInterface(player.equipment().items[3]);
				player.packetDispatcher().sendInterfaceString("Combat Level: " + combatLevel, sidebar, 23);
			}
		}
	}

   /*
   * When a player needs to recieve EXP we call this method and it handles EXP correctly.
   */
	public byte addSkillExp(int level, int experience) {
		exp[level] += experience;
		if (exp[level] > 200000000)
			exp[level] = 200000000;
		int changed = 0;
		for (int i = mainLevels[level] + 1; i < 100; i++) {
			if(EXP_ARRAY[i] > exp[level])
				break;
			++changed;
		}
		if (changed > 0) {
			if (levels[level] <= mainLevels[level])
				levels[level] += changed;
			mainLevels[level] += changed;
			if (level < 7)
				combatLevel = getNextLevel();
			int sidebar = ItemManagement.getSidebarInterface(player.equipment().items[3]);
			player.packetDispatcher().sendInterfaceString("Combat Level: " + combatLevel, sidebar, 23);
			return mainLevels[level];		
		}
		return -1;
	}

   /*
   * This is to only be called by the Skills class.
   * It is called when a players combat skills are leveled up.
   */
	private byte getNextLevel() {
		double numMeele = (mainLevels[0] * 0.325) + (mainLevels[2] * 0.325);
		double numRange = mainLevels[4] * 0.4825;
		double numMagic = mainLevels[6] * 0.4825;
		if(numMeele > numRange && numMeele > numMagic)
			return (byte) (numMeele + mainLevels[1] * 0.25 + mainLevels[3] * 0.25 + mainLevels[5] * 0.125);
		if(numRange > numMagic)
			return (byte) (numRange + mainLevels[1] * 0.25 + mainLevels[3] * 0.25 + mainLevels[5] * 0.125);
		return (byte) (numMagic + mainLevels[1] * 0.25 + mainLevels[3] * 0.25 + mainLevels[5] * 0.125);
	}

}
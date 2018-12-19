package com.model;

import com.util.Task;
import com.util.TaskQueue;
import com.util.ItemManagement;

public class Damage 
{

	private Task death_task = Task.EMPTY;
	
	private short index;
	private long[] players = new long[10];
	private short[] damages = new short[10];
	private short pos = 0;
	private short highPos = -1;

	protected Damage() {

	}

	public void setIndex(short index) {
		this.index = index;
	}
	

	private void expandList() {
		short[] damage = new short[damages.length + 10];
                System.arraycopy(damages, 0, damage, 0, damages.length);
		damages = new short[damage.length];
                System.arraycopy(damage, 0, damages, 0, damage.length);
		long[] player = new long[players.length + 10];
                System.arraycopy(players, 0, player, 0, players.length);
		players = new long[player.length];
                System.arraycopy(player, 0, players, 0, player.length);	
	}
	
    /*
    * Example:  dealDamage(0, "blue", 0); , dealDamage(50, "red", 0);
    */
	public void dealDamage(int damage, String colour, long attacker) {
		byte colours[] = {1, 0, 2, 3};
		dealDamage(damage, colours[colour.length() - 3], attacker);
	}

    /*
    * This handles the damage dealt to you.
    * This verifies damage with colour to ensure accurate hits.
    * Damage dealt over HP is reduced.
    */
	public void dealDamage(int damage, byte colour, long attacker) {
		final Player p = World.getPlayerByClientIndex(index);
		if (p.skills().levels[3] == 0)
			return;
		int health = p.skills().levels[3] - damage;
		if (health < 1) {
			damage = p.skills().levels[3];
			health = 0;
		}
		if (damage == 0)
			colour = 0;
		p.skills().levels[3] = (byte)health;
		p.updateFlags().hit((byte)damage, colour);
		if (damage > 0) {
			if (attacker != 0)
				addToKillList((byte)damage, attacker);
			p.packetDispatcher().sendSkill(3);
			p.updateFlags().setAnimation(ItemManagement.getBlockAnimation(p.equipment().items[3]), 0);
		}
		if (health == 0)
		{
			death();
		}
		else if (p.skills().getPercentRemaining(3) < 11 && p.equipment().items[12] == 2570)
		{
			p.equipment().erase(12);
			p.updateFlags().setForcedAnimation(714);
			TaskQueue.add(new Task(4, false) 
			{
				@Override
				public void execute() 
				{
					p.updateFlags().setForcedAnimation(715);
					p.setTeleport(3086, 3486, 0);
					p.packetDispatcher().sendMessage("Your ring of life saves you and is destroyed in the process.");
				}
			});
		} else {

		}	
	}

	private void death()
	{
		final Player p = World.getPlayerByClientIndex(index);
		p.updateFlags().setForcedAnimation(836);
		p.updateFlags().resetFaceTo();
		p.combat().resetAllVariables();
		if (!death_task.has_executed)
		{
	  	    death_task = new Task(6, true) 
		    {
			@Override
			public void execute() 
			{
				defaultDeath(3086, 3486, 0);
			}
		    };
		    death_task.has_executed = true;
	            TaskQueue.add(death_task);
		} else {
		    TaskQueue.revive(death_task);
		}
	}

	private void defaultDeath(int x, int y, int z)
	{	
		long t1 = System.nanoTime();
		Player p = World.getPlayerByClientIndex(index);
		int[] keepItems = p.itemsOnDeath().calculate(3);
		Player other = World.getPlayerByName(getKiller());
		p.inventory().release(other.details().USERNAME_AS_LONG, p.location(), keepItems);
		p.equipment().release(other.details().USERNAME_AS_LONG, p.location(), keepItems);
		other.packetDispatcher().sendMessage("You have defeated " + p.details().USERNAME_AS_STRING);
		reset();
		p.special().sendSpecialBar(92);
		p.packetDispatcher().sendTab(92, 99);
		p.packetDispatcher().sendConfig(43, p.fightType().box);
		p.fightType().wield(92);
		p.inventory().addItems(keepItems);
		p.packetDispatcher().sendMultiItems(387, 28, 93, p.equipment().items, p.equipment().amounts);
		p.setTeleport(x, y, z);
		p.updateFlags().setForcedAnimation(-1);
		p.updateFlags().forceAppearence();
		p.skills().reset();
		p.packetDispatcher().sendSkill(3);
		long t2 = System.nanoTime();
		System.out.println("Took " +(t2-t1)+" ns to kill a player..");
	}

    /*
    * Returns the long value of the player who dealt the most damage too you!
    */
	private long getKiller() 
	{
		if (highPos == -1)
			return World.getPlayerByClientIndex(index).details().USERNAME_AS_LONG;
		return players[highPos];
	}

	private void reset() 
	{
		pos = 0;
		highPos = -1;
		players = new long[10];
		damages = new short[10];
	}

	private void addToKillList(byte damage, long name) 
	{
		boolean inList= false;
		if (pos >= players.length)
			expandList();
		for(short pick = 0; pick < players.length; pick++) 
		{
		    if (players[pick] == name) 
		    {
             		inList = true;
			damages[pick] += damage;
			if(highPos == -1 || damages[pick] > damages[highPos])
				highPos = pick;
			break;
		    }
		}
		if (!inList) 
		{
			players[pos] = name;
			damages[pos] = damage;
			if (highPos == -1 || damages[pos] > damages[highPos])
				highPos = pos;
			++pos;
		}
		
	}
		
}
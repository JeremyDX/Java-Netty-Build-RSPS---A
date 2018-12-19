/*
* @ Author - Digistr.
*/

package com.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.lang.Math;

import com.util.Task;
import com.util.TaskQueue;
import com.util.ItemManagement;
import com.util.MagicSystem;

public class Combat {

	private Task task = Task.EMPTY;
	private Player me;

	private final Deque<Hit> HITS = new ArrayDeque<Hit>();

	public short attackingOn = -1;
	public short attackedBy = -1;

	public int combatCycle;
	public int hitDealtCycle;
	private short lastHitWeapon = -1;
	private byte lastHitDelay = -1;
	private int spellIndex = -1;

	protected Combat(Player me) 
	{
		this.me = me;
	}

	public void stopAttacking()
	{
		attackingOn = -1;
		spellIndex = -1;
	}

	public void resetAllVariables()
	{
		attackedBy = -1;
		stopAttacking();
	}

	public Player getAttackedBy()
	{
		return null;	
	}

	public boolean isInMultiCombat()
	{
		return true;
	}

	public void pushMagic(Player entity, int spellIndex, boolean autocast)
	{
		this.spellIndex = spellIndex;
		me.walkingQueue().reset();
		me.follow().setFollower(entity.INDEX);
		attackingOn = entity.INDEX;
		setAttackingOn(entity.INDEX);
	}

	public void pushMelee(short clientIndex)
	{
		if (attackingOn == clientIndex)
			return;
		me.follow().setFollower(clientIndex);
		attackingOn = clientIndex;
		setAttackingOn(clientIndex);
	}

	private void setAttackingOn(short clientIndex) 
	{	
		me.updateFlags().setFaceToPlayer(clientIndex);

		if (!task.has_executed)
		{
	  	    task = new Task(1, true) 
		    {
			@Override
			public void execute() 
			{
				Player other = World.getPlayerByCheckingIndex(attackingOn);

				if (other != null)
				{
					int sqX = other.location().x - me.location().x;
					int sqY = other.location().y - me.location().y;
					me.packetDispatcher().sendMessage("Combat Distance -> X: " + sqX + " , Y: " + sqY);
				}

				int timeElapsed = World.gameTickIndex - hitDealtCycle;
				
				if (timeElapsed >= lastHitDelay)
				{
					if (other == null)
					{
						stopAttacking();
						this.die();
						return;
					}

					hitDealtCycle = World.gameTickIndex;
					lastHitDelay = ItemManagement.getWeaponSpeed(lastHitWeapon);
					if (me.fightType().type == 13)
						--lastHitDelay;
					else if (me.fightType().type > 14)
						lastHitDelay = 5;
				} else {
					int remainder = lastHitDelay - timeElapsed;
					if (remainder == 1)
					{
						lastHitWeapon = me.equipment().items[3];
					}
				}
			}
		    };
		    task.has_executed = true;
		    TaskQueue.add(task);
		} else {
		    TaskQueue.revive(task);
		}
	}

	public void sendHits() 
	{
		/*Hit h = null;
		Player me = World.getPlayerByClientIndex(index);
		change();
		while ((h = HITS.poll()) != null) 
		{
			Player p = World.getPlayerByCheckingIndex(h.INDEX);
			if (p == null || p.skills().levels[3] == 0)
				continue;
			me.damage().dealDamage(h.DAMAGE, h.COLOR, p.details().USERNAME_AS_LONG);
			me.updateFlags().setAnimation(ItemManagement.getBlockAnimation(me.equipment().items[3]), 0);
		}*/
	}

	private byte calculateHit()
	{
		Player other = World.getPlayerByClientIndex(attackingOn);

		int oppenent = getValue(other.skills().levels[1], 1.0, other.equipment().bonus[me.fightType().bonusIndex()]);
		int mine = getValue(me.skills().levels[0], 1.0, me.equipment().bonus[me.fightType().bonusIndex() + 5]);

		if (oppenent > mine)
			return (byte)0;

		int maximum_hit = getMaxHit();
		double factor = oppenent / (double)mine;
		int shown_hit = (int)Math.ceil(maximum_hit * factor);

		return (byte)shown_hit;
	}

	public byte getMaxHit() 
	{
		double maximum_hit = me.skills().levels[2] / 4.0;
		return (byte)maximum_hit;
	}

	public static int getValue(byte level, double extra, short bonus)
	{
		return (int)(Math.random() * (level * extra + 32) * (bonus + 32));
	}

	private class Hit 
	{
		public final short INDEX;
		public final byte DAMAGE;
		public final byte COLOR;

		public Hit(short index, byte damage, byte color) 	
		{
			INDEX = index;
			DAMAGE = damage;
			COLOR = color;
		}
	}

}
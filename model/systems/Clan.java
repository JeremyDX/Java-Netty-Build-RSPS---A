package com.model.systems;

import com.model.Player;
import com.model.PlayerChat;
import com.model.World;
import com.util.GameUtility;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Clan
{
	public long displayName;
	public long owner;

	public byte speakerRank;
	public byte kickPlayerRank;
	public byte lootShareRank;

	public final HashMap<Long, Byte> members = new HashMap<Long, Byte>();

	public Clan(long owner, long displayName, int data)
	{
		this.owner = owner;
		this.displayName = displayName;	
		this.speakerRank = (byte)((data >> 8) & 0xFF);
		this.kickPlayerRank = (byte)((data >> 16) & 0xFF);
		this.lootShareRank = (byte)((data >> 24) & 0xFF);
	}

	public boolean addPlayer(Player user, int rank)
	{
		long me = user.details().USERNAME_AS_LONG;
		if (members.containsKey(me) || (members.size() > 99 && rank < 8))
			return false;
		user.packetDispatcher().buildClanListMembers(this);
		members.put(me, (byte)rank);
		Iterator iterator = members.keySet().iterator();
		while (iterator.hasNext())
		{
			long key = (long)iterator.next();
			World.getPlayerByName(key).packetDispatcher().updateClanRank(me, rank);  
		}
		return true;
	}

	public void updateListIfNeeded(Player owner)
	{
		Iterator<Entry<Long, Byte>> iterator = members.entrySet().iterator();
		while(iterator.hasNext()) 
		{
			Entry<Long, Byte> entry = iterator.next();
			long key = entry.getKey();
			Byte rank = owner.chat().FRIENDS.get(key);
			Player user = World.getPlayerByName(key);
			if (rank == null)
			{
				if (key == owner.details().USERNAME_AS_LONG)
					continue;
				rank = -1;
			}
			if (owner.chat().IGNORES.contains(key))
			{
				user.packetDispatcher().sendMessage("You have been banned from this clan chat.");
				for (Map.Entry<Long, Byte> clanMembers : members.entrySet())
					World.getPlayerByName(clanMembers.getKey()).packetDispatcher().updateClanRank(key | 0x8000000000000000L, 0);	
				user.packetDispatcher().clearClanListMembers();
				iterator.remove();
				continue;
			}
			if (rank < owner.chat().enterRank)
			{
				user.packetDispatcher().sendMessage("You are no longer high enough rank for this clan chat.");
				for (Map.Entry<Long, Byte> clanMembers : members.entrySet())
					World.getPlayerByName(clanMembers.getKey()).packetDispatcher().updateClanRank(key | 0x8000000000000000L, 0);	
				user.packetDispatcher().clearClanListMembers();
				iterator.remove();
				continue;
			}
			if (rank != members.get(key))
			{
				members.put(key, rank);
				for (Map.Entry<Long, Byte> clanMembers : members.entrySet())
					World.getPlayerByName(clanMembers.getKey()).packetDispatcher().updateClanRank(key, rank);
			}
		}
		speakerRank = owner.chat().speakerRank;
		kickPlayerRank = owner.chat().kickRank;
		lootShareRank = owner.chat().lootShareRank;
	}
}
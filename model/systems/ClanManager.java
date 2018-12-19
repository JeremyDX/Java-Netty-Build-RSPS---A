package com.model.systems;

import com.model.Player;
import com.model.PlayerChat;
import com.util.GameUtility;
import com.model.World;

import java.util.Iterator;
import java.util.HashMap;

public class ClanManager
{
	/*
	 * List of all the active clans in currently online. We add/remove as needed.
	 */
	public static final HashMap<Long, Clan> mapped_clans = new HashMap<Long, Clan>();
  
        /*
   	 * Servers will dynamically load clan data as requested.
    	 */ 
	public static void joinClan(Player me, long name)
	{	
		if ((me.chat().clanResponseCode & 0x10) == 0x10)
		{
			removePlayer(me);
			me.packetDispatcher().clearClanListMembers();
			me.chat().clanResponseCode = 0x0;		
			return;
		}
		me.packetDispatcher().sendMessage("Attempting to join clan chat...");
		me.chat().attemptingToJoin = name;
		me.chat().beginClanFileLoaderTask();
	}

	public static void removePlayer(Player me)
	{
		if ((me.chat().clanResponseCode & 0x10) != 0x10)
			return;
		Clan clan = mapped_clans.get(me.chat().attemptingToJoin);
		if (clan == null)
			return;
		clan.members.remove(me.details().USERNAME_AS_LONG);
		if (clan.members.size() == 0)
			mapped_clans.remove(me.chat().attemptingToJoin);
		else {
			Iterator iterator = clan.members.keySet().iterator();
			while (iterator.hasNext())
			{
				long key = (long)iterator.next();
				World.getPlayerByName(key).packetDispatcher().updateClanRank(me.details().USERNAME_AS_LONG | 0x8000000000000000L, 0);
			}
		}
	}

	public static void kickPlayer(Player me, long name)
	{
		Clan clan = mapped_clans.get(me.chat().attemptingToJoin);
		if (clan == null)
			return;
		if (clan.kickPlayerRank < clan.members.get(me.details().USERNAME_AS_LONG))
		{
			clan.members.remove(name);
			World.getPlayerByName(name).packetDispatcher().clearClanListMembers();
			Iterator iterator = clan.members.keySet().iterator();
			while (iterator.hasNext())
			{
				long key = (long)iterator.next();
				World.getPlayerByName(key).packetDispatcher().updateClanRank(name | 0x8000000000000000L, 0);
			}
		} else {
			me.packetDispatcher().sendMessage("You are not high enough rank to kick this player.");
		}	
	}
}
package com.model;

import com.packet.PacketBuilder;
import com.util.Task;
import com.util.TaskQueue;
import com.util.GameUtility;
import com.util.FileManagement;
import com.model.systems.Clan;
import com.model.systems.ClanManager;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class PlayerChat 
{

	private static final short WORLD = 1;

	public static final String[] RANKS = {
		"Anyone", "Any Friends", "Recruit", "Corporal", "Sergeant", "Lieutenant", "Captain", "General", "Only Me", "Disabled"
	};

	private Task friend_server = Task.EMPTY;
	private Task task = Task.EMPTY;

	public final HashMap<Long, Byte> FRIENDS = new HashMap<Long, Byte>(200);
	public final List<Long> IGNORES = new ArrayList<Long>(200);

	public byte chatPublic, chatPrivate, chatClan, chatTrade;
	public byte enterRank = 0, speakerRank = -1, kickRank = 7, lootShareRank = 8;

	public byte clanResponseCode;  //Response code for last clan attempt made if successful or not.
	public long attemptingToJoin;  //Clan we last attempted to join or successfully joined.

	public long myClanDisplayName; //Your clans displayName if set to 0 then YOU DO NOT HAVE A CLAN.

	private short index;

	/*
	* Constructor is used when we create the Player so we can save the FRIENDS before we obtain player index.
	*/
	public PlayerChat() 
	{

	}

	/*
	* This sets the players index so we can use this player in the methods below.
	*/
	public void setIndex(short index) {
		this.index = index;
	}

	public void updateFriendServer()
	{
		friend_server.die();
		Player me = World.getPlayerByClientIndex(index);
		FileManagement.updateFriendServer(me);
		Clan clan = null;
		if ((clan = ClanManager.mapped_clans.get(me.details().USERNAME_AS_LONG)) != null)
		{
			clan.updateListIfNeeded(me);
		}
	}

	public void beginClanFileLoaderTask()
	{
	    if(!task.has_executed)
	    {
		task = new Task(3, false)
		{
		    @Override
		    public void execute()
		    {
			task.die();
			final Player me = World.getPlayerByClientIndex(index);
			clanResponseCode = (byte)FileManagement.readClanFileData(me, attemptingToJoin);
		    }
		};
		task.has_executed = true;
		TaskQueue.add(task);
	    } else {
		TaskQueue.revive(task);
	    }
	}

	public void initiateClanChatUpdate()
	{
		final Player me = World.getPlayerByClientIndex(index);

		if (myClanDisplayName != 0)
			me.packetDispatcher().sendMessage("Your clan changes will take effect within the next minute.");

		if (!friend_server.has_executed)
		{
	  	    friend_server = new Task(60, false) 
		    {
			@Override
			public void execute() 
			{
				updateFriendServer();
				if (myClanDisplayName != 0)
					me.packetDispatcher().sendMessage("Your clan changes have been updated.");
			}
		    };
		    friend_server.has_executed = true;
	            TaskQueue.add(friend_server);
		} else {
	            TaskQueue.revive(friend_server);
		}	
	}

	/*
	* When chat's are changed via the client we call this method.
	* This isn't to be used on login as it does update the list when called.
	*/
	public void setChat(byte chatPublic, byte chatPrivate, byte chatTrade) {
		if (this.chatPrivate != chatPrivate) {
			this.chatPrivate = chatPrivate;
			updateList(this.chatPrivate < 2);
		}
		this.chatPublic = chatPublic;
		this.chatTrade = chatTrade;
	}
	
	/*
	* When AddFriend packet is called we sent this.
	*/
	public void addFriend(long name, int rank) 
	{
		if (FRIENDS.size() > 199 && !FRIENDS.containsKey(name))
			return;

		FRIENDS.put(name, (byte)rank);
		sendFriend(name, (byte)rank);
		initiateClanChatUpdate();
	}

	/*
	* When RemoveFriend packet is called we sent this.
	*/
	public void removeFriend(long name) 
	{
		Player otherPlayer = World.getPlayerByName(name);
		if (FRIENDS.remove(name) != null && otherPlayer != null) {
			long me = World.getPlayerByClientIndex(index).details().USERNAME_AS_LONG;
			Byte rank = otherPlayer.chat().FRIENDS.get(me);
			if (rank != null && chatPrivate == 1) {
				otherPlayer.getPacket().createPacket(200).addLong(me).addShort(0).addByte(rank);
			}
		}
		initiateClanChatUpdate();
	}
	
	/*
	* This is used to add a friend to your list and tell that friend you've added them.
	*/
	public void sendFriend(long name, byte clanRank) {
		Player me = World.getPlayerByClientIndex(index);
		Player otherPlayer = World.getPlayerByName(name);
		short world = 0;
		if (otherPlayer != null) {
			Byte rank = otherPlayer.chat().FRIENDS.get(me.details().USERNAME_AS_LONG);
			if (rank != null) {
				if (otherPlayer.chat().chatPrivate < 2)
					world = WORLD;
				if (chatPrivate < 2)
					otherPlayer.getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(WORLD).addByte(rank);		
			} else if (otherPlayer.chat().chatPrivate == 0)
					world = WORLD;
		}
		me.getPacket().createPacket(200).addLong(name).addShort(world).addByte(clanRank);
	}

	/*
	* This is to be used where you send all other stuff on login and only to be called 1 time on login.
	*/
	public void sendList() 
	{
		Iterator iterator = FRIENDS.keySet().iterator();
		while (iterator.hasNext())
		{
			long key = (Long)iterator.next();
			Byte rank = FRIENDS.get(key);
			sendFriend(key, rank);
		}
		updateList(chatPrivate < 2);
		sendIgnores();
	}

	/*
	* This is used to update all players so they know if your online or offline and how to correctly show you.
	* Depending on chatPrivate type you'll either be offline or online for the other players.
	*/
	public void updateList(boolean show) 
	{
		Player me = World.getPlayerByClientIndex(index);
		if (show) 
		{
			for (int i = 0; i < World.curIndex; i++) 
			{
				Byte rank = World.players[i].chat().FRIENDS.get(me.details().USERNAME_AS_LONG);
				if (rank != null) {
					if (chatPrivate == 0) {
						World.players[i].getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(WORLD).addByte(rank);
					} else {
						if (FRIENDS.containsKey(World.players[i].details().USERNAME_AS_LONG))
							World.players[i].getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(WORLD).addByte(rank);
						else
							World.players[i].getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(0).addByte(rank);
					}			
				}
			}
		} else {
			for (int i = 0; i < World.curIndex; i++) 
			{
				Byte rank = World.players[i].chat().FRIENDS.get(me.details().USERNAME_AS_LONG);
				if (rank != null)
					World.players[i].getPacket().createPacket(200).addLong(me.details().USERNAME_AS_LONG).addShort(0).addByte(rank);
			}
		}
	}

	public void addIgnore(long name) 
	{
		if (IGNORES.size() > 199)
			return;

		if (IGNORES.contains(name))
			return;

		IGNORES.add(name);
		initiateClanChatUpdate();
	}

	public void removeIgnore(long name) 
	{
		IGNORES.remove(name);
		initiateClanChatUpdate();
	}

	/*
	* This sets the ignore list.
	*/
	private void sendIgnores() 
	{
		PacketBuilder packet = World.getPlayerByClientIndex(index).getPacket();
		packet.createPacketTypeShort(173);
		for (long name : IGNORES) {
			packet.addLong(name);
		}
		packet.endPacketTypeShort();
	}
}
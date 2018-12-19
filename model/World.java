/*
* @ Author - Digistr
* @ Info - Resembles A PlayerHandler.java
*/

package com.model;

import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.ArrayDeque;
import java.util.Deque;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.codec.RSDecoder;
import com.codec.ChannelHandler;

import com.packet.Packet;
import com.packet.PacketBuilder;
import com.packet.PacketReceiver;

import com.util.FileManagement;
import com.util.GameUtility;
import com.util.ShopManagement;
import com.util.TaskQueue;

public class World {

	private static Deque<Player> LOGOUTS = new ArrayDeque<Player>();
	private static Deque<PlayerLoginDetails> LOGINS = new ArrayDeque<PlayerLoginDetails>();

	public static Player[] players;
	public static Npc[] npcs;

	private static short[] indexs;
	public static short curIndex = 0;
	public static short totalNpcs = 0;

	private static short saverIndex = 0;
	public static int gameTickIndex = 0;
	private static int restartOnTick = -1;

	public static void bootServer(int time_in_seconds)
	{
		restartOnTick = gameTickIndex + (int)((time_in_seconds + 1) / 0.6);
		for (int i = 0; i < curIndex; ++i)
		{
			players[i].packetDispatcher().sendWalkableInterface(57);
			players[i].packetDispatcher().sendInterfaceString(
				GameUtility.formatTimeForDisplay(restartOnTick - gameTickIndex, "Rebooting Server In: "), 57, 0);	
		}
	}

    /*
    * Creates The List Based On Size You Choose in Server.java
    */
	public static void createList(int plrSize, int npcSize) 
	{
		players = new Player[plrSize];
		indexs = new short[plrSize + 1];
		for (int i = 0; i <= plrSize; i++)
			indexs[i] = -1;

		npcs = new Npc[npcSize];
	}


    /*
    * Adds A Player into the current Index slot.
    */
	public static byte add(Player plr) 
	{
		if (curIndex >= players.length)
			return 7;
		if (getPlayerByName(plr.details().USERNAME_AS_LONG) != null)
			return 5;
		if (restartOnTick >= gameTickIndex)
			return 6;
		short index = 0;
		for ( ; ++index < indexs.length ;) {
			if (indexs[index] == -1) {
				break;
			}
		}
		indexs[index] = curIndex;
		players[curIndex++] = plr;
		plr.INDEX = index;
		plr.details().STATUS |= 0x2;
		return 2;
	}
	

    /*
    * Removes a player then shifts the array down.
    */
	public static boolean remove(Player plr) 
	{
		if ((plr.details().STATUS & 0x2) != 0x2)
			return false;
		plr.chat().updateList(false);
		plr.details().STATUS ^= 0x2; //Sets Inactive From Player List.
		if (curIndex <= saverIndex)
			--saverIndex;
		--curIndex;
		FileManagement.savePlayer(plr);
		for (short s = indexs[plr.INDEX]; s < curIndex; s++) 
		{
			players[s] = players[s + 1];
			indexs[players[s].INDEX] = s;
		}
		indexs[plr.INDEX] = -1;
		return true;
	}

    /*
    * Check's To Verify This Player Isn't On With too many connections.
    * To Change max connections modify (count < 2000). 2000 being the max aloud.
    */
	public static byte checkIpAddress(char[] IP) 
	{
		long t1 = System.nanoTime();
		int count = 0;
		for (int i = 0; i < curIndex; i++)
			if (Arrays.equals(IP, players[i].details().IP))
				++count;
		if (count < 2000)
			return 2;
		return 9;
		
	}

    /*
    *  Enters a players username (STRING) then retrieves back a Player object (PLAYER) or NULL if not found.
    */
	public static Player getPlayerByName(String USERNAME_AS_STRING) {
		for (int i = 0; i < curIndex; i++)
			if (players[i].details().USERNAME_AS_STRING.equals(USERNAME_AS_STRING))
				return players[i];
		return null;
	}


    /*
    *  Enters a players username (LONG) then retrieves back a Player object (PLAYER) or NULL if not found.
    */
	public static Player getPlayerByName(long USERNAME_AS_LONG) {
		for (int i = 0; i < curIndex; i++)
			if (players[i].details().USERNAME_AS_LONG == USERNAME_AS_LONG)
				return players[i];
		return null;
	}

	public static Npc getNpcByCheckingIndex(int index) {
		if (index < 0 || index >= npcs.length)
			return null;
		return npcs[index];
	}

    /*
    *  Enter in the clientindex to be checked then retrieves back a Player object (PLAYER) or NULL if not found.
    *  This is to be used when you cannot verify if the index is static.
    */
	public static Player getPlayerByCheckingIndex(int index) 
	{
		if (index < 0 || index >= indexs.length || indexs[index] == -1)
			return null;
		return players[indexs[index]];
	}

    /*
    *  This will instantly return the index checked! [WARNING] there is no verification.
    */
	public static Player getPlayerByClientIndex(int index) 
	{
		return players[indexs[index]];
	}

    /*
    * Process's All players. Read To Login After All Updates From Current players Have Been Finished.
    * This Prevents Any Glitches In Processing A Player That Doesn't Quite Exist Yet.
    */
	public static void queueLogins() 
	{
		PlayerLoginDetails d = null;
		while ((d = LOGINS.poll()) != null) 
		{
			final Player plr = new Player(d);
			PacketBuilder pb = new PacketBuilder(6);
			byte returnCode = checkIpAddress(d.IP);

			if (returnCode == 2)
				returnCode = FileManagement.loadPlayer(plr);

			if (returnCode == 2)
				returnCode = World.add(plr);

			pb.addByte(returnCode);

			if (returnCode != 2) 
			{
				plr.getSession().write(pb).addListener(ChannelFutureListener.CLOSE);
				continue;
			}

			plr.finish();
			pb.addByte((byte)(plr.RANK & 3));
			pb.addByte((byte) 0);
			pb.addShort(plr.INDEX);
			pb.addByte((byte) 1);

			plr.getSession().write(pb);
			plr.packetDispatcher().sendLogin();
			plr.getSession().write(plr.getPacket());
			plr.getPacket().reset();

			ChannelHandler ch = (ChannelHandler)plr.getSession().getPipeline().getLast();
			ch.setAttachment(plr);
			plr.details().STATUS |= 0x4; //Connection is established.
			plr.getSession().getPipeline().addLast("decoder", new RSDecoder(plr));
		}
	}

    /*
    * Process's All players Ready To Logout 1 Server Cycle After All Updates Are Recieved.
    * This Gives The 1 Server Cycle Delay Effect and prevents Any Null Issue's Or Problems
    * Where The Player Is Logged Out But Is Still Being Processed Results In Bad Information.
    */
	public static void queueLogouts()
	{
		Player p;
		while ((p = LOGOUTS.poll()) != null) 
		{
			System.out.println("Logging User Out Of Server: " + p.details().USERNAME_AS_STRING);
			p.packetDispatcher().logoutDisconnectChanges();
			p.getSession().write(p.getPacket().createPacket(166));
		}
	}

    /*
    * Process All players and Specific Tasks That All players Must See.
    * We start With logouts to remove all players who don't need to be executed.
    * We then send all Packets based in order of recieving them.
    * We then send all main player tasks which are executed only when needed.
    * We then send the basic stuff ( tick , updating , flagreset ).
    * We then save the next available player.
    * We then Login all new connections and then start all over. 
    */
	public static void tick() 
	{
		if (++gameTickIndex <= restartOnTick)
		{
			for (int i = 0; i < curIndex; i++)
				players[i].packetDispatcher().sendInterfaceString(GameUtility.formatTimeForDisplay(restartOnTick - gameTickIndex, "Rebooting Server In: "), 57, 0);
			if (gameTickIndex == restartOnTick)
				System.exit(0);
		}

	    	queueLogouts();

		TaskQueue.queue();

		for (int i = 0; i < curIndex; i++)
			players[i].tick();

		ShopManagement.updateContents();

		for (int i = 0; i < curIndex; i++)
			PlayerUpdating.update(players[i]);
		for (int i = 0; i < curIndex; ++i)
			NpcUpdating.update(players[i]);
		for (int i = 0; i < curIndex; i++)
			players[i].updateFlags().reset();

		for (int i = 0; i < curIndex; i++) 
		{
			players[i].getSession().write(players[i].getPacket());
			players[i].getPacket().reset();
		}

		saveNextPlayer();
		queueLogins();
	}

   /*
   * Adds A Player To The Logout Queue.
   */
        public static void addToLogoutQueue(Player player) 
	{
		LOGOUTS.add(player);	
	}

   /*
   * Adds A Player To The Login Queue.
   */
        public static void addToLoginQueue(PlayerLoginDetails details) 
	{
		LOGINS.add(details);
	}

   /*
   * Hopefully this method is called everytime the server is exited.
   */
	public static void exit() 
	{
		long t1 = System.nanoTime();
		for (int i = 0; i < curIndex; i++) {
			FileManagement.savePlayer(players[i]);
		}
		long t2 = System.nanoTime();
		System.out.println("Saved "+curIndex+" Players Accounts. -> " + (t2-t1));
	}

   /*
   * Best Performance for saving players. With servers that have 2000 players on they get saved once every 20 minutes.
   */
	public static void saveNextPlayer() 
	{
		if (curIndex == 0)
			return;
		if (saverIndex == curIndex || saverIndex < 0)
			saverIndex = 0;
		FileManagement.savePlayer(players[saverIndex++]);
	}

}
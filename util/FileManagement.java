/*
* @ Author - Digistr.
* @ Info - Handles All File Loaders. 
* @ Objectives - Finish This when i figure out the best way to load / write information.
*/

package com.util;

import java.io.File;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Arrays;

import com.model.PlayerLoginDetails;
import com.model.Location;
import com.model.Player;
import com.model.systems.Clan;
import com.model.systems.ClanManager;

public class FileManagement 
{
	public static Location[] MAP_REGIONS;
	public static int nextLocation = -1;

	public static Location getMap(int setIndex)
	{
		nextLocation = -1 + setIndex * 2;
		return getMap();
	}

	public static Location getMap() 
	{
		if (MAP_REGIONS == null)
			loadMaps();
		nextLocation += 2;
		if (nextLocation >= MAP_REGIONS.length)
			nextLocation = 1;
		return MAP_REGIONS[nextLocation];
	}
	
	public static int getMapIndex(Location me)
	{
		for (int i = 1; i < MAP_REGIONS.length; i += 2)
		{
			if (MAP_REGIONS[i].x <= me.x && (MAP_REGIONS[i].x + 64) > me.x)
			{
				if (MAP_REGIONS[i].y <= me.y && (MAP_REGIONS[i].y + 64) > me.y)
					return i;
				continue;
			}
		}
		return -1;
	}
	
	public static void loadMaps() {
		MAP_REGIONS = new Location[1884];
		for (int i = 1; i < MAP_REGIONS.length; i += 2)
			MAP_REGIONS[i] = new Location(3086, 3486, 0);
		int lowX = 32768;
		int lowY = 32768;
		int highX = 0;
		int highY = 0;
		try {
		BufferedReader loadFile = new BufferedReader(new FileReader("data/debug/FloorMaps.txt"));
		String data = null;
			while ((data = loadFile.readLine()) != null) 
			{
				String s[] = data.split(",");
				int id = Integer.valueOf(s[0]);
				MAP_REGIONS[id] = new Location(Short.valueOf(s[1]), Short.valueOf(s[2]), 0);
				if (MAP_REGIONS[id].x < lowX)
					lowX = MAP_REGIONS[id].x;
				if (MAP_REGIONS[id].x > highX)
					highX = MAP_REGIONS[id].x;
				if (MAP_REGIONS[id].y < lowY)
					lowY = MAP_REGIONS[id].y;
				if (MAP_REGIONS[id].y > highY)
					highY = MAP_REGIONS[id].y;
			}
		} catch (Exception e){ e.printStackTrace(); }		
		System.out.println("LowX: " + lowX + " HighX: " + highX + " LowY: " + lowY + " HighY: " + highY);
	}

	public static void registerUser(long username, String password, int rank)
	{
		File verify = new File("data/characters/" + username);
		boolean verified = verify.exists();
		if (verified && password != null)
		{
			System.out.println("Registered: " + username + " Password: " + password + " Rank: " + rank);
			Player player = new Player(new PlayerLoginDetails(username, password));
			player.RANK = (short)rank;
			savePlayer(player);
		}
	}

	public static void savePlayer(Player p) 
	{
	   try {
		FileBuilder file = new FileBuilder(10000);
		file.skipBytes(2);					//Begins at index 0, Read 2 Bytes Total.
		file.writeString(p.details().PASSWORD, 32);		//Begins at index 2, Read 32 Bytes Total.

		file.writeShort(p.RANK);				//Begins at index 38, Read 2 Bytes Total.

		for (int i = 0; i < 28; i++) {
			file.writeShort(p.inventory().items[i]);	//Begins at index 36, ID = 2, Amount = 4 : 6 Bytes Per Item.
			file.writeInt(p.inventory().amounts[i]);	//With 28 items thats 6 * 28 = Read 168 Bytes Total. 
		}

		for (int i = 0; i < 14; i++) {
			file.writeShort(p.equipment().items[i]);	//Begins at index 204, ID = 2, Amount = 4 : 6 Bytes Per Item.
			file.writeInt(p.equipment().amounts[i]);	//With 14 items thats 6 * 14 = Read 84 Bytes Total.
		}
		for (int i = 0; i < 23; i++) {
			file.writeByte(p.skills().levels[i]);		//Begins at index 288, LVL = 1, MLVL = 1, EXP = 4 : 6 Bytes Per Skill.
			file.writeByte(p.skills().mainLevels[i]);	//With 23 skills thats 6 * 23 = Read 138 Bytes Total.
			file.writeInt(p.skills().exp[i]);		
		}

		for (int i = 0; i < 400; i++) {
			file.writeShort(p.bank().items[i]);		//Begins at index 426, ID = 2, AMT = 4 : 6 Bytes Per Banked Item.
			file.writeInt(p.bank().amounts[i]);		//With 400 Items thats 6 * 400 = Read 2400 Bytes Total.
		}
		for (int i = 2; i < 15; i++) {
			file.writeByte((byte)p.updateFlags().getFeature(i)); //Begin at index 2826, Read 12 Bytes Total.
		}

		file.writeByte (p.chat().chatPublic);
		file.writeByte (p.chat().chatPrivate);
		file.writeByte (p.chat().chatTrade);
		file.writeByte (p.chat().chatClan);

		if ((p.chat().clanResponseCode & 0x10) == 0x10)
			file.writeLong (p.chat().attemptingToJoin);
		else
			file.writeLong (0); //Do NOT SAVE if we weren't in a clan chat.

		file.writeByte (p.fightType().box);

		file.writeByte ((byte)7);

		file.writeShort(p.location().x);
		file.writeShort(p.location().y);
		file.writeShort(p.location().z);

		file.writeByte(p.walkingQueue().running ? (byte)1 : 0);

		int length = file.writeLength();
		
		DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/backups/" + p.details().USERNAME_AS_LONG));
		dos.write(file.array(), 0, length);
		dos.close(); //Create a backup first in case save fails. Then we still have the integrity of our REAL file last save. 
			     //Or if this succeeds but our next safe fails then we can utilize the data of our BACKUP file.

		dos = new DataOutputStream(new FileOutputStream("data/characters/" + p.details().USERNAME_AS_LONG));
		dos.write(file.array(), 0, length);
		dos.close();
		
	    } catch (IOException io){ io.printStackTrace(); }
	}

	public static byte loadPlayer(Player p) 
	{
			DataInputStream dis = null;
		try {
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream("data/characters/" + p.details().USERNAME_AS_LONG)));

			if (dis == null || dis.available() < 2) 
			{
				dis = new DataInputStream(new BufferedInputStream(new FileInputStream("data/backups/" + p.details().USERNAME_AS_LONG)));
				System.out.println("Player File was corrupted or deleted or something just went wrong terribly wrong... [Read Backup]");
				if (dis == null || dis.available() < 2) 
				{
					p.updateFlags().forceAppearence();
					return 2;
				}
			}
			
			int length = dis.readShort();

			byte[] data = new byte[length - 2];
			dis.readFully(data, 0, data.length);
			FileBuilder file = new FileBuilder(data);

			char[] password = file.readString(32).toCharArray();
			p.details().STATUS |= 0x1; //NOT NEW PLAYER.
			p.RANK = (short)file.readShort();

			if (!Arrays.equals(password, p.details().PASSWORD)) 
			{
				dis.close();
				return 3;
			}

			for (int i = 0; i < 28; i++) 
			{
				p.inventory().items[i] = (short)file.readShort();
				p.inventory().amounts[i] = file.readInt();
	
				if (p.inventory().amounts[i] == 0)
					continue;

				--p.inventory().availableInventorySpace;
			}

			for (int i = 0; i < 14; i++) 
			{
				p.equipment().items[i] = (short)file.readShort();
				p.equipment().amounts[i] = file.readInt();
							
			}
			for (int i = 0; i < 23; i++) {
				p.skills().levels[i] = (byte)file.readByte();
				p.skills().mainLevels[i] = (byte)file.readByte();
				p.skills().exp[i] = file.readInt();
			}
			for (int i = 0; i < 400; i++) {
				p.bank().items[i] = (short)file.readShort();
				p.bank().amounts[i] = file.readInt();				
			}

			short[] looks = new short[13];
			for (int i = 0; i < 13; i++) {
				looks[i] = (short)file.readByte();
			}

			p.chat().chatPublic = (byte)file.readByte();
			p.chat().chatPrivate = (byte)file.readByte();
			p.chat().chatTrade = (byte)file.readByte();
			p.chat().chatClan = (byte)file.readByte();
			p.chat().attemptingToJoin = file.readLong();

			p.fightType().box = (byte)file.readByte();

			p.walkingQueue().faceDirection = (byte)file.readByte();
			p.location().setCoords((short)file.readShort(), (short)file.readShort(), (short)file.readShort());

		       /*Anything added beyond this point is considered unsafe reading. You must read using safeReads or
			*Risk attempting to read a player who may not be updated.
			*On occasion you can clear the safe parameter by running a Global Update On All Players.
		        */
			
			p.walkingQueue().running = file.safeReadByte(0) == 1;

		       /*End of safety reads.
			*/

			p.updateFlags().setLooks(looks);

			dis.close();
			return 2;
		} 
		catch (Exception ioe) 
		{
			ioe.printStackTrace();
			if (dis != null)
			{
				try {
					dis.close();
				} catch (IOException io) { }
			}	
		}

		System.out.println("Creating Fresh Player!");
		p.updateFlags().forceAppearence();
		return 2;
	}

	public static void loadFriendServer(Player p)
	{
		try 
		{
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream("data/friendserver/" + p.details().USERNAME_AS_LONG)));
		
			p.chat().myClanDisplayName = dis.readLong(); //If this is Zero then the Clan Chat isn't setup yet.

			p.chat().enterRank = (byte)dis.readByte();
			p.chat().speakerRank = (byte)dis.readByte();
			p.chat().kickRank = (byte)dis.readByte();
			p.chat().lootShareRank = (byte)dis.readByte();

			System.out.println("Enter Rank: " + p.chat().enterRank + " Speaker Rank: " + p.chat().speakerRank);
		
			int friend_size = dis.readByte();
			for (int i = 0; i < friend_size; i++)
				p.chat().FRIENDS.put(dis.readLong(), (byte)dis.readByte());
	
			int ignore_size = dis.readByte();
			for (int i = 0; i < ignore_size; i++)
				p.chat().IGNORES.add(dis.readLong());

			dis.close();

		} catch (IOException ioe) { }
	}

/*
 * clanResponseCode # LIST
 * 1 = This clan is currently too full. You can try again at any time.
 * 2 = No Connection or Clan doesn't exist. You can try again in 1 minute.
 * 3 = You aren't high enough rank to join this chat. You can try again in 1 minute.
 * 4 = You were recently kicked from this chat. You can try again in 5 minutes.
 * 5 = You are banned or blocked from accessing this channel. You can try again in 15 minutes.

 * 16 = You have successfully joined this clan chat and you are the only person to join.
 * 17 = You have successfully joined this clan chat and others have joined this chat already.
 */

	public static int readClanFileData(Player p, long username)
	{
		try 
		{
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream("data/friendserver/" + username)));
		
			long displayName = dis.readLong(); //If this is Zero then the Clan Chat isn't setup yet.

			if (displayName == 0)
			{
				dis.close();
				p.packetDispatcher().sendMessage("This user doesn't appear to have created a clan chat yet.");
				return 2;
			}

			int clanRanks = dis.readByte();
			dis.skipBytes(3);
			
			int myRank = -1;

			if (username == p.details().USERNAME_AS_LONG)
				myRank = 7;

			int friend_size = dis.readByte();

			if (myRank == -1)
			{
				for (int i = 0; i < friend_size; i++)
				{
					long user = dis.readLong();
					int rank = dis.readByte();
					if (user == p.details().USERNAME_AS_LONG)
					{
						myRank = rank;
						break;
					}
				}
			}

			if (myRank == -1)
			{
	
				int ignore_size = dis.readByte();
				for (int i = 0; i < ignore_size; i++)
				{
					long banned_user = dis.readLong();
					if ( banned_user == p.details().USERNAME_AS_LONG )
					{
						p.packetDispatcher().sendMessage("You are currently blocked from accessing this clan chat.");
						dis.close();
						return 5;
					}
				}
			}

			dis.close();

			if (myRank < ((byte)(clanRanks & 0xFF)))
			{
				p.packetDispatcher().sendMessage("You don't have a high enough rank to join this clan chat.");
				return 3;
			}

			Clan clan = ClanManager.mapped_clans.get(username);
			if (clan == null)
			{
				clan = new Clan(username, displayName, clanRanks);
				clan.addPlayer(p, myRank);
				ClanManager.mapped_clans.put(username, clan);
				p.packetDispatcher().sendMessage("You have joined into the clan chat: <" + GameUtility.longToString(displayName));
				return 16;
			} else {
				if (!clan.addPlayer(p, myRank))
				{
					p.packetDispatcher().sendMessage("This clan chat is too full.. Please try again later.");
					return 1;
				} else {
					p.packetDispatcher().sendMessage("You have joined into the clan chat: " + GameUtility.longToString(displayName));
					return 17;
				}
			}

		} catch (IOException ioe) { 
			p.packetDispatcher().sendMessage("This user doesn't appear to have created a clan chat yet.");
			return 2;
		}
	}

	public static void updateFriendServer(Player p)
	{
		try 
		{
			FileBuilder file = new FileBuilder((p.chat().FRIENDS.size() + p.chat().IGNORES.size()) * 10 + 32);

			file.writeLong (p.chat().myClanDisplayName);

			file.writeByte((byte) p.chat().enterRank);
			file.writeByte((byte) p.chat().speakerRank);
			file.writeByte((byte) p.chat().kickRank);
			file.writeByte((byte) p.chat().lootShareRank);

			file.writeByte((byte)p.chat().FRIENDS.size());

			Iterator iterator = p.chat().FRIENDS.keySet().iterator();

			while (iterator.hasNext()) 
			{
				long key = (Long)iterator.next();
				Byte rank = p.chat().FRIENDS.get(key);
				file.writeLong(key);
				file.writeByte(rank);
			}

			file.writeByte((byte)p.chat().IGNORES.size());	

			for (long name : p.chat().IGNORES)
				file.writeLong(name);

			DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/friendserver/" + p.details().USERNAME_AS_LONG));
			dos.write(file.array(), 0, file.writerIndex());
			dos.close();

		} catch (IOException ioe) { }
	}

	public static byte[] readFile(String s, int length) 
	{
		byte[] bytes = new byte[length];
		try {
			DataInputStream data = new DataInputStream(new BufferedInputStream(new FileInputStream("data/" + s)));
        		data.readFully(bytes, 0, length);
			data.close();
		} catch (IOException io) {

		}
		return bytes;
	}

	public static void writeFile(String s, byte[] data) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/" + s));
        		dos.write(data);
			dos.close();
		} catch (IOException io) {
		
		}
	}

	public static void writeFile(String s, byte[] data, int length) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/" + s));
        		dos.write(data,0,length);
			dos.close();
		} catch (IOException io) {
		
		}
	}

}
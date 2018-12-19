/*
* @ author - Digistr
* @ info - contains most packets that the client recieves.
* @ moreinfo - this could be made a bit better not sure how i want it too exactly be / work.
*/

package com.packet;

import com.model.World;
import com.model.Player;
import com.model.Location;
import com.util.FileManagement;
import com.util.ItemManagement;
import com.model.systems.Clan;
import com.model.systems.ClanManager;

import java.util.Iterator;
import java.util.HashMap;

public class PacketDispatcher 
{

	private int pingNumber = 0;
	private static int globalMessageCounter = (int)(System.currentTimeMillis() & 0xFFFFFFFF);
	private Player me;

	public PacketDispatcher(Player player) 
	{
		me = player;		
	}

	private PacketBuilder getPacket() 
	{
		return me.getPacket();
	}

	private void sendWelcomeScreen() 
	{
		sendMessage("Welcome To Valcan 474");
   		sendWindowPane(549);
		sendInterface(1, 549, 378, 2);
		sendInterface(1, 549, 17, 3);

		sendInterfaceString("Friend List - World 1", 550, 2);
		sendInterfaceString("Welcome to Valcan 474 Server.", 378, 12);
		sendInterfaceString("You last logged in <col=FF0000>earlier today <col=000000>from: " + (new String(me.details().IP)), 378, 13);
		sendInterfaceString("N/A", 378, 14);
		sendInterfaceString("You have <col=00FF00>0 <col=FFFF00> messages in your message centre!", 378, 15);
		sendInterfaceString("N/A", 378, 17);
		sendInterfaceString("You have <col=00FF00>Unlimited <col=FFFF00>days of member credit remaining.", 378, 19);
		sendInterfaceString("Message Of The Day", 17, 2);	
		sendInterfaceString("Updates are in the works!", 17, 1);
	}

	public void sendLogin() 
	{
		sendMapRegion();
		sendPingPacket();
		sendWelcomeScreen();
		for (int i = 0; i < 23; i++)
			sendSkill(i);
		sendMultiItems(149,  0, 93, me.inventory().items, me.inventory().amounts);
		sendMultiItems(387, 28, 93, me.equipment().items, me.equipment().amounts);
		FileManagement.loadFriendServer(me);
		sendContactsStatus((byte)2);
		me.chat().sendList();
		if (me.chat().attemptingToJoin != 0)
			ClanManager.joinClan(me, me.chat().attemptingToJoin);
		sendChatTypes(me.chat().chatPublic, me.chat().chatPrivate, me.chat().chatTrade);
		sendTab(137, 90);
		int sidebar = ItemManagement.getSidebarInterface(me.equipment().items[3]);
		me.skills().setCombatLevel();
		me.fightType().wield(sidebar);
		me.packetDispatcher().sendConfig(173, me.walkingQueue().running ? 1 : 0);
		//me.packetDispatcher().sendConfig(43, me.fightType().box);
		//me.packetDispatcher().sendInterfaceString(sidebar == 92 ? ("Unarmed") : ("Wield ID: " + me.equipment().items[3]), sidebar, 0);
		//me.packetDispatcher().sendInterfaceString("Combat Level: " + me.skills().combatLevel, sidebar, 23);
		me.fightType().display(me);
		me.special().sendSpecialBar(sidebar);
		sendTab(sidebar, 99);
		sendTab(320, 100);
		sendTab(274, 101);
		sendTab(149, 102);
		sendTab(387, 103);
		sendTab(271, 104);
		sendTab(192, 105);
		sendTab(589, 106);
		sendTab(550, 107);
		sendTab(551, 108);
		sendTab(182, 109);
		sendTab(261, 110);
		sendTab(464, 111);
		sendTab(239, 112);
		sendConfig(304,0); 
		sendConfig(115,0);
		sendRightClickOption("Attack", 1, true);
		sendRightClickOption("Follow", 3, false);
		sendRightClickOption("Trade", 4, false);
		sendMultiItems( -1,  0, 93, me.inventory().items, me.inventory().amounts);
	}

    /*
     * THIS IS ONLY CALLED WHEN A SUCCESSFUL normal logout has occured!
     * DO NOT PUT forcible closures in here as this is NOT guarenteed to always be called!
     */
	public void logoutDisconnectChanges() 
	{		

	}

    /*
     * This is GUARENTEED TO ALWAYS BE CALLED, but there will NOT be a guareentee on STABLE CONNECTION to user client!
     * When a players connection is closed and after logging out has occured they go through the below!
     * The player will remain in the World System until the below has completed.
     * This allows you to decline trades, duel offers, etc.. and anything else that must occur on logout!
     * DO NOT PUT connection specific changes in here as these are situations that occur when no connection is available!
     */
	public void crashDisconnectSecurity() 
	{
		me.trade().declineTrade(true);
		ClanManager.removePlayer(me);
		me.chat().updateFriendServer();
	}

	public void sendUnknownPacket4(byte slot,  byte id) 
	{
		getPacket().createPacket(4).addByte(slot).addByte(id);
	}

	public void sendMinimapType(int type) 
	{
		getPacket().createPacket(5).addByte((byte) type);
	}

	public void sendConfig2(int id, int value) 
	{
		getPacket().createPacket(10).addLEInt(value).addShort(id);
	}

	public void sendChatTypes(byte chatPublic, byte chatPrivate, byte chatTrade)
	{
		getPacket().createPacket(15).addByte(chatPublic).
		addByte(chatPrivate).addByte(chatTrade);
	}

	public void sendTab(int interfaceId, int childId) 
	{
		sendInterface(1, 548, interfaceId, childId);
	}

	public void sendTab(int interfaceId, int childId, int location) 
	{
		me.interfaceContainer().sidebar[location] = (short)interfaceId;
		sendInterface(1, 548, interfaceId, childId);
	}

	public void sendHideTabs(int id) 
	{	
		sendInterface(0, 548, id, 97);
	}


	public void sendWalkableInterface(int id) 
	{
		me.interfaceContainer().subInterface = (short)id;
		sendInterface(1, 548, id, 75);
	}

	public void sendInterface(int id) 
	{
		me.interfaceContainer().mainInterface = (short)id;
		sendInterface(0, 548, id, 77);
	}

	public void sendInterface(int showId, int windowId, int interfaceId, int childId) 
	{
		getPacket().createPacket(17).addByteS((byte)showId).
		addLEShort(interfaceId).addShort(childId).addShort(windowId);
	}	

	public void sendPlayerFaceOnInterface(int interfaceId, int child) 
	{
		getPacket().createPacket(22).addInt(interfaceId << 16 | child);
	}

	public void sendContactsStatus(byte status) 
	{
		getPacket().createPacket(30).addByte(status);
	}

	public void sendModelsOnInterface(int model, int interfaceId, int child) 
	{
		getPacket().createPacket(33).addShortA(model).addInt1(interfaceId << 16 | child);
	}


	public PacketBuilder sendClanMessage(Player speaker, long displayName, int length, byte[] encryptedText) 
	{
		PacketBuilder builder = new PacketBuilder(256);
		
		builder.createPacketTypeByte(35).addLong(speaker.details().USERNAME_AS_LONG).addByte((byte)0)
		.addLong(displayName).addShort(++globalMessageCounter >> 32).addTriByte(globalMessageCounter).addByte((byte)(speaker.RANK & 0x3))
		.addBytes(encryptedText, 0, length).endPacketTypeByte();
		return builder;
	}

	public void buildClanListMembers(Clan clan)
	{
		PacketBuilder builder = getPacket();

		builder.createPacketTypeShort(56).addLong(clan.owner).addLong(clan.displayName);
		builder.addByte((byte)0).addByte((byte)clan.members.size());
		Iterator iterator = clan.members.keySet().iterator();
		while (iterator.hasNext())
		{
			long key = (long)iterator.next();
			builder.addLong(key).addShort((short)1).addByte((byte)clan.members.get(key));
		}
		builder.endPacketTypeShort();
	}

	public void updateClanRank(long username, int rank)
	{
		getPacket().createPacket(32).addLong(username).addShort((short)1).addByte((byte)rank);
	}

	public void clearClanListMembers()
	{
		me.chat().clanResponseCode = 0x0;
		getPacket().createPacketTypeShort(56).addLong(0).endPacketTypeShort();
	}


	public void sendInterfaceImageDetails(int rotation, int size, int interfaceId, int child) 
	{
		getPacket().createPacket(36).addShort(0).addLEShort(size).
		addInt(interfaceId << 16 | child).addLEShortA(rotation);
	}

	public void sendEnergy(byte energy) 
	{
		getPacket().createPacket(42).addByte(energy);
	}

	public void sendMapRegion() 
	{
		boolean forceSend = true;
		if(((me.location().regionX() / 8) == 48 || (me.location().regionX() / 8) == 49) && 
			me.location().regionY() / 8 == 48)
				forceSend = false;
		if((me.location().regionX() / 8) == 48 && (me.location().regionY() / 8) == 148)
				forceSend = false;
		getPacket().createPacketTypeShort(61).addLEShortA(me.location().localY()).
			addByte((byte)me.location().z).addShort(me.location().regionX());
		for (int x = (me.location().regionX() - 6) / 8; x <= (me.location().regionX() + 6) / 8; x++) {
			for (int y = (me.location().regionY() - 6) / 8; y <= (me.location().regionY() + 6) / 8; y++) {
				if (forceSend || ((y != 49) && (y != 149) && (y != 147) && 	
				   (x != 50) && ((x != 49) || (y != 47)))) {
					getPacket().
					addInt(0). //WE
					addInt(0). //DONT
					addInt(0). //HAVE
					addInt(0); //474 XTEA.
				}
			}
		}
		getPacket().addShortA(me.location().localX()).addShort(me.location().regionY()).endPacketTypeShort();
		me.setLastLocation();
	}

	public void moveChildInterfaces(int interfaceId, int childId, int moveX, int moveY) 
	{
		getPacket().createPacket(76).addLEShort(moveX).
		addLEInt(interfaceId << 16 | childId).addShortA(moveY);
	}

	public void sendRemoveMapFlag() 
	{
		getPacket().createPacket(84);
	}

	public void sendInterfaceAnimation(int emote, int interfaceId, int child) 
	{
		getPacket().createPacket(107).addInt2(interfaceId << 16 | child).addLEShort(emote);
	}

	public void sendMultiItems(int parentId, int childId, int type, short[] items, int[] itemsN) 
	{
		PacketBuilder multiItemPacket = getPacket();
		multiItemPacket.createPacketTypeShort(119).addInt(parentId << 16 | childId).
			addShort(type).addShort(items.length);
		for (int i = 0; i < items.length; i++) {
			if (itemsN[i] > 254) {
				multiItemPacket.addByteC(255).addLEInt(itemsN[i]);
			} else {
				multiItemPacket.addByteC(itemsN[i]);
			}
			multiItemPacket.addLEShortA(items[i] + 1);
		}
		multiItemPacket.endPacketTypeShort();
	}
	
	public void sendPlayMusic(int musicIdx) 
	{
		getPacket().createPacket(121).addLEShortA(musicIdx);
	}

	public void sendNpcFaceOnInterface(int npc, int interfaceId, int child) 
	{
		getPacket().createPacket(127).addShortA(npc).addInt(interfaceId << 16 | child);
	}

	public void setInterfaceOptions(int bits, int parent, int child, int offset, int length) 
	{
		getPacket().createPacket(133).addLEInt(bits).addLEShortA(length)
		.addInt(parent << 16 | child).addShortA(offset);
	}

	public void sendAnimationReset() {
		getPacket().createPacket(138);		
	}

	public void resetInterfaceStrings(int interfaceId) 
	{
		getPacket().createPacket(153).addInt1(interfaceId);
	}

	public void sendCameraReset() 
	{
		getPacket().createPacket(156);
	}

	public void sendGraphic(Location loc, int id)
	{
		sendCoords2(loc.x, loc.y);
		getPacket().createPacket(49).addByte((byte) 0).
		addShort(id).addByte((byte) 60).addShort(0);
	}

	public void sendProjectile(Player entity, int graphicId, int sHeight, int eHeight, int speed, int beginDelay, int slope, int eyeOffset) 
	{
		int difX = me.location().x - entity.location().x;
		int difY = me.location().y - entity.location().y;

		sendCoords(me.location(), me.lastLocation());

		getPacket().createPacket(38).
		addByte((byte)0).	  //Offset of start position.
		addByte((byte)difX).	  //Distance to Travel X.
		addByte((byte)difY).	  //Distance to Travel Y.
		addShort(~entity.INDEX).  //Entity Index.
		addShort(graphicId).	  //Graphic ID.
		addByte((byte)sHeight).	  //Begin Height. 64 = Player Top
		addByte((byte)eHeight).	  //End Height.   32 = Player Mid
		addShort(beginDelay).	  //Start Delay
		addShort(speed).	  //18.75ms per value. 32 = 1 Server Cycle.
		addByte((byte)slope).	  //Slope Angle , 32 = 45 Degree Slope.
		addByte((byte)eyeOffset); //128 = 1 Tile Distance.
	}

	public void sendPositionChunkIndex(int x, int y)
	{
		int xValue = x & 63;
		int yValue = y & 63;
		getPacket().createPacket(168)
			.addByte((byte)xValue).addByteA((byte)yValue);
	}

	public void sendCoords(Location loc, Location last) 
	{
		System.out.println("X: " + loc.localX(last) + ", Y: " + loc.localY(last) + " toString(): " + last.toString());
		getPacket().createPacket(168).addByte((byte)loc.localX(last)).addByteA(loc.localY(last));
	}

	public void sendCoords(int x, int y) 
	{
		getPacket().createPacket(168).addByte((byte)(x - (me.location().regionX() * 8))).
		addByteA((byte)(y - (me.location().regionY() * 8)));
	}

	protected void sendCoords2(int x, int y) 
	{
		int regionX = me.lastLocation().regionX(),
	    	regionY = me.lastLocation().regionY();
		getPacket().createPacket(168).addByte((byte) (x - ((regionX - 6) * 8))).
		addByteA((byte) (y - ((regionY - 6) * 8)));
	}

	public void sendCloseInterface(int parentLocation) 
	{
		getPacket().createPacket(174).addInt(35913728 | parentLocation);
	}

	public void sendInterfaceConfig(int parentId, int childId, boolean show) 
	{
		getPacket().createPacket(184).addByteA((byte)(show ? 0 : 1)).addInt(parentId << 16 | childId);
	}

	public void sendSingleItem(int interfaceId, int index, int type, int slot, int item, int itemN) 
	{
		PacketBuilder singleItemPacket = getPacket().createPacketTypeShort(187);
		singleItemPacket.addInt(interfaceId << 16 | index);
		singleItemPacket.addShort(type).addSmart(slot);
		singleItemPacket.addShort(item + 1);
		if (itemN > 254) {
			singleItemPacket.addByte((byte)255).addInt(itemN);
		} else {
			singleItemPacket.addByte((byte)(itemN > 0 ? itemN : -1));
		}
		singleItemPacket.endPacketTypeShort();
	}

	public void sendCreateObject(Location loc, int id, int x, int y, int z, int dir, int type) 
	{
		if (loc.z != z) 
			return;

		sendCoords2(x, y);
		getPacket().createPacket(188).
		addByte((byte)((type << 2) + (dir & 3))).
		addShortA(id).addByteS(0);		
	}

	public void sendSkill(int slot) 
	{
		getPacket().createPacket(196).addByte((byte)slot).
		addInt2(me.skills().exp[slot]).addByte(me.skills().levels[slot]);	
	}

	public void sendSystemUpdate(int timeLeft) 
	{
		getPacket().createPacket(207).addLEShort(timeLeft * 5 / 3);
	}

	public void sendMessage(String msg) 
	{
		getPacket().createPacketTypeByte(209).addString(msg).endPacketTypeByte();
	}

	public void sendRightClickOption(String option, int slot, boolean top) 
	{
		getPacket().createPacketTypeByte(225).addByteA(top ? 1 : 0).
		addString(option).addByteC(slot).endPacketTypeByte();
	}

	public void sendInterfaceString(String msg, int parentId, int childId) 
	{
		getPacket().createPacketTypeShort(231).addInt2(parentId << 16 | childId).addString(msg).endPacketTypeShort();
	}

	public void sendInterfaceScript(int type, Object[] script, String order) 
	{
		PacketBuilder interfaceScript = getPacket().createPacketTypeShort(237);	
		interfaceScript.addString(order);
		char[] orders = order.toCharArray();
		int spotIndex = -1;
		for (int i = order.length() - 1; i > -1; --i) {
			if (orders[i] == 115)
				interfaceScript.addString((String) script[++spotIndex]);
			else
				interfaceScript.addInt((Integer) script[++spotIndex]);
		}
		interfaceScript.addInt(type).endPacketTypeShort();
	}

	public void sendEnterInterface(int type, String msg) 
	{
		getPacket().createPacketTypeShort(237).addString("s").addString(msg).addInt(type).endPacketTypeShort();
	}

	public void sendForceSelectTab(int tab) 
	{
		getPacket().createPacketTypeShort(237).addString("i").addInt(tab).addInt(71).endPacketTypeShort();
	}

	public void runScriptInt(int idx, int type) 
	{
		getPacket().createPacketTypeShort(237).addString("i").addInt(idx).addInt(type).endPacketTypeShort();
	}

	public void sendPingPacket() 
	{
		getPacket().createPacketTypeShort(238).addInt(pingNumber++).endPacketTypeShort();
	}

	public void sendItemsOnInterface(int interfaceId, int child, int item, int size) 
	{
		getPacket().createPacket(249).addInt1(interfaceId << 16 | child).addInt(size).addShort(item);
	}

	public void sendWindowPane(int pane) 
	{
		getPacket().createPacket(251).addLEShort(pane);
	}

	public void sendTest(int interfaceID, int sidebar) 
	{
		getPacket().createPacket(102).addLEShort(interfaceID).addByteC(sidebar);
	}
	public void sendConfig(int id, int value) 
	{
		if (value < 128)
			sendConfig1(id, value);
		else
			sendConfig2(id, value);
	}

	public void sendConfig1(int id, int value) 
	{
		getPacket().createPacket(253).addShort(id).addByteA(value);
	}

}
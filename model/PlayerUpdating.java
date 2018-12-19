/*
* @ Author - Digistr
* @ Objective - Updates Players In The Game.
*/
package com.model;

import com.packet.PacketBuilder;
import com.packet.Packet;
import com.util.ItemManagement;
import com.util.GameUtility;

public class PlayerUpdating {

	private static final byte[] FACE_DIRECTIONS = {0, 1, 2, 3, 6, 4, 5, 6, 7};

   /*
   * This method is called every server cycle and performs in this order.
   * StartBitWriter.
   * UpdateMyPlayersPosition
   * UpdateOtherPlayersPosition + RemovePlayers.
   * SendNewPlayers.
   * writeNewPlayerSize.
   * EndBitWriter.
   * SendMyPlayersUpdatePackets + SendOtherPlayersUpdatePackets.
   * Taken right out of the 474 client for how it goes about reading the data.
   */
	public static void update(Player p) 
	{
		if (p.updateFlags().flagsAreUpdated[0])
			p.packetDispatcher().sendMapRegion();
		PacketBuilder updatePacket = p.getPacket().createPacketTypeShort(150);
		updatePacket.startBitWriter();
		sendMyPlayersPosition(p,updatePacket);
		int size = p.playerList().getMainListSize();
		updatePacket.addBits(8, size);
		p.playerList().resetMainListSize();
		short[] list = p.playerList().getMainList();
		for (int i = 0; i < size; i++) 
		{
			Player p2 = World.getPlayerByCheckingIndex(list[i]);
			if (p2 == null || ((p2.details().STATUS & 0xE) != 0x6) || p2.updateFlags().flagsAreUpdated[1] || !p.location().withinDistance(p2.location())) {
				updatePacket.addBits(1, 1);
				updatePacket.addBits(2, 3);
				p.playerList().addSubList(list[i], false);
			} else {
				p.playerList().addSubList(p2.INDEX, true);
				sendOtherPlayersPosition(p2, updatePacket);
				p.playerList().insertMainList(p2.INDEX);
			}
		}
		for (int i = 0; i < World.curIndex; i++) {
			if (p.playerList().getMainListSize() >= 255)
				break;
			Player p2 = World.players[i];
			if (p2.INDEX == p.INDEX)
				continue;
			if (p.location().withinDistance(p2.location())) 
			{
				if (p.playerList().addMainList(p2.INDEX)) {
					sendCreatePlayer(p, p2, updatePacket);
					p.playerList().addSubList(p2.INDEX, false);
				} else {
					p.playerList().addSubList(p2.INDEX, true);
				}
			}
		}
		updatePacket.addBits(11, 2047);
		updatePacket.endBitWriter();
		if (p.updateFlags().flagsAreUpdated[3])
			sendPlayerUpdatePackets(p, updatePacket, false);
		list = p.playerList().getMainList();
		for (int i = 0; i < p.playerList().getMainListSize(); i++) 
		{
			Player p2 = World.getPlayerByCheckingIndex(list[i]);
			if (p2 == null)
				continue;
			if (!p.playerList().getSubList(list[i]))
				sendPlayerUpdatePackets(p2, updatePacket, true);
			else if (p2.updateFlags().flagsAreUpdated[3])
				sendPlayerUpdatePackets(p2, updatePacket, false);
		}
		updatePacket.endPacketTypeShort();
	}

  /*
  * Each section is taken from client and signifys what it does.
  * The first bit i think is update.
  * Next is walk type like stand , walk , run , teleport.
  * Next is bit i think is update again.
  * Next is some divider i think.
  * Next is self explanitory.
  */
	private static void sendMyPlayersPosition(Player p, PacketBuilder updatePacket) {
		byte update = (byte)(p.updateFlags().flagsAreUpdated[3] ? 1 : 0);
		if (p.updateFlags().flagsAreUpdated[1]) {
	    		updatePacket.addBits(1, 1);
	    		updatePacket.addBits(2, 3);
	    		updatePacket.addBits(1, update);
	    		updatePacket.addBits(1, 1);
	   		updatePacket.addBits(7, p.location().localX(p.lastLocation()));
	    		updatePacket.addBits(2, p.location().z);
	    		updatePacket.addBits(7, p.location().localY(p.lastLocation()));
		} else {
			if (p.walkingQueue().walkDir < 0) {
				updatePacket.addBits(1, update);
				if (update == 1)
					updatePacket.addBits(2, 0);
			} else {
				updatePacket.addBits(1, 1);
				if (p.walkingQueue().runDir > -1) {
		   			updatePacket.addBits(2, 2);
					updatePacket.addBits(3, p.walkingQueue().walkDir);
					updatePacket.addBits(3, p.walkingQueue().runDir);
				} else {
		   			updatePacket.addBits(2, 1);
					updatePacket.addBits(3, p.walkingQueue().walkDir);
				}
				updatePacket.addBits(1, update);
			}
		}
	}

  /*
  * Each section is taken from client and signifys what it does.
  * Similar to SendPlayerPosition Method Only Theirs no support for Teleporting.
  */
	private static void sendOtherPlayersPosition(Player p, PacketBuilder updatePacket) {
		if (p.walkingQueue().walkDir < 0) {
			if (p.updateFlags().flagsAreUpdated[3]) {
				updatePacket.addBits(1,1);
				updatePacket.addBits(2,0);
			} else {
				updatePacket.addBits(1,0);
			}
		} else if (p.walkingQueue().runDir < 0) {
			updatePacket.addBits(1,1);
			updatePacket.addBits(2,1);
			updatePacket.addBits(3,p.walkingQueue().walkDir);
			updatePacket.addBits(1,p.updateFlags().flagsAreUpdated[3] ? 1 : 0);
		} else {
			updatePacket.addBits(1,1);
			updatePacket.addBits(2,2);
			updatePacket.addBits(3,p.walkingQueue().walkDir);
			updatePacket.addBits(3,p.walkingQueue().runDir);
			updatePacket.addBits(1,p.updateFlags().flagsAreUpdated[3] ? 1 : 0);				
		}
	}

  /*
  * Each section is taken from client and signifys what it does.
  * We first send the players index.
  * I think this is a seperator or considered a type.
  * We then send the direction of this player, i use a seperate system for following so
  * We need to put it back to the way the client likes it.
  */
	private static void sendCreatePlayer(Player p, Player p2, PacketBuilder updatePacket) {
		updatePacket.addBits(11, p2.INDEX);
		updatePacket.addBits(1, 1);
		updatePacket.addBits(3, FACE_DIRECTIONS[p2.walkingQueue().faceDirection]);
		updatePacket.addBits(5, p2.location().x - p.location().x);
		updatePacket.addBits(1, 1);
		updatePacket.addBits(5, p2.location().y - p.location().y);
	}

  /*
  * Sends player update packets.
  */ 
	private static void sendPlayerUpdatePackets(Player p, PacketBuilder updatePacket, boolean forceAppearence) {
		int size = 0x0;
		boolean flags[] = p.updateFlags().flagsAreUpdated;
		if (flags[4])
			size |= 0x1;
		if (flags[5])
			size |= 0x2;
		if (flags[6])
			size |= 0x4;
		if (flags[7])
			size |= 0x8;
		if (flags[8] || forceAppearence)
			size |= 0x10;
		if (flags[9])
			size |= 0x40;
		if (flags[10])
			size |= 0x80;
		if (flags[11])
			size |= 0x100;
		if (flags[12])
			size |= 0x200;
		if (flags[13])
			size |= 0x400;
		if (size > 255) {
			size |= 0x20;
			updatePacket.addByte((byte)size);
			updatePacket.addByte((byte)(size >> 8));
		} else {
			updatePacket.addByte((byte)size);
		}
		if (flags[8] || forceAppearence)
			sendAppearenceUpdatePacket(p,updatePacket);
		if (flags[11])
			sendGraphicsUpdatePacket(p, updatePacket);
		if (flags[4])
			sendTurnToPosition(p,updatePacket);
		if (flags[9])
			sendHeadTextUpdatePacket(p,updatePacket);
		if (flags[7])
			sendAnimationUpdatePacket(p,updatePacket);
		if (flags[5])
			sendFaceUpdatePacket(p,updatePacket);
		if (flags[6])
			sendHitDamage1UpdatePacket(p,updatePacket);
		if (flags[10])
			sendChatTextUpdatePacket(p,updatePacket);
		if (flags[13])
			sendForceMovementUpdatePacket(p, updatePacket);
		if (flags[12])
			sendHitDamage2UpdatePacket(p, updatePacket);
	}

	private static void sendAppearenceUpdatePacket(Player p, PacketBuilder updatePacket) {
		PacketBuilder playerProps = new PacketBuilder();
		playerProps.addByte((byte)(p.updateFlags().getFeature(2) & 0xff)); //Gender
		playerProps.addByte((byte)p.updateFlags().getFeature(0)); //Prayer Icon 
		playerProps.addByte((byte)p.updateFlags().getFeature(1)); //Skull Icon.
		for (int i = -1; ++i < 4;) {
			if (p.equipment().items[i] > 0)
				playerProps.addShort(p.equipment().items[i] + 512);
			else
				playerProps.addByte((byte)0);
		}
		if (p.equipment().items[4] < 1)
			playerProps.addShort(p.updateFlags().getFeature(5));
		else
			playerProps.addShort(p.equipment().items[4] + 512);
		if (p.equipment().items[5] < 1)
			playerProps.addByte((byte)0);
		else
			playerProps.addShort(p.equipment().items[5] + 512);
		if (ItemManagement.showArms(p.equipment().items[4]))
			playerProps.addShort(p.updateFlags().getFeature(6));
		else
			playerProps.addShort(p.equipment().items[4] + 512);
		if (p.equipment().items[7] < 1)
			playerProps.addShort(p.updateFlags().getFeature(8));
		else
			playerProps.addShort(p.equipment().items[7] + 512);
		if (p.equipment().items[0] < 1 || ItemManagement.showHead(p.equipment().items[0]))
			playerProps.addShort(p.updateFlags().getFeature(3));
		else
			playerProps.addShort(p.equipment().items[0] + 512);
		if (p.equipment().items[9] < 1)
			playerProps.addShort(p.updateFlags().getFeature(7));
		else
			playerProps.addShort(p.equipment().items[9] + 512);
		if (p.equipment().items[10] < 1)
			playerProps.addShort(p.updateFlags().getFeature(9));
		else
			playerProps.addShort(p.equipment().items[10] + 512);
		if (ItemManagement.removeBeard(p.equipment().items[0]) || p.updateFlags().getFeature(2) != 256)
			playerProps.addShort(p.equipment().items[0] + 512);
		else
			playerProps.addShort(p.updateFlags().getFeature(4));
		for (int i = 10; i < 15; i++)
			playerProps.addByte((byte)p.updateFlags().getFeature(i));
		playerProps.addShort(ItemManagement.getAppearenceAnimation(p.equipment().items[3], 0)); //Stand
		playerProps.addShort(ItemManagement.getAppearenceAnimation(p.equipment().items[3], 1)); //Slow Walk backwards
		playerProps.addShort(ItemManagement.getAppearenceAnimation(p.equipment().items[3], 2)); //Walk
		playerProps.addShort(ItemManagement.getAppearenceAnimation(p.equipment().items[3], 3)); //Walk backwards
		playerProps.addShort(ItemManagement.getAppearenceAnimation(p.equipment().items[3], 4)); //Walk left
		playerProps.addShort(ItemManagement.getAppearenceAnimation(p.equipment().items[3], 5)); //Walk right
		playerProps.addShort(ItemManagement.getAppearenceAnimation(p.equipment().items[3], 6)); //run
		playerProps.addLong(p.details().CUSTOM_USERNAME);
		playerProps.addByte(p.skills().combatLevel);
		playerProps.addShort(0);
		updatePacket.addByteA((byte)(playerProps.writerIndex() & 0xFF));
		updatePacket.addBytesReversed(playerProps);
	}

	private static void sendHitDamage1UpdatePacket(Player p, PacketBuilder updatePacket) {
		updatePacket.addByte(p.updateFlags().getHitDamage1());
		updatePacket.addByteA(p.updateFlags().getHitColor1());
		updatePacket.addByteS(p.skills().levels[3]); //CurrentHP
		updatePacket.addByteC(p.skills().mainLevels[3]); //TotalHP		 
	}

	private static void sendHitDamage2UpdatePacket(Player p, PacketBuilder updatePacket) {
		updatePacket.addByteS(p.updateFlags().getHitDamage2());
		updatePacket.addByteS(p.updateFlags().getHitColor2());
		updatePacket.addByte(p.skills().levels[3]); //CurrentHP
		updatePacket.addByte(p.skills().mainLevels[3]); //TotalHP
    	}

	private static void sendAnimationUpdatePacket(Player p, PacketBuilder updatePacket) {
		updatePacket.addShortA(p.updateFlags().getAnimId());
		updatePacket.addByte(p.updateFlags().getAnimDelay());	
	}

	private static void sendGraphicsUpdatePacket(Player p, PacketBuilder updatePacket) {
		updatePacket.addShortA(p.updateFlags().getGfxId());
		updatePacket.addInt2(p.updateFlags().getGfxData());
    	}

	private static void sendHeadTextUpdatePacket(Player p, PacketBuilder updatePacket) {
		updatePacket.addString(p.updateFlags().getHeadText());
	}

	private static void sendChatTextUpdatePacket(Player p, PacketBuilder updatePacket) {
		byte[] chat = new byte[256];
		chat[0] = (byte) p.updateFlags().getChatText().length;
		int offset = GameUtility.encryptText(chat, p.updateFlags().getChatText(), 0);
		updatePacket.addLEShortA(p.updateFlags().getChatEffects());
		updatePacket.addByteA(p.RANK & 0x3);
		updatePacket.addByteS(offset);
		updatePacket.addBytes(chat, 0, offset);
	}

	private static void sendTurnToPosition(Player p, PacketBuilder updatePacket) {
		updatePacket.addLEShort(p.updateFlags().getDirectionX() * 2 + 1);
		updatePacket.addLEShortA(p.updateFlags().getDirectionY() * 2 + 1);
	}

	private static void sendFaceUpdatePacket(Player p, PacketBuilder updatePacket) {
		updatePacket.addShort(p.updateFlags().getFaceTo());
	}

	private static void sendForceMovementUpdatePacket(Player p, PacketBuilder updatePacket) {
		updatePacket.addByteC((byte) 0);
		updatePacket.addByteS((byte) 0);
		updatePacket.addByteS((byte) 0);
		updatePacket.addByteC((byte) 0);
		updatePacket.addShort((byte) 0);
		updatePacket.addShortA((byte) 0);
    		updatePacket.addByte((byte) 0);
	}

}
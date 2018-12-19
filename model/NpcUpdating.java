package com.model;

import com.packet.Packet;
import com.packet.PacketBuilder;

import com.packet.incomingPackets.Commands;

public class NpcUpdating
{
	//Class37_Sub9_Sub7.method788(13896);
	//Class56.method1143((byte) 91);
	//

	static int test = 0;

	public static void update(Player p)
	{
		PacketBuilder updatePacket = p.getPacket().createPacketTypeShort(146);
		updatePacket.startBitWriter();

		int size = p.npcList().getMainListSize();
		p.npcList().resetMainListSize();

		updatePacket.addBits(8, size);

		p.npcList().resetMainListSize();
		short[] list = p.npcList().getMainList();

		for (int i = 0; i < size; i++) 
		{
			Npc npc = World.getNpcByCheckingIndex(list[i]);
			//0x100 = UpdateRequired.
			//0x200 = Teleporting.
			//0x400 = HiddenNPC (Aka Unused).
			if (npc == null || (npc.updateFlags & 0x600) != 0x0 || !p.location().withinDistance(npc.location())) {
				updatePacket.addBits(1, 1);
				updatePacket.addBits(2, 3);
				p.npcList().addSubList(list[i], false);
			} else {
				p.npcList().addSubList(npc.index, true);
				handleNPCMovement(npc, updatePacket);
				p.npcList().insertMainList(npc.index);
			}
		}

		for (int i = 0; i < World.totalNpcs; i++) 
		{
			if (p.npcList().getMainListSize() >= 255)
				break;
			Npc npc = World.npcs[i];
			if (p.location().withinDistance(npc.location())) {
				if (p.npcList().addMainList(npc.index)) {
					sendCreateNPC(npc, p.location(), updatePacket);
					p.npcList().addSubList(npc.index, false);
				} else {
					p.npcList().addSubList(npc.index, true);
				}
			}
		}

		boolean endBits = true;

		list = p.npcList().getMainList();

		for (int i = 0; i < p.npcList().getMainListSize(); ++i) 
		{
			Npc npc = World.getNpcByCheckingIndex(list[i]);
			if ((npc.updateFlags & 0x600) != 0x0)
				continue;
			if (npc.updateFlags > 0xFF)
			{
				if (endBits)
				{
					updatePacket.addBits(15, 32767);
					updatePacket.endBitWriter();
					endBits = false;
				}
				sendNpcUpdatePackets(npc, updatePacket);
			}
		}

		if (endBits)
			updatePacket.endBitWriter();

		updatePacket.endPacketTypeShort();
	}

	private static void sendCreateNPC(Npc npc, Location loc, PacketBuilder updatePacket)
	{
		int x = (npc.location().x - loc.x) & 0x1F;
		int y = (npc.location().y - loc.y) & 0x1F;

		updatePacket.addBits(15, npc.index);
		updatePacket.addBits(5, x);
		updatePacket.addBits(14, npc.id);
		updatePacket.addBits(5, y);
		updatePacket.addBits(1, (npc.updateFlags & 0x100) >> 8);
		updatePacket.addBits(3, npc.direction);
		updatePacket.addBits(1, 0); //Unsure.
	}

	private static void handleNPCMovement(Npc npc, PacketBuilder updatePacket)
	{
		if (npc.walk_direction < 0) {
			if ((npc.updateFlags & 0x100) == 0x100) {
				updatePacket.addBits(1,1);
				updatePacket.addBits(2,0);
			} else {
				updatePacket.addBits(1,0);
			}
		} else {
			updatePacket.addBits(1, 1);
			updatePacket.addBits(2, 1);
			updatePacket.addBits(3, npc.walk_direction);
			updatePacket.addBits(1, (npc.updateFlags & 0x100) >> 8);
		}		
	}

	//Mask 1   , HeadText = readString
	//Mask 2   , npcid = readLEShortA()  
	//Mask 4   , FaceEntityIndex = readShort()
	//Mask 8   , GfxId = readShortA() , GfxDelay = ReadLEShort() , GfxHeight = (ReadLEShort() >> 16))
	//Mask 16  , AnimId = readLEShortA(), AnimDelay = readByteC() 
	//Mask 32  , Hit1Dmg = readByteA(), Hit2Color = readByteA(), Hit2Numer = readByteA(), Hit2Denom = readByteA()
	//Mask 64  , TurnX = readLEShortA(), TurnY = readLEShortA()
	//Mask 128 , Hit2Dmg = readByte(), Hit1Color = readByteA(), Hit1Numer = readByte(), Hit1Denom = readByteC()

	private static void sendNpcUpdatePackets(Npc npc, PacketBuilder updatePacket)
	{
		updatePacket.addByte((byte)npc.updateFlags);

		if ((npc.updateFlags & 16) == 16) 
		{
			updatePacket.addLEShortA(npc.animationId);
			updatePacket.addByteC(npc.animationDelay);
		}
		if ((npc.updateFlags & 4) == 4) 
		{
			updatePacket.addShort(npc.faceIndex);
		}
		if ((npc.updateFlags & 2) == 2) 
		{
			updatePacket.addLEShortA(npc.id); 
		}
		if ((npc.updateFlags & 64) == 64) 
		{
			updatePacket.addLEShortA(0); //TurnToX Not Used Yet.
			updatePacket.addLEShortA(0); //TurnToY Not Used Yet.
		}
		if ((npc.updateFlags & 128) == 128) 
		{
			updatePacket.addByte(npc.hitDamage1);
			updatePacket.addByteA(npc.hitColor1);
			updatePacket.addByte((byte)npc.health);  //Current Health
			updatePacket.addByteC((byte)npc.health); //Maximum Health
		}
		if ((npc.updateFlags & 32) == 32) 
		{
			updatePacket.addByteA(npc.hitDamage2);
			updatePacket.addByteA(npc.hitColor2);
			updatePacket.addByteA((byte)npc.health); //Current Health
			updatePacket.addByteA((byte)npc.health); //Maximum Health
		}
		if ((npc.updateFlags & 1) == 1) 
		{
			updatePacket.addString(npc.chatHeadText);
		}
		if ((npc.updateFlags & 8) == 8) 
		{
			updatePacket.addShortA(npc.graphicsId);
			updatePacket.addInt1(npc.graphicsData); //This is a Little Endian Integer w/ 2 byte swap.
		}

		npc.updateFlags = npc.flagResetValue;
	}	
}
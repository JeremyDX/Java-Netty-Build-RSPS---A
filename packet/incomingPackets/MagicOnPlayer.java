/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.model.World;
import com.model.WalkingQueue;
import com.util.MagicSystem;

public class MagicOnPlayer implements PacketAssistant 
{
	public MagicOnPlayer() {

	}

	public void send(Player me, Packet packet)
	{
		int parent = packet.readShort();
		int child = packet.readShort();
		Player entity = World.getPlayerByCheckingIndex(packet.readLEShortA());

		if (entity == null)
			return;

		if (parent != 192)
			child |= 0x40;

		byte type;
		if((type = MagicSystem.validate(me, child, 160)) > -1)
		{
			if (type == 0)
			{
				me.combat().pushMagic(entity, child, false);
			}
		}
		me.packetDispatcher().sendMessage("Parent: " + parent + ",  Child: " + child);
	}
}
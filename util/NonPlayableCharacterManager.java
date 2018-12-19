public class NPCManager {

	public void update(Player p) {
		PacketBuilder updatePacket = p.getPacket().createPacketTypeShort(146);
		updatePacket.startBitWriter();	
		int size = p.npcList().getMainListSize();
		updatePacket.addBits(8, size);
		p.npcList().resetMainListSize();
		short[] list = p.npcList().getMainList();
		for (int i = 0; i < size; i++) {
			NPC npc = npcs[list[i]];
			if (npc == null || !p.location().withinDistance(npc.location())) {
				updatePacket.addBits(1, 1);
				updatePacket.addBits(2, 3);
				p.npcList().addSubList(list[i], false);
			} else {
				sendOtherNpcPosition(p2, updatePacket);
				p.npcList().addSubList(p2.INDEX, true);		
			}
		}
		for (int i = 0; i < curIndex; i++) {
			NPC npc = npcs[list[i]];
			if (p.location().withinDistance(npc.location())) {
				if (p.playerList().addMainList(npc.INDEX)) {
					sendCreateNpc(p, npc, updatePacket);
					p.npcList().addSubList(npc.INDEX, true);
				} else {
					p.npcList().addSubList(npc.INDEX, false);
				}
			}
		}
		updatePacket.addBits(11, 2047);
		updatePacket.endBitWriter();
		updatePacket.endPacketTypeShort();
	}
}
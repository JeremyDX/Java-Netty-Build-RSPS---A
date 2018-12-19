package com.model;

import com.util.ItemManagement;

public class Specials {


	private static final int[] INTERFACE_CONFIGS = 
	{
		12, 10, 10, 12, 10,
		0,  12, 12, 12, 10,
		10, 0,  12, 12, 12,
		87, 10, 10, 10, 10,
		10
	};

	private short index;
	public short specLeft = 1000;
	public double specModifier = 1.0;
	public boolean specOn = false;

	protected Specials() {

	}

	public void setIndex(short index) {
		this.index = index;
	}

	public boolean reverseSpec() {
		specOn(!specOn);
		return !specOn;
	}

	public void specOn(boolean on) {
		specOn = on;
		Player p = World.getPlayerByClientIndex(index);
		p.packetDispatcher().sendConfig(301, specOn ? 1 : 0);
	}
	
	public void setSpecialData(short weapon) {
		Player p = World.getPlayerByClientIndex(index);
		int slot = -1;
		if (slot == -1) {
			specModifier = 1.0;
		} else {
			specModifier = 1.0;
		}
	}

	public boolean lowerSpecial(int amount) {
		int left = specLeft - amount;
		specOn(false);
		if (left < 0)
			return false;
		Player p = World.getPlayerByClientIndex(index);
		p.packetDispatcher().sendConfig2(300, specLeft = (short)left);
		return true;
	}

	public void increaseSpecial() {
		if (specLeft == 1000)
			return;
		Player p = World.getPlayerByClientIndex(index);
		specLeft = specLeft > 749 ? (short)1000 : (short)(specLeft + 250);
		p.packetDispatcher().sendConfig2(300, specLeft);
	}

	public void sendSpecialBar(int interfaceId) {
		Player p = World.getPlayerByClientIndex(index);
		boolean show = ItemManagement.hasSpecial(p.equipment().items[3]);
		p.packetDispatcher().sendInterfaceConfig(interfaceId, INTERFACE_CONFIGS[interfaceId > 472 ? interfaceId - 454 : interfaceId - 75], show);
		p.packetDispatcher().sendConfig2(300, specLeft);
	}

}
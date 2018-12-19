/*
* @ Author - Digistr.
* @ Info - Contains All Information On Opened / Used Interfaces During GamePlay.
*/

package com.model;

public class InterfaceContainer {
	
	public int xInfo = -1;
	public byte state = 0;
	public short mainInterface = -1;
	public short subInterface = -1;
	public short chatInterface = -1;
	public short[] sidebar = new short[4];

	protected InterfaceContainer() {

	}

	public void resetInterfaces(Player p, boolean flag) 
	{
		if (mainInterface != -1) 
		{
			switch (mainInterface) {
				case 12:
					if (sidebar[0] == 15) 
					{
						p.packetDispatcher().sendMultiItems(149, 0, 93, p.inventory().items, p.inventory().amounts);
						p.packetDispatcher().sendCloseInterface(97);
						sidebar[0] = -1;
					}
				break;
				case 335:
				case 334:
					if (sidebar[0] == 336) 
					{
						p.packetDispatcher().sendCloseInterface(97);
						sidebar[0] = -1;
						if (flag)
							p.trade().declineTrade(true);
					}
				break;
				case 620:
					if (sidebar[0] == 621)
					{
						p.packetDispatcher().sendCloseInterface(97);
						sidebar[0] = -1;
					}
			}
			p.packetDispatcher().sendCloseInterface(77);
			state = 0;
			mainInterface = -1;
		}
	}

	@ Override
	public String toString() {
		return "Main: "+mainInterface+" Sub: "+subInterface+" Chat: "+chatInterface+" sideBars["+sidebar[0]+","+sidebar[1]+","+sidebar[2]+","+sidebar[3]+"].";
	}

}
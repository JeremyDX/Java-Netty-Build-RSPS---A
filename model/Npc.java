package com.model;

public class Npc
{
	public short index;
	public short id;

	public short updateFlags;
	public short flagResetValue; //This value will be what the updateFlags reset to keep @ 0 unless you need repeats I.e. Chat Messages!

	public String chatHeadText;
	public short faceIndex;
	public short graphicsId;
	public int graphicsData;
	public short animationId;
	public byte animationDelay;
	public byte hitDamage1;
	public byte hitDamage2;
	public byte hitColor1;
	public byte hitColor2;

	public short health;

	public byte direction = 3;
	public byte walk_direction = -1;

	private int absolute_location;
	private Location location;

	public Npc(short id)
	{
		this.id = id;
		this.location = new Location(0, 0, 0);
		this.absolute_location = 0;
		loadDefinitions(id, -1);
	}

	public Npc(short id, Location location)
	{
		this.id = id;
		this.absolute_location = location.x | (location.y << 16);
		this.location = new Location(location);
		loadDefinitions(id, -1);
	}
	
	public Npc(short id, int dataIndex)
	{
		loadDefinitions(-1, dataIndex);
	}

	public Location location()
	{
		return location;
	}

	private void loadDefinitions(int id, int dataIndex)
	{
		if (dataIndex == -1)
		{
			return;	
		}
	}

	public void setFaceToNpc(int index)
	{
		updateFlags |= 0x102;
		this.faceIndex = (short)(index | 32768);
	}

	public void setFaceToPlayer(short index)
	{
		updateFlags |= 0x102;
		this.faceIndex = index;
	}

	public void setHeadText(String value)
	{
		updateFlags |= 0x101; //1.
		chatHeadText = value;
	}

	public void setAnimation(short id)
	{
		updateFlags |= 0x110; //16.
		animationId = id;
		animationDelay = 0;
	}

	public void setGraphic(short id, int height, int delay)
	{
		updateFlags |= 0x108; //8.
		graphicsId = id;
		graphicsData = height << 16 | delay;
	}

	public void transformNpcType(short type)
	{
		id = type;
		loadDefinitions(type, -1);
		updateFlags |= 0x102;
	}

	public void setHitDamage(byte damage, byte color)
	{
		if ((updateFlags & 0x180) == 0x180)
		{
			updateFlags |= 0x120;
			hitDamage2 = damage;
			hitColor2 = color;
		} else {
			updateFlags |= 0x180;
			hitDamage1 = damage;
			hitColor1 = color;
		}
	}

	public void addToWorldList()
	{
		short slot = -1;

		for (short i = 0; i < World.totalNpcs; ++i)
		{
			if ((World.npcs[i].updateFlags & 0x400) != 0)
			{	
				slot = i;
				break;
			}	
		}

		if (slot == -1)
		{
			slot = World.totalNpcs;
			if (slot >= World.npcs.length)
			{
				System.out.println("Error Npc's Maximum Reached!");
				return;
			}
			World.totalNpcs++;
		}
		
		this.index = slot;
		World.npcs[slot] = this;
	}
}
/*
* @ Author - Digistr.
* @ Information - This stores The Object Or Non-Walkable Spots ObjectId, X, Z, Direction,Type.
* @ Information - There is no Y Coordinate because the Array used is 0 - 13000 and is used as Y Idx.
*/

package com.util;

public class Coordinate {

	private byte DTZ;
	private short ID;
	private short X;

	public int getLoc() {
		return X + ((DTZ >> 2) << 14);
	}

	protected Coordinate(short id, short x, byte z, byte dir, byte type) {
		DTZ = (byte)(dir + (type << 3) + (z << 5));
		ID = id;
		X = x;
	}
}
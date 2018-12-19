/*
* Stores each coordinate in this field.
*/

package com.util;

public class GrabbedObjectField {

	protected Coordinate[] positions;

	protected GrabbedObjectField(int size) {
		positions = new Coordinate[size];
	}

	public void addPosition(int idx, short id, short x, byte z, byte dir, byte type) 
	{
		positions[idx] = new Coordinate(id,x,z,dir,type);
	}
		
}
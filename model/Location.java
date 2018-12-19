/*
* @ Author - Digistr.
* @ info - Basic Handler for all position properties.
*/

package com.model;

public class Location {

	public short x,y,z;


   /*
   * This creates a new Location instance with Home Coordinates.
   */
	protected Location() {
		this(3086,3486,0);
	}

   /*
   * This creates a new Location instance with the input x,y,z coordinates.
   */
	public Location(int x, int y, int z) {
		this.x = (short)x;
		this.y = (short)y;
		this.z = (short)z;
	}

   /*
   * This creates a new Location instance with the input x,y,z coordinates.
   */
	public Location(Location loc) {
		this.x = loc.x;
		this.y = loc.y;
		this.z = loc.z;
	}

   /*
   * Sets this locations coordinates with a difference locations coordinates.
   */
	public void setLocation(Location loc) {
		x = loc.x;
		y = loc.y;
		z = loc.z;
	}

   /*
   * Sets this locations coordinates with the input x,y,z coordinates.
   */
	public void setCoords(int x, int y, int z) {
		this.x = (short) x;
		this.y = (short) y;
		this.z = (byte) z;
	}

   /*
   * Returns true if the coordinates equal each other.
   */
	public boolean equals(int x, int y, int z) {
		return this.y == y && this.x == x && this.z == z;
	}

   /*
   * adds to the players x,y coords used for the walkingQueue when we increment a step.
   */
	public void add(byte x, byte y) {
		this.x += x;
		this.y += y;
	}

   /*
   * remove from the players x,y coords used for the walkingQueue when our region updates.
   */
	public void minus(byte x, byte y) {
		this.x -= x;
		this.y -= y;
	}

   /*
   * The below are required for the client to do various things.
   */
	public int localX() {
		return x - 8 * (regionX() - 6);
	}

    	public int localY() {
		return y - 8 * (regionY() - 6);
	}

	public int regionX() {
		return (x >> 3);
	}

	public int regionY() {
		return (y >> 3);
	}

    	public int localX(Location loc) {
		return x - 8 * (loc.regionX() - 6);
  	}

	public int localY(Location loc) {
		return y - 8 * (loc.regionY() - 6);
	}

   /*
   * Returns the difference the 2 Locations are from each other.
   */
	public double getDistance(Location other) {
		int xdiff = x - other.x;
		int ydiff = y - other.y;
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	public int getMaxDistance(Location other)
	{
		int xDiff = x - other.x;
		int yDiff = y - other.y;
		xDiff *= xDiff;
		yDiff *= yDiff;
		if (xDiff > yDiff)
			return xDiff;
		return yDiff;
	}

	public boolean withinDistance(Location other) {
		if (other.z != z)
			return false;
		int xdiff = other.x - x;
		int ydiff = other.y - y;
		return xdiff >= -15 && xdiff < 15 && ydiff >= -15 && ydiff < 15;
	}
}
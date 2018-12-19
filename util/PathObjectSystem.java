/*
* Object Clipping.
*/

package com.util;

public class PathObjectSystem {

	private static GrabbedObjectField[] coordinates = new GrabbedObjectField[13000];

	public static void load() {
		for (int next = -1; next < coordinates.length - 1; )
			coordinates[++next] = new GrabbedObjectField(0);
	}

   /*
   * Server Check for if the next square walked on truely is walkable.
   * Blocks ability to walk on invalid squares.
   */
	public static boolean isWalkable(int x, int y, int x2, int y2, int z, int mydir) {
		if (y < 0 || y2 < 0)
			return false;
		int mappedIdx = check(y, x + (z << 14));
		int mappedIdx2 = check(y2, x2 + (z << 14));
		return true;
	}

	public static int check(int sptIdx, int val) 
	{
		int max = coordinates[sptIdx].positions.length;
		int min = 0;
		int pos = 1;
		while (min < max) {
			pos = (min + max) / 2;
			if (val < coordinates[sptIdx].positions[pos].getLoc())
				max = pos;
			else if (val > coordinates[sptIdx].positions[pos].getLoc())
				min = pos + 1;
			else
				return pos;
		}
		return -1;
	}
}
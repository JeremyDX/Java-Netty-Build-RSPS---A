/*
* This system is only used for Player Updating at the moment.
* MainList contains all viewable players. SubList contains wheather this person index needs updates.
* If SubList returns True means updates false means none.
*/

package com.util;

public class IndexContainer {

	private short[] mainList = new short[8];
	private boolean[] subList = new boolean[8];
	private short mainListWriter = 0;

	public IndexContainer() {

	}

	/*
	* This is called when we must insert the old players back into the list.
	* Only player's that we can see can be inserted back in.
	*/
	public void insertMainList(short index) {
		mainList[mainListWriter++] = index;
	}

	/*
	* This is called when we must add new players into our list or old players that where removed.
	*/
	public boolean addMainList(short index) {
		if (mainListWriter == mainList.length) {
			if (mainListWriter > 255)
				System.out.println("MainListSize: " + mainListWriter);
			short[] list = new short[mainList.length * 2];
			System.arraycopy(mainList, 0, list, 0, mainList.length);
			mainList = list;
		}
		if (index >= subList.length || !subList[index]) {
			mainList[mainListWriter++] = index;
			return true;
		}
		return false;
	}

	/*
	* This sets the player's index to false or true depending on if updates / changes.
	* Were made for this player.
	*/
	public void addSubList(short index, boolean sub) {
		if (index >= subList.length) {
			boolean[] list = new boolean[index + 1];
			System.arraycopy(subList, 0, list, 0, subList.length);
			subList = list;
		}
		subList[index] = sub;
	}

	/*
	* Returns mainList array.
	*/ 
	public short[] getMainList() {
		return mainList;
	}

	/*
	* Returns false if no updates required for this player or true if updates required.
	* Used during player creation to see if we need create player or not.
	*/ 
	public boolean getSubList(short index) {
		return subList[index];
	}

	/*
	* Returns total player's in our list so we don't read old / null index's.
	*/ 
	public int getMainListSize() {
		return mainListWriter;
	}

	/*
	* Reset's list we don't bother removing old players since they will be overlapped.
	* When new player are inserted / added.
	*/ 
	public void resetMainListSize() {
		mainListWriter = 0;
	}

}
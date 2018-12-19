/*
* @ Author - Digistr.
* @ Use's - Containing the index's of certain task's.
* @ info - If value is true is contained if false it's not.
* @ moreinfo - starts structured size at 10 and will increment only if needed.
*/

package com.util;

public class BooleanContainer {

	private boolean[] container = new boolean[10];

	public BooleanContainer() {

	}

	public boolean add(int index) {
		if (index >= container.length) {
			increase(index * 2);
			container[index] = true;
			return true;
		}
		if (container[index])
			return false;
		container[index] = true;
		return true;	
	}

	public boolean remove(int index) {
		if (index >= container.length) {
			return false;
		}
		if (!container[index])
			return false;
		container[index] = false;
		return true;
	}

	private void increase(int incrementer) {
		boolean[] newContainer = new boolean[incrementer];
		System.arraycopy(container,0,newContainer,0,container.length);
		container = newContainer;
	}
}
/*
* @ Author - Digistr. //05250819350
*/

package com.util;

public class BitIndexContainer
{
	private long[] cache;

	public BitIndexContainer(int size)
	{
		cache = new long[(size + 64) >> 6];	
	}

	public void enable(int index)
	{
		index &= 0xFF;
		cache[index >> 6] |= 1 << (index & 0x3f);
	}

	public void disable(int index)
	{
		index &= 0xFF;
		long value = 1 << (index & 0x3F);
		int n = index >> 6;
		cache[n] ^= cache[n] & value;
	}
	
}
/*
* @ Author - Digistr
* @ Info - Extremely Fast Coversion Methods.
*/

package com.util;

import java.nio.ByteBuffer;

public class GameUtility {

	private static Random random;

	public static final int[] LOGIN_KEYS = 
	{
 		1985066137, -1745113159, -713954854, 1681481266
	};

	public static final char VALID_CHARS[] = {
        	' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
        	'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
      		't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
        	'3', '4', '5', '6', '7', '8', '9'
	};

	public static int[] anIntArray233 = { 0, 1024, 2048, 3072, 4096, 5120,
	    6144, 8192, 9216, 12288, 10240, 11264, 16384, 18432, 17408, 20480,
	    21504, 22528, 23552, 24576, 25600, 26624, 27648, 28672, 29696,
	    30720, 31744, 32768, 33792, 34816, 35840, 36864, 536870912,
	    16777216, 37888, 65536, 38912, 131072, 196608, 33554432, 524288,
	    1048576, 1572864, 262144, 67108864, 4194304, 134217728, 327680,
	    8388608, 2097152, 12582912, 13631488, 14680064, 15728640,
	    100663296, 101187584, 101711872, 101974016, 102760448, 102236160,
	    40960, 393216, 229376, 117440512, 104857600, 109051904, 201326592,
	    205520896, 209715200, 213909504, 106954752, 218103808, 226492416,
	    234881024, 222298112, 224395264, 268435456, 272629760, 276824064,
	    285212672, 289406976, 223346688, 293601280, 301989888, 318767104,
	    297795584, 298844160, 310378496, 102498304, 335544320, 299892736,
	    300941312, 301006848, 300974080, 39936, 301465600, 49152,
	    1073741824, 369098752, 402653184, 1342177280, 1610612736,
	    469762048, 1476395008, -2147483648, -1879048192, 352321536,
	    1543503872, -2013265920, -1610612736, -1342177280, -1073741824,
	    -1543503872, 356515840, -1476395008, -805306368, -536870912,
	    -268435456, 1577058304, -134217728, 360710144, -67108864,
	    364904448, 51200, 57344, 52224, 301203456, 53248, 54272, 55296,
	    56320, 301072384, 301073408, 301074432, 301075456, 301076480,
	    301077504, 301078528, 301079552, 301080576, 301081600, 301082624,
	    301083648, 301084672, 301085696, 301086720, 301087744, 301088768,
	    301089792, 301090816, 301091840, 301092864, 301093888, 301094912,
	    301095936, 301096960, 301097984, 301099008, 301100032, 301101056,
	    301102080, 301103104, 301104128, 301105152, 301106176, 301107200,
	    301108224, 301109248, 301110272, 301111296, 301112320, 301113344,
	    301114368, 301115392, 301116416, 301117440, 301118464, 301119488,
	    301120512, 301121536, 301122560, 301123584, 301124608, 301125632,
	    301126656, 301127680, 301128704, 301129728, 301130752, 301131776,
	    301132800, 301133824, 301134848, 301135872, 301136896, 301137920,
	    301138944, 301139968, 301140992, 301142016, 301143040, 301144064,
	    301145088, 301146112, 301147136, 301148160, 301149184, 301150208,
	    301151232, 301152256, 301153280, 301154304, 301155328, 301156352,
	    301157376, 301158400, 301159424, 301160448, 301161472, 301162496,
	    301163520, 301164544, 301165568, 301166592, 301167616, 301168640,
	    301169664, 301170688, 301171712, 301172736, 301173760, 301174784,
	    301175808, 301176832, 301177856, 301178880, 301179904, 301180928,
	    301181952, 301182976, 301184000, 301185024, 301186048, 301187072,
	    301188096, 301189120, 301190144, 301191168, 301193216, 301195264,
	    301194240, 301197312, 301198336, 301199360, 301201408, 301202432
	};

	public static byte[] aByteArray235 = { 22, 22, 22, 22, 22, 22, 21, 22, 22,
	    20, 22, 22, 22, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	    22, 22, 22, 22, 22, 22, 3, 8, 22, 16, 22, 16, 17, 7, 13, 13, 13,
	    16, 7, 10, 6, 16, 10, 11, 12, 12, 12, 12, 13, 13, 14, 14, 11, 14,
	    19, 15, 17, 8, 11, 9, 10, 10, 10, 10, 11, 10, 9, 7, 12, 11, 10, 10,
	    9, 10, 10, 12, 10, 9, 8, 12, 12, 9, 14, 8, 12, 17, 16, 17, 22, 13,
	    21, 4, 7, 6, 5, 3, 6, 6, 5, 4, 10, 7, 5, 6, 4, 4, 6, 10, 5, 4, 4,
	    5, 7, 6, 10, 6, 10, 22, 19, 22, 14, 22, 22, 22, 22, 22, 22, 22, 22,
	    22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	    22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	    22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	    22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	    22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	    22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
	    22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 21, 22, 21, 22, 22, 22, 21,
	    22, 22 };

	private static int[] TEXT_KEYS = { 215, 203, 83, 158, 104, 101, 93,
			84, 107, 103, 109, 95, 94, 98, 89, 86, 70, 41, 32, 27, 24, 23, -1,
			-2, 26, -3, -4, 31, 30, -5, -6, -7, 37, 38, 36, -8, -9, -10, 40,
			-11, -12, 55, 48, 46, 47, -13, -14, -15, 52, 51, -16, -17, 54, -18,
			-19, 63, 60, 59, -20, -21, 62, -22, -23, 67, 66, -24, -25, 69, -26,
			-27, 199, 132, 80, 77, 76, -28, -29, 79, -30, -31, 87, 85, -32,
			-33, -34, -35, -36, 197, -37, 91, -38, 134, -39, -40, -41, 97, -42,
			-43, 133, 106, -44, 117, -45, -46, 139, -47, -48, 110, -49, -50,
			114, 113, -51, -52, 116, -53, -54, 135, 138, 136, 129, 125, 124,
			-55, -56, 130, 128, -57, -58, -59, 183, -60, -61, -62, -63, -64,
			148, -65, -66, 153, 149, 145, 144, -67, -68, 147, -69, -70, -71,
			152, 154, -72, -73, -74, 157, 171, -75, -76, 207, 184, 174, 167,
			166, 165, -77, -78, -79, 172, 170, -80, -81, -82, 178, -83, 177,
			182, -84, -85, 187, 181, -86, -87, -88, -89, 206, 221, -90, 189,
			-91, 198, 254, 262, 195, 196, -92, -93, -94, -95, -96, 252, 255,
			250, -97, 211, 209, -98, -99, 212, -100, 213, -101, -102, -103,
			224, -104, 232, 227, 220, 226, -105, -106, 246, 236, -107, 243,
			-108, -109, 231, 237, 235, -110, -111, 239, 238, -112, -113, -114,
			-115, -116, 241, -117, 244, -118, -119, 248, -120, 249, -121, -122,
			-123, 253, -124, -125, -126, -127, 259, 258, -128, -129, 261, -130,
			-131, 390, 327, 296, 281, 274, 271, 270, -132, -133, 273, -134,
			-135, 278, 277, -136, -137, 280, -138, -139, 289, 286, 285, -140,
			-141, 288, -142, -143, 293, 292, -144, -145, 295, -146, -147, 312,
			305, 302, 301, -148, -149, 304, -150, -151, 309, 308, -152, -153,
			311, -154, -155, 320, 317, 316, -156, -157, 319, -158, -159, 324,
			323, -160, -161, 326, -162, -163, 359, 344, 337, 334, 333, -164,
			-165, 336, -166, -167, 341, 340, -168, -169, 343, -170, -171, 352,
			349, 348, -172, -173, 351, -174, -175, 356, 355, -176, -177, 358,
			-178, -179, 375, 368, 365, 364, -180, -181, 367, -182, -183, 372,
			371, -184, -185, 374, -186, -187, 383, 380, 379, -188, -189, 382,
			-190, -191, 387, 386, -192, -193, 389, -194, -195, 454, 423, 408,
			401, 398, 397, -196, -197, 400, -198, -199, 405, 404, -200, -201,
			407, -202, -203, 416, 413, 412, -204, -205, 415, -206, -207, 420,
			419, -208, -209, 422, -210, -211, 439, 432, 429, 428, -212, -213,
			431, -214, -215, 436, 435, -216, -217, 438, -218, -219, 447, 444,
			443, -220, -221, 446, -222, -223, 451, 450, -224, -225, 453, -226,
			-227, 486, 471, 464, 461, 460, -228, -229, 463, -230, -231, 468,
			467, -232, -233, 470, -234, -235, 479, 476, 475, -236, -237, 478,
			-238, -239, 483, 482, -240, -241, 485, -242, -243, 499, 495, 492,
			491, -244, -245, 494, -246, -247, 497, -248, 502, -249, 506, 503,
			-250, -251, 505, -252, -253, 508, -254, 510, -255, -256, 0, 
	};

    /*
    * Transforms a Long value to a String (Ripped from most misc.java).
    */
	public static String longToString(long l) {
        	if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L || l % 37L == 0L)
            		return "Invalid Name";
        	int i = 0;
        	char ac[] = new char[12];
        	while (l != 0L && i < 12) {
       			long l1 = l;
    			l /= 37L;
           		ac[11 - i++] = VALID_CHARS[(int) (l1 - l * 37L)];
        	}
     		return new String(ac, 12 - i, i);
	}
	
   /*
   * Transforms a String to a Long Value (Ripped from most misc.java).
   */
	public static long stringToLong(String s) {
		if (s.length() > 12 || s.isEmpty())
			return 1;
		long l = 0L;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
	    		l *= 37L;
	    		if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
	    		else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
	    		else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L)
	    		l /= 37L;
		return l;
	}

   /*
   * 3 - 6x Faster Then Math.abs(value);
   */
	public static int abs(int value) {
		if (value > -1)
			return value;
		return -value;
	}

   /*
   * Lowercase's Strings at 2x speed of String.toLowerCase();
   */
	public static String toLowerCase(String s) {
		char[] c = s.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] > 64 && c[i] < 91)
				c[i] += 32;
		}
		return new String(c);
	}

   /*
   * Uppercase's Strings at 2x speed of String.toUpperCase();
   */
	public static String toUpperCase(String s) {
		char[] c = s.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] > 96 && c[i] < 123)
				c[i] -= 32;
		}
		return new String(c);
	}

   /*
   * Sets the random instance.
   */
	public static void setRandom() {
		random = new Random();
	}

    /*
    *  Returns a random value and is faster then Math.random();
    */
	public static int random(int value) {
		return random.random(value);
	}

   /*
   * Formats Players Name Completely.
   * Removes all characters that are not a-z,A-Z,0-9
   * Capitalizes first letter and all letters after a space.
   * Lowercases all capital letters not after a space.
   * Removes spaces in back + front then returns the newly formated username.
   * Takes roughly 5k - 20k nano seconds to do this all depends how often it's called.
   */
	public static String formatUsernameForProtocol(String username) {
		if (username.length() < 1)
			return "";
		char[] ch = username.trim().toCharArray();
		for (int i = 1; i < ch.length; i++) {
			if (ch[i] > 96 && ch[i] < 123) {
				if (ch[i - 1] == ' ')
					ch[i] -= 32;
				continue;
			}
			if (ch[i] > 64 && ch[i] < 91) {
				if (ch[i - 1] != ' ')
					ch[i] += 32;
				continue;
			}
			if (ch[i] > 47 && ch[i] < 58)
				continue;
			ch[i] = ' ';
		}
		if ((ch[0] > 47 && ch[0] < 58))
			return (new String(ch)).trim();
		if (ch[0] > 96 && ch[0] < 123)
			ch[0] -= 32;
		else if (ch[0] < 65 || ch[0] > 90)
			ch[0] = ' ';
		return (new String(ch)).trim();
		
	}

	public static String formatUsernameForProtocol(long l) 
	{
        	int i = 0;
        	char ac[] = new char[12];
        	while (l != 0L && i < 12) {
       			long l1 = l;
    			l /= 37L;
           		ac[11 - i++] = VALID_CHARS[(int) (l1 - l * 37L)];
        	}
     		return new String(ac, 12 - i, i);
	}

	public static String formatTimeForDisplay(int cycles, String message)
	{
		StringBuilder sb = new StringBuilder(8);
		
		long hours = cycles / 6000;
		cycles -= hours * 6000;
		
		long minutes = cycles / 100;
		cycles -= minutes * 100;

		int seconds = (int)(cycles * 0.6);

		sb.append(message);

		if (hours > 0)
		{
			if (hours < 10)
				sb.append('0');
			sb.append(hours);
			sb.append(':');
		}

		if (minutes < 10)
			sb.append('0');
		sb.append(minutes);
		sb.append(':');

		if (seconds < 10)
			sb.append('0');
		sb.append(seconds);

		return sb.toString();
	}

   /*
   * Decrypts Text - Ripped out of the client and renamed/edited slighly.
   */
	public static void decryptText(byte[] text, byte[] encryptedData, int offset) {
		int writerIndex = 0;	
		int readerIndex = offset;
		int searchIndex = 0;
		int length = text.length;
		for (;;) {
			byte textByte = encryptedData[readerIndex++];
			if (textByte >= 0)
				searchIndex++;
			else
				searchIndex = TEXT_KEYS[searchIndex];
			int charId;
			if ((charId = TEXT_KEYS[searchIndex]) < 0) {
				text[writerIndex++] = (byte) (charId ^ 0xffffffff);
				if (writerIndex >= length)
					break;
				searchIndex = 0;
			}
			if ((0x40 & textByte) == 0)
				searchIndex++;
			else
				searchIndex = TEXT_KEYS[searchIndex];
			if ((charId = TEXT_KEYS[searchIndex]) < 0) {
				text[writerIndex++] = (byte) (charId ^ 0xffffffff);
				if (writerIndex >= length)
					break;
				searchIndex = 0;
			}
			if ((0x20 & textByte) == 0)
				searchIndex++;
			else
				searchIndex = TEXT_KEYS[searchIndex];
			if ((charId = TEXT_KEYS[searchIndex]) < 0) {
				text[writerIndex++] = (byte) (charId ^ 0xffffffff);
				if (writerIndex >= length)
					break;
				searchIndex = 0;
			}
			if ((0x10 & textByte) == 0)
				searchIndex++;
			else
				searchIndex = TEXT_KEYS[searchIndex];
			if ((charId = TEXT_KEYS[searchIndex]) < 0) {
				text[writerIndex++] = (byte) (charId ^ 0xffffffff);
				if (writerIndex >= length)
					break;
				searchIndex = 0;
			}
			if ((0x8 & textByte) == 0)
				searchIndex++;
			else
				searchIndex = TEXT_KEYS[searchIndex];
			if ((charId = TEXT_KEYS[searchIndex]) < 0) {
				text[writerIndex++] = (byte) (charId ^ 0xffffffff);
				if (writerIndex >= length)
					break;
				searchIndex = 0;
			}
			if ((0x4 & textByte) == 0)
				searchIndex++;
			else
				searchIndex = TEXT_KEYS[searchIndex];
			if ((charId = TEXT_KEYS[searchIndex]) < 0) {
				text[writerIndex++] = (byte) (charId ^ 0xffffffff);
				if (writerIndex >= length)
					break;
				searchIndex = 0;
			}
			if ((0x2 & textByte) == 0)
				searchIndex++;
			else
				searchIndex = TEXT_KEYS[searchIndex];
			if ((charId = TEXT_KEYS[searchIndex]) < 0) {
				text[writerIndex++] = (byte) (charId ^ 0xffffffff);
				if (writerIndex >= length)
					break;
				searchIndex = 0;
			}
			if ((0x1 & textByte) == 0)
				searchIndex++;
			else
				searchIndex = TEXT_KEYS[searchIndex];
			if ((charId = TEXT_KEYS[searchIndex]) < 0) {
				text[writerIndex++] = (byte) (charId ^ 0xffffffff);
				if (writerIndex >= length)
					break;
				searchIndex = 0;
			}
		}
	}


   /*
   * Encrypts Text - Ripped out of the client and renamed/edited slighly.
   */
	public static int encryptText(byte[] encryptedText, byte[] decryptedText, int offset) {
		int decryptedLength = decryptedText.length;
		int readerIndex = offset;
		int i_29_ = 0;
		int i_30_ = 8;
		for (; decryptedLength > readerIndex; readerIndex++) {
			int curByte = 0xff & decryptedText[readerIndex];
			int i_32_ = anIntArray233[curByte];
			byte i_33_ = aByteArray235[curByte];
			int writerIndex = i_30_ >> 3;
			int i_35_ = i_30_ & 0x7;
			i_29_ &= (-i_35_ >> 473515839);
			i_30_ += i_33_;
			int i_36_ = ((-1 + (i_35_ + i_33_)) >> 3) + writerIndex;
			i_35_ += 24;
			encryptedText[writerIndex] = (byte) (i_29_ = (i_29_ | (i_32_ >>> i_35_)));
			if (i_36_ > writerIndex) {
		    		i_35_ -= 8;
		    		encryptedText[++writerIndex] = (byte) (i_29_ = i_32_ >>> i_35_);
		    		if (i_36_ > writerIndex) {
					i_35_ -= 8;
					encryptedText[++writerIndex] = (byte) (i_29_ = i_32_ >>> i_35_);
					if (i_36_ > writerIndex) {
			    			i_35_ -= 8;
			    			encryptedText[++writerIndex] = (byte) (i_29_ = i_32_ >>> i_35_);
			   			if (writerIndex < i_36_) {
							i_35_ -= 8;
							encryptedText[++writerIndex] = (byte) (i_29_ = i_32_ << -i_35_);
			    			}
					}
				}
			}
	    	}
		return 1 + ((7 + i_30_) >> 3);
	}


	public static void decipherXTEA32(ByteBuffer buffer, int start, int end, int[] key) {
		if (key.length != 4)
			throw new IllegalArgumentException();

		int numQuads = (end - start) / 8;
		for (int i = 0; i < numQuads; i++) {
			int sum = 0x9E3779B9 * 32;
			int v0 = buffer.getInt(start + i * 8);
			int v1 = buffer.getInt(start + i * 8 + 4);
			for (int j = 0; j < 32; j++) {
				v1 -= (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
				sum -= 0x9E3779B9;
				v0 -= (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
			}
			buffer.putInt(start + i * 8, v0);
			buffer.putInt(start + i * 8 + 4, v1);
		}
	}

}

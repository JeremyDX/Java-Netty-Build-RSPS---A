/*
* @ Author - Digistr
* @ Objective - Contains Basic Information We Need For A Login Attempt #2.
* @ Info - if SecondLoginAttempt Is Successfull we create a Player Class.
*/

package com.model;
import org.jboss.netty.channel.Channel;
import java.net.InetSocketAddress;
import com.util.GameUtility;

public class PlayerLoginDetails 
{
	public int STATUS = 0x0;
	public final String USERNAME_AS_STRING;
	public final long USERNAME_AS_LONG;
	public long CUSTOM_USERNAME;
	public final char[] IP;
	public final char[] PASSWORD;
	public final Channel CHANNEL;

	public PlayerLoginDetails(long nameLong, String pass)
	{
		USERNAME_AS_STRING = "";
		USERNAME_AS_LONG = nameLong;
		PASSWORD = pass.toCharArray();
		IP = "First Time Logging In".toCharArray();
		CHANNEL = null;
	}

	public PlayerLoginDetails(long nameLong, String pass, Channel channel, long clientIndentification) 
	{
		USERNAME_AS_STRING = GameUtility.formatUsernameForProtocol(GameUtility.longToString(nameLong));
		USERNAME_AS_LONG = nameLong;
		CUSTOM_USERNAME = USERNAME_AS_LONG;
		PASSWORD = pass.toCharArray();
		CHANNEL = channel;
		IP = ((InetSocketAddress)channel.getRemoteAddress()).getHostName().toCharArray();
	}

	public void createCustomName(String name) 
	{
		CUSTOM_USERNAME = GameUtility.stringToLong(GameUtility.formatUsernameForProtocol(name));
	}

	/*
	 * Returns true if 1: Player is Active. 2: Player connection is Active 3: NOT in process of disconnecting.
	 */
	public boolean isPlayerActive()
	{
		return (STATUS & 0xE) == 0x6;	
	}

	/*
	 * STATUS RANKS
	 * 0x1 = Existing Player.
	 * 0x2 = Active In Player World List.
	 * 0x4 = Connection is Available.
	 * 0x8 = In Process of Disconnecting.
	 */
}

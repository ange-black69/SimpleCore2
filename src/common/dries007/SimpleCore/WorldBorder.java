package dries007.SimpleCore;

import java.util.EnumSet;

import net.minecraft.src.EntityPlayerMP;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class WorldBorder implements IScheduledTickHandler
{
	protected int ticks = 0;
	public static boolean enable = false;
	public static int minX = -150;
	public static int maxX = 150;
	public static int minZ = -150;
	public static int maxZ = 150;
	private static int players = 1;
	
	public static String wbMessage;
	
	public static void setWorldBorder(int minX, int maxX, int minZ, int maxZ)
	{
		WorldBorder.minX = minX;
		WorldBorder.maxX = maxX;
		WorldBorder.minZ = minZ;
		WorldBorder.maxZ = maxZ;
	}
	
	public void checkPlayer(EntityPlayerMP player)
	{
		if(player.posX < minX)
		{
			player.sendChatToPlayer(color.RED + wbMessage);
			player.playerNetServerHandler.setPlayerLocation(minX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		}
		if(player.posX > maxX)
		{
			player.sendChatToPlayer(color.RED + wbMessage);
			player.playerNetServerHandler.setPlayerLocation(maxX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		}
		if(player.posZ < minZ)
		{
			player.sendChatToPlayer(color.RED + wbMessage);
			player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, minZ, player.rotationYaw, player.rotationPitch);
		}
		if(player.posZ > maxZ)
		{
			player.sendChatToPlayer(color.RED + wbMessage);
			player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, maxZ, player.rotationYaw, player.rotationPitch);
		}
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
    	if(this.ticks >= Integer.MAX_VALUE) this.ticks = 1;
    	this.ticks ++;    	
		if(!enable) return;
		
		if(ticks % players == 0)
		{
			players = SimpleCore.server.getAllUsernames().length + 1;
		}
		else
		{
			EntityPlayerMP player = ((EntityPlayerMP)SimpleCore.server.getConfigurationManager().playerEntityList.get((int) (ticks % players - 1)));
			checkPlayer(player);
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
		
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() 
	{
		return "WorldBorder";
	}

	@Override
	public int nextTickSpacing() 
	{
		if(players < 50)
		{
			return 20;
		}
		else if (players < 100)
		{
			return 10;
		}
		else if (players < 200)
		{
			return 5;
		}
		else
		{
			return 0;
		}
	}

}

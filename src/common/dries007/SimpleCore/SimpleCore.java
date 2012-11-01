package dries007.SimpleCore;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.server.FMLServerHandler;
import dries007.SimpleCore.Commands.*;
import dries007.SimpleCore.asm.SimpleCorePlugin;
import dries007.SimpleCore.asm.SimpleCoreTransformer;

public class SimpleCore extends DummyModContainer
{
	public static NBTTagCompound playerData;
	public static NBTTagCompound rankData;
	public static String defaultRank;
	public static String opRank;
	public static Boolean spawnOverride;
	
	public static HashSet<String> availablePermission = new HashSet<String>();
	public static HashSet<String> availableRanks = new HashSet<String>();
	
	public static Boolean postModlist;
	public static String postLocation;

	public static MinecraftServer server;
	
	public static NBTTagCompound defSettings = new NBTTagCompound();
	
	public SimpleCore()
	{
		super(new ModMetadata());
		ModMetadata meta	=	getMetadata();
        meta.modId      	=	"SimpleCore";
        meta.name       	=	"SimpleCore";
        meta.version    	=	"0.1";
        meta.authorList 	=	Arrays.asList("Dries007");
        meta.credits		=	"Dries007, ChickenBones for making his mods open-source!";
        meta.description	=	"Provides a framework for other SimpleServer mods. This includes world bound data storage and a basic permission system.";
        meta.url        	=	"http://ssm.dries007.net";
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		bus.register(this);
		MinecraftForge.EVENT_BUS.register(this);
		return true;		
	}
	
	@Subscribe
	public void serverStarting(FMLServerStartingEvent event)
	{
		server = ModLoader.getMinecraftServerInstance();
		
		NBTTagInt example = new NBTTagInt("Example", 42);
		Permissions.addDefaultSetting(example);
		
		playerData=data.loadData("playerData");
		rankData=data.loadData("rankData");
		
		Permissions.addPermission("SC.admin");
		
		if(!rankData.hasKey(opRank)) newRank(opRank);
		if(!rankData.hasKey(defaultRank)) newRank(defaultRank);
		
		for(Object base : rankData.getTags())
		{
			NBTBase tag = (NBTBase) base;
			Permissions.addRank(tag.getName());
		}
		
		addcommands();
		
		GameRegistry.registerPlayerTracker(new PlayerTracker());
	}
	
	@Subscribe
	public void serverStarted(FMLServerStartedEvent event)
	{
		if(event.getSide().isServer() && postModlist) writemodlist(event);
	}
	
	@ForgeSubscribe
	public void chuckSave(WorldEvent.Save event)
	{
		data.saveData(playerData, "playerData");
		data.saveData(rankData, "rankData");
	}
	
	@Subscribe
	public void serverStopping(FMLServerStoppingEvent event)
	{
		data.saveData(playerData, "playerData");
		data.saveData(rankData, "rankData");
	}
	
	public void addcommands()
	{
		ICommandManager commandManager = server.getCommandManager();
		ServerCommandManager manager = ((ServerCommandManager) commandManager); 
		
		manager.registerCommand(new CommandPermissions());
		manager.registerCommand(new CommandPromote());
		manager.registerCommand(new CommandAddrank());
		manager.registerCommand(new CommandRanks());
		manager.registerCommand(new CommandPlayer());
		manager.registerCommand(new CommandRank());
		manager.registerCommand(new CommandSetSpawn());
	}
	
	public static void writemodlist(FMLServerStartedEvent event)
	{
		try
		{
			Calendar cal = Calendar.getInstance();
			FileWriter fstream = new FileWriter(postLocation);
			PrintWriter out = new PrintWriter(fstream);
			out.println("# --- ModList ---");
			out.println("# Generated: " + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.YEAR) + " (Server time)");
			out.println("# Change the lacation of this file in config/SimpleCore.cfg");
			out.println();
			
			for(ModContainer mod : Loader.instance().getModList())
			{
				String url = "";
				if(!mod.getMetadata().url.isEmpty()) url = mod.getMetadata().url;
				if(!mod.getMetadata().updateUrl.isEmpty()) url = mod.getMetadata().updateUrl;
				out.println(mod.getName() + " Version:" + mod.getVersion() + " URL:" + url);
			}
				
			out.close();
		}
		catch (Exception e)
		{	
			FMLLog.severe("Error writing to modlist");
			FMLLog.severe(e.getLocalizedMessage());
		}
	}	
	
	public static boolean newRank(String name, String nameToCopy)
	{
		Permissions.addRank(name);
		if (!rankData.hasKey(name))
		{
			rankData.setCompoundTag(name, rankData.getCompoundTag(name));
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean newRank(String name)
	{
		Permissions.addRank(name);
		if (!rankData.hasKey(name))
		{
			NBTTagCompound rank = new NBTTagCompound();
			rank.setCompoundTag("Settings", defSettings);
			rank.setCompoundTag("Permissions", new NBTTagCompound());
			rankData.setCompoundTag(name, rank);
			return true;
		}
		else
		{
			return false;
		}
	}
}
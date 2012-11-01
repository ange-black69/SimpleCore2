package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandPlayer extends CommandBase
{
	public String getCommandName()
    {
        return "player";
    }

    public String getCommandUsage(ICommandSender sender)
    {
    	return "/" + getCommandName() + " <player> <permission> <allow|deny> ";
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if(args.length == 1)
        {
        	return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());   
        }
        else if(args.length == 2)
        {
        	String msg = "";
        	for(String st : Permissions.getPermissions()) msg = msg + st + ", ";
        	sender.sendChatToPlayer("List of permissions: " + msg);
        	return getListOfStringsMatchingLastWord(args, Permissions.getPermissions());
        }
        else if (args.length == 3)
        {
        	return getListOfStringsMatchingLastWord(args, "allow", "deny");
        }
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {	
    	if(args.length!=3) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	EntityPlayer target = func_82359_c(sender, args[0]);
    	
    	NBTTagCompound data1 = SimpleCore.playerData.getCompoundTag(target.username);
    	NBTTagCompound permissions = data1.getCompoundTag("Permissions");
    	
    	if(args[2].equalsIgnoreCase("allow"))
    	{
    		permissions.setBoolean(args[1], true);
    		sender.sendChatToPlayer("You have allowed '" + target.username + "' '" + args[1] + "'.");
    	}
    	else if(args[2].equalsIgnoreCase("deny"))
    	{
    		permissions.setBoolean(args[1], false);
    		sender.sendChatToPlayer("You have denied '" + target.username + "' '" + args[1] + "'.");
    	}
    	else throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	data1.setCompoundTag("Permissions", permissions);
    	SimpleCore.playerData.setCompoundTag(target.username, data1);
    	data.saveData(SimpleCore.playerData, "playerData");
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SC.admin");
    }
}

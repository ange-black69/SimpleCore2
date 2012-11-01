package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandPromote extends CommandBase
{	
    public String getCommandName()
    {
        return "promote";
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
    	return "/" + getCommandName() + " <player> <rank>";
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {
    	EntityPlayer target = func_82359_c(sender, args[0]);
    	Iterator ranks = SimpleCore.rankData.getTags().iterator();
    	while (ranks.hasNext())
    	{
    		NBTTagCompound rank = (NBTTagCompound) ranks.next();
    		if (rank.getName().equalsIgnoreCase(args[1]))
    		{
    			NBTTagCompound playerData = SimpleCore.playerData.getCompoundTag(target.username);
    			playerData.setString("Rank", rank.getName());
    			SimpleCore.playerData.setCompoundTag(target.username, playerData);
    			sender.sendChatToPlayer("You have promoted " + target.username + " to the rank of " + Permissions.getRank(target));
    			target.sendChatToPlayer("You have been promoted to the rank of " + Permissions.getRank(target) + " by " + sender.getCommandSenderName());
    			return;
    		}
    	}
    	sender.sendChatToPlayer("Can't find the rank '"+args[1]+"'");
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return Permissions.hasPermission(sender.getCommandSenderName(), "SC.admin");
    }
    
    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if(args.length == 1)
        {
        	return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        else if(args.length == 2)
        {
        	String msg = "";
        	for(String st : Permissions.getRanks()) msg = msg + st + ", ";
        	sender.sendChatToPlayer("List of ranks: " + msg);
        	return getListOfStringsMatchingLastWord(args, Permissions.getRanks());
        }
        return null;
    }
}

package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandAddrank extends CommandBase
{
    public String getCommandName()
    {
        return "addrank";
    }

    public String getCommandUsage(ICommandSender sender)
    {
    	return "/" + getCommandName() + " <name> [copyOtherRankName]";
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SC.admin");
    }
    
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if(args.length == 2)
        {
        	String msg = "";
        	for(String st : Permissions.getRanks()) msg = msg + st + ", ";
        	sender.sendChatToPlayer("List of ranks: " + msg);
        	return getListOfStringsMatchingLastWord(args, Permissions.getRanks());
        }
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {
    	if(args.length==0) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	if(args.length>2) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	if(!SimpleCore.rankData.hasKey(args[0]))
    	{
    		if(args.length==2)
    		{
    			Iterator ranks = SimpleCore.rankData.getTags().iterator();
            	int i = 0;
            	while (ranks.hasNext())
            	{
            		NBTTagCompound rank = (NBTTagCompound) ranks.next();
            		if(rank.getName().trim().equalsIgnoreCase(args[1]))
            		{
            			SimpleCore.rankData.setCompoundTag(args[0], (NBTTagCompound) SimpleCore.rankData.getCompoundTag(rank.getName()).copy());
            			sender.sendChatToPlayer("Rank " + args[0] + " made by copying " + args[1] + ".");
            			return;
            		}
            	}
            	sender.sendChatToPlayer("Rank to copy (" + args[1] + ") doesn't exist.");
    		}
    		else
    		{
    			SimpleCore.newRank(args[0]);
    			sender.sendChatToPlayer("Rank " + args[0] + " made.");
    		}
    	}
    	else
    	{
    		sender.sendChatToPlayer("Rank " + args[0] + " already exists.");
    	}
    }
}

package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandRank extends CommandBase
{
    public String getCommandName()
    {
        return "rank";
    }

    public String getCommandUsage(ICommandSender sender)
    {
    	return "/" + getCommandName() + " <rank> <permission> <allow|deny> ";
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {	
    	if(args.length!=3) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	
    	String rank = getRank(args[0]);
    	String permission = getPermission(args[1]);
    	
    	NBTTagCompound data1 = SimpleCore.rankData.getCompoundTag(rank);
    	NBTTagCompound permissions = data1.getCompoundTag("Permissions");
    	
    	if(args[2].equalsIgnoreCase("allow"))
    	{
    		permissions.setBoolean(args[1], true);
    		sender.sendChatToPlayer("You have allowed '" + rank + "' '" + permission + "'.");
    	}
    	else if(args[2].equalsIgnoreCase("deny"))
    	{
    		permissions.setBoolean(args[1], false);
    		sender.sendChatToPlayer("You have denied '" + rank + "' '" + permission + "'.");
    	}
    	else throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	data1.setCompoundTag("Permissions", permissions);
    	SimpleCore.rankData.setCompoundTag(rank, data1);
    	data.saveData(SimpleCore.rankData, "rankData");
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SC.admin");
    }
    
    protected String getRank(String input)
    {
    	for(String st : SimpleCore.availableRanks)
    	{
    		if(st.equalsIgnoreCase(input)) return input;
    	}
    	throw new WrongUsageException("Rank '" + input + "' not found!", new Object[0]);
    }
    
    protected String getPermission(String input)
    {
    	for(String st : SimpleCore.availablePermission)
    	{
    		if(st.equalsIgnoreCase(input)) return input;
    	}
    	throw new WrongUsageException("Permission '" + input + "' not found!", new Object[0]);
    }
    
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
    	if(args.length == 1)
        {
    		String msg = "";
        	for(String st : Permissions.getRanks()) msg = msg + st + ", ";
        	sender.sendChatToPlayer("List of ranks: " + msg);
        	return getListOfStringsMatchingLastWord(args, Permissions.getRanks());
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
}

package dries007.SimpleCore.Commands;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleCore.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandPermissions extends CommandBase
{
    public String getCommandName()
    {
        return "permissions";
    }

    public String getCommandUsage(ICommandSender sender)
    {
    	return "/" + getCommandName();
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {
    	String msg = "";
    	for(String st : Permissions.getPermissions()) msg = msg + st + ", ";
    	sender.sendChatToPlayer("List of permissions: " + msg);
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return true;
    }
}

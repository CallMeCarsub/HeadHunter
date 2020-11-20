package tv.tirco.headhunter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import tv.tirco.headhunter.config.Config;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class HeadHunterCommand implements CommandExecutor,TabCompleter {
	
	String prefix = MessageHandler.getInstance().translateTags(MessageHandler.getInstance().prefix);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(!(sender instanceof Player)) {
    		sender.sendMessage(prefix + " This command can only be used by players.");
    		return true;
    	}
    	//ARGS - LIST, COUNT, TOP, HELP
    	
    	Player player = (Player) sender;

    	if(args.length < 1) {
			String s = MessageHandler.getInstance().translateTags(Config.getInstance().getMessageCount(), player);
			player.sendMessage(s);
        	return true;
    	} else {
    		if(args[0].equalsIgnoreCase("count")) {
    			String s = MessageHandler.getInstance().translateTags(Config.getInstance().getMessageCount(), player);
    			player.sendMessage(s);
    		} else if(args[0].equalsIgnoreCase("list")) {
    			if(!UserManager.hasPlayerDataKey(player)){
    				player.sendMessage("Unable to do this at this time.");
    				return true;
    			}
    			
    			PlayerData pData = UserManager.getPlayer(player);
    			
    			ComponentBuilder message = new ComponentBuilder("");
    			player.sendMessage(ChatColor.GOLD + "-- Here is a list of all heads you can find. --");
    			player.sendMessage(ChatColor.GOLD + "(" + ChatColor.GREEN + " Found. " + ChatColor.GOLD + "/"+ ChatColor.RED + " Not Found. " + ChatColor.GOLD + ")");
    			
    			for(int i : Heads.getInstance().getHeads().keySet() ) {
    				ChatColor c = ChatColor.RED;
    				if(pData.hasFound(i)) {
    					c = ChatColor.GREEN;
    				}
    				TextComponent string = new TextComponent("" + c + i + " ");
    				Text hint = new Text(ChatColor.translateAlternateColorCodes('&', Heads.getInstance().getHint(i)));
    				string.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hint));
    				message.append(string);

    			}
    			player.spigot().sendMessage(message.create());
    			player.sendMessage(ChatColor.GOLD + "" +  ChatColor.ITALIC + "Hover a number to see its hint.");
    			return true;

    		} else if(args[0].equalsIgnoreCase("top")) {
    			player.sendMessage("Not yet implemented.");
    			return true;
    		} else { //Help command or wrong command.
    			
    			player.sendMessage(prefix + " /hh list - List the heads you have and have not found.");
    			player.sendMessage(prefix + " /hh count - Shows the amount of heads you have found.");
    			player.sendMessage(prefix + " /hh top - Get the list of the users that have found the most.");
    			return true;
    		}
    	}

    	return true;
    }
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> commands = ImmutableList.of("count","list","help");
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<String>(commands.size()));
		default:
			return null;
    	}
			
	}
}

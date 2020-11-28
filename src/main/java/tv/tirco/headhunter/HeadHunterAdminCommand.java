package tv.tirco.headhunter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class HeadHunterAdminCommand implements CommandExecutor,TabCompleter {
	
	String prefix = MessageHandler.getInstance().translateTags(MessageHandler.getInstance().prefix);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(!(sender instanceof Player)) {
    		sender.sendMessage("This command can only be used by players.");
    		return true;
    	}
    	
    	Player player = (Player) sender;
    	
    	if(args.length < 1 || args[0].equalsIgnoreCase("help")) {
			player.sendMessage(prefix + " /hha help" + ChatColor.WHITE + " - this.");
			player.sendMessage(prefix + " /hha find <id>" + ChatColor.WHITE + " - get head location.");
			player.sendMessage(prefix + " /hha findforuser <name>" + ChatColor.WHITE + " - set head as found.");
			player.sendMessage(prefix + " /hha seelistas <name> (true/false)" + ChatColor.WHITE + " - see heads they have. If true is set, it will display the list with numbers instead of the head names.");
			player.sendMessage("");
			player.sendMessage(prefix + " /hha add (on/off)" + ChatColor.WHITE + " - toggle addmode.");
			player.sendMessage(prefix + " /hha sethint <id> <msg>"+ ChatColor.WHITE + " - set hint.");
			player.sendMessage(prefix + " /hha setname <id> <name>"+ ChatColor.WHITE + " - set name.");
			player.sendMessage(prefix + " /hha setcommand <id> <cmd>"+ ChatColor.WHITE + " - set command.");
			player.sendMessage(prefix + " /hha delete <id>"+ ChatColor.WHITE + " - delete head.");
			player.sendMessage("");
			player.sendMessage(prefix + " /hha debug (on/off)" + ChatColor.WHITE + " - toggle debug.");
			player.sendMessage(prefix + " /hha notifyadmins (on/off)" + ChatColor.WHITE + " - toggle debug in chat.");
			player.sendMessage(prefix + " /hha forcesfave (heads/users)"+ ChatColor.WHITE + " - forcesave file");
			return true;
    	} 
    	
  //ForceSave
    	if(args[0].equalsIgnoreCase("forcesave")) {
    		if(args.length > 1) {
    			if(args[1].equalsIgnoreCase("config") || args[1].equalsIgnoreCase("heads")) {
    	        	player.sendMessage(prefix + " Saving config!");
    	        	Heads.getInstance().saveHeads();
    	        	return true;
    			} else if(args[1].equalsIgnoreCase("users")) {
    				player.sendMessage(prefix + " Saving users!");
    	        	UserManager.saveAll();
    	        	return true;
    			}
    		}
        	player.sendMessage(prefix + " Saving config!");
        	Heads.getInstance().saveHeads();
        	player.sendMessage(prefix + " Saving users!");
        	UserManager.saveAll();
        	return true;
        	
        	
        	
        	
    	} else if(args[0].equalsIgnoreCase("add")){ //toggle on/off
    		if(!UserManager.hasPlayerDataKey(player)) {
    			player.sendMessage(prefix + " Could not perform this action at this time.");
    			return true;
    		}
    		PlayerData pData = UserManager.getPlayer(player);
    		if(args.length > 1) {
    			if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {
    				pData.setAddMode(true);
    			} else {
    				pData.setAddMode(false);
    			}
    		} else {
    			pData.toggleAddMode();
    		}
    		Boolean addMode = pData.getAddMode();
    		player.sendMessage(prefix + " Add mode is set to " + (addMode ? ChatColor.GREEN : ChatColor.RED) + addMode);
    		if(addMode) {
    			player.sendMessage(prefix + ChatColor.RED +  " Warning:" + ChatColor.GREEN + " All playerheads you now place will automatically be added to the HeadHunter list.");
    		}
    		return true;
    		
    		
    		
    		
    		
    	} else if(args[0].equalsIgnoreCase("debug")){ //toggle on/off
    		
    		boolean state = !MessageHandler.getInstance().getDebugState();
    		if(args.length > 1) {
    			if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {
    				state = true;
    			} else {
    				state = false;
    			}
    		}
    		MessageHandler.getInstance().setDebugState(state);
    		player.sendMessage(prefix + " Debug has now been set to " + (state ? ChatColor.GREEN : ChatColor.RED) + state);
    		return true;
    		
    		
    		
    		
    		
    	} else if(args[0].equalsIgnoreCase("notifyadmins")){ //toggle on/off
    	
    		boolean state = !MessageHandler.getInstance().getDebugToAdminState();
    		if(args.length > 1) {
    			if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("true")) {
    				state = true;
    			} else {
    				state = false;
    			}
    		}
    		MessageHandler.getInstance().setDebugToAdminState(state);
    		player.sendMessage(prefix + " AdminNotifications has now been set to " + (state ? ChatColor.GREEN : ChatColor.RED) + state);
    		return true;
    		
    		
    		
    		
    		
    	} else if(args[0].equals("findforuser")) {
    		//hha findforuser name id
    		if(args.length < 3) {
    			player.sendMessage(prefix + " /hha findforuser name id");
    			return true;
    		}
    		String playerName = args[1];
    		int id = 0;
        	try {
        		id = Integer.parseInt(args[2]);
        	} catch(NumberFormatException ex) {
        		player.sendMessage(prefix + " Could not parse " + args[2] + " to a number.");
        		player.sendMessage(prefix + " /hha findforuser name id");
        		return false;
        	}
        	
        	Player target = Bukkit.getPlayerExact(playerName);
        	if(target == null) {
        		player.sendMessage(prefix +"The specified player "+ playerName + " could not be found.");
        		return true;
        	}
        	if(!UserManager.hasPlayerDataKey(target)) {
        		player.sendMessage(prefix +"The specified player "+ playerName + " is not currently loaded.");
        		return true;
        	}
        	PlayerData tData = UserManager.getPlayer(target);
        	tData.find(id);
        	player.sendMessage(prefix + " The head " + args[2] + " has been unlocked for player " + playerName);
        	return true;
        	
        	
        	
        	
    	} else if(args[0].equals("seelistas")) {
    		//hha findforuser name id
    		if(args.length < 2) {
    			player.sendMessage(prefix + " /hha seelistas name");
    			return true;
    		}
    		String playerName = args[1];

        	Player target = Bukkit.getPlayerExact(playerName);
        	if(target == null) {
        		player.sendMessage(prefix +"The specified player "+ playerName + " could not be found.");
        		return true;
        	}
        	if(!UserManager.hasPlayerDataKey(target)) {
        		player.sendMessage(prefix +"The specified player "+ playerName + " is not currently loaded.");
        		return true;
        	}
        	PlayerData tData = UserManager.getPlayer(target);
    		player.sendMessage(ChatColor.GOLD + "-- Here is a list of all heads "+ playerName + " can find. --");
        	MessageHandler.getInstance().seeList(tData, player);
        	return true;
    	}
    	
    	
    	if(args.length < 2) {
    		player.sendMessage(prefix + " Please specify the ID of the head you want to edit.");
    		return true;
    	}
    	int id = 0;
    	
    	try {
    		id = Integer.parseInt(args[1]);
    	} catch(NumberFormatException ex) {
    		player.sendMessage(prefix + " Could not parse " + args[1] + " to a number.");
    		return false;
    	}
    	
    	if(!Heads.getInstance().headExists(id)) {
    		player.sendMessage(prefix + " There is no head with ID " + id);
    		return true;
    	}
	
    	if(args[0].equalsIgnoreCase("find")){
    		Location loc = Heads.getInstance().getLocFromHeadId(id);
    		
    		ComponentBuilder message = new ComponentBuilder("");
    		String coords = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
			TextComponent string = new TextComponent(prefix + "Head " + id + " is located at " + ChatColor.WHITE + coords);
			Text hint = new Text("Click for TP command. \n/gamemode spectator is recommended.");
			string.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hint));
			string.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp " + coords));
			message.append(string);
			player.spigot().sendMessage(message.create());
			return true;
    		
    		
    	} else if(args[0].equalsIgnoreCase("delete")){
    		Heads.getInstance().deleteHead(id);
    		player.sendMessage(prefix + " Head " + id + " has been removed. Note that this might cause issues.");
    		return true;
    		
    	} else if(args[0].equalsIgnoreCase("sethint")){
    		StringBuilder sb = new StringBuilder("");
    		for (int i = 2; i < args.length; i++) {
    		    sb.append(args[i]).append(' ');
    		}
    		String hint = sb.toString().substring(0, sb.toString().length() - 1);
    		Heads.getInstance().setHint(id,hint);
    		player.sendMessage(ChatColor.GREEN + "Head " + id + " now has its hint set as:");
    		player.sendMessage(ChatColor.translateAlternateColorCodes('&', hint));
    		return true;
    		
    		
    	} else if(args[0].equals("setname")) {
    		StringBuilder sb = new StringBuilder("");
    		for (int i = 2; i < args.length; i++) {
    		    sb.append(args[i]).append(' ');
    		}
    		String name = sb.toString().substring(0, sb.toString().length() - 1);
    		if(Heads.getInstance().setName(id,name,true)) {
        		player.sendMessage(ChatColor.GREEN + "Head " + id + " now has its name set as:");
        		player.sendMessage(ChatColor.translateAlternateColorCodes('&', name));
    		} else {
    			player.sendMessage(ChatColor.RED + "A head with the name " + name + " already exists.");
    		}
    		return true;
    		
    		
    	} else if(args[0].equals("setcommand")) {
    		StringBuilder sb = new StringBuilder("");
    		for (int i = 2; i < args.length; i++) {
    		    sb.append(args[i]).append(' ');
    		}
    		String command = sb.toString().substring(0, sb.toString().length() - 1);
    		Heads.getInstance().setCommand(id,command);
    		player.sendMessage(ChatColor.GREEN + "Head " + id + " now has its command set as:");
    		player.sendMessage(command);
    		
    		return true;
    	}
    	
        return true;
    }
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> commands = ImmutableList.of("help","add","find","sethint","delete","debug","notifyadmins","forcesave","setname","findforuser","seelistas","setname","setcommand");
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<String>(commands.size()));
		default:
			return null;
    	}
			
	}
}

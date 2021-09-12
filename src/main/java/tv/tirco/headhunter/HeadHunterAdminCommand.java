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
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import tv.tirco.headhunter.config.Config;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class HeadHunterAdminCommand implements CommandExecutor,TabCompleter {
	
	String prefix = MessageHandler.getInstance().translateTags(MessageHandler.getInstance().prefix);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(args.length < 1 || args[0].equalsIgnoreCase("help")) {
			return helpCommand(sender);
    	} 
    	
  //ForceSave
    	if(args[0].equalsIgnoreCase("forcesave")) {
    		if(args.length > 1) {
    			if(args[1].equalsIgnoreCase("config") || args[1].equalsIgnoreCase("heads")) {
    	        	sender.sendMessage(prefix + " Saving config!");
    	        	Heads.getInstance().saveHeads();
    	        	return true;
    			} else if(args[1].equalsIgnoreCase("users")) {
    				sender.sendMessage(prefix + " Saving users!");
    	        	UserManager.saveAll();
    	        	return true;
    			}
    		}
        	sender.sendMessage(prefix + " Saving config!");
        	Heads.getInstance().saveHeads();
        	sender.sendMessage(prefix + " Saving users!");
        	UserManager.saveAll();
        	return true;
    	} else if(args[0].equalsIgnoreCase("purgepowerless")) {
    		int purged = HeadHunter.db.purgePowerlessUsers();
        	sender.sendMessage("Purged " + purged + " users from the database, as they had found 0 heads.");
        	return true;
    	} else if(args[0].equalsIgnoreCase("reloadconfig")) {
    		Config.getInstance().reload();
    		sender.sendMessage("Config has been reloaded!");
    		return true;
        	//PLAYER ONLY
    	} else if(args[0].equalsIgnoreCase("add")){ //toggle on/off
    		if(!(sender instanceof Player)) {
    			sender.sendMessage(prefix + " the argument add can only be used by players.");
    			sender.sendMessage(ChatColor.WHITE + "Use the command"+ChatColor.GOLD + 
    					" /hha help " + ChatColor.WHITE + "for a list of available commands.");
    			return true;
    		}
    		Player player = (Player) sender;
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
    		sender.sendMessage(prefix + " Debug has now been set to " + (state ? ChatColor.GREEN : ChatColor.RED) + state);
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
    		sender.sendMessage(prefix + " AdminNotifications has now been set to " + (state ? ChatColor.GREEN : ChatColor.RED) + state);
    		return true;
    		
    		
    		
    		
    		
    	} else if(args[0].equals("findforuser")) {
    		//hha findforuser name id
    		if(args.length < 3) {
    			sender.sendMessage(prefix + " /hha findforuser name id");
    			return true;
    		}
    		String playerName = args[1];
    		
    		
        	int id = getHeadID(args[2]);
        	if(id == -1) {
        		sender.sendMessage(prefix + " Could not parse " + args[2] + " to a number or a headname.");
        		sender.sendMessage(prefix + " Keep in mind that some heads have colorcodes.");
        		return true;
        	}
        	
        	Player target = Bukkit.getPlayerExact(playerName);
        	if(target == null) {
        		sender.sendMessage(prefix +"The specified player "+ playerName + " could not be found.");
        		return true;
        	}
        	if(!UserManager.hasPlayerDataKey(target)) {
        		sender.sendMessage(prefix +"The specified player "+ playerName + " is not currently loaded.");
        		return true;
        	}
        	PlayerData tData = UserManager.getPlayer(target);
        	tData.find(id);
        	sender.sendMessage(prefix + " The head " + args[2] + " has been unlocked for player " + playerName);
        	return true;
        	
        	
        	
        	//PLAYER ONLY
    	} else if(args[0].equals("seelistas")) {
    		if(!(sender instanceof Player)) {
    			sender.sendMessage(prefix + " the argument seelistas can only be used by players.");
    			sender.sendMessage(ChatColor.WHITE + "Use the command"+ChatColor.GOLD + 
    					" /hha help " + ChatColor.WHITE + "for a list of available commands.");
    			return true;
    		}
    		Player player = (Player) sender;
    		//hha findforuser name id
    		if(args.length < 2) {
    			sender.sendMessage(prefix + " /hha seelistas name");
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
    		
    		Boolean asIDs = false;
    		int page = 0;
    		if(args.length >= 3) {
    			if(args[2].equalsIgnoreCase("true")) {
    				asIDs = true;
    			} else {
    				try {
    					page = Integer.parseInt(args[2]);
    					
    				} catch(NumberFormatException ex) {
    					
    				}
    			}
    			if(args.length >= 4) {
    				if(args[3].equalsIgnoreCase("true")) {
    					asIDs = true;
    				} else if(page != 0) {
        				try {
        					page = Integer.parseInt(args[3]);
        					
        				} catch(NumberFormatException ex) {
        					
        				}
    				}
    			}
    		}
    		if(!asIDs && page == 0) {
    			page = 1;
    		}
        	MessageHandler.getInstance().seeList(tData, player, asIDs, page);
        	return true;
    	}
    	
    	
    	if(args.length < 2) {
    		sender.sendMessage(prefix + " Please specify the ID of the head you want to edit.");
    		return true;
    	}
    	
    	int id = getHeadID(args[1]);
    	if(id == -1) {
    		sender.sendMessage(prefix + " Could not parse " + args[1] + " to a number or a headname.");
    		sender.sendMessage(prefix + " Keep in mind that some heads have colorcodes.");
    		return true;
    	}
    	
    	if(!Heads.getInstance().headExists(id)) {
    		sender.sendMessage(prefix + " There is no head with ID " + id);
    		return true;
    	}
	
    	if(args[0].equalsIgnoreCase("find")){
    		Location loc = Heads.getInstance().getLocFromHeadId(id);
    		
    		ComponentBuilder message = new ComponentBuilder("");
    		String coords = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
    		String reply = prefix + "Head " + id + " is located at " + ChatColor.WHITE + coords + ChatColor.DARK_AQUA +" in world: " + loc.getWorld().getName();
        	if(!(sender instanceof Player)) {
        		sender.sendMessage(reply);
        		return true;
        	} else {	        	
        		Player player = (Player) sender;
        	
        		TextComponent string = new TextComponent(reply);
        		Text hint = new Text("Click for TP command. \n/gamemode spectator is recommended.");
        		string.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hint));
        		string.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tppos " + coords + " 90 0 " + loc.getWorld().getName()));
        		message.append(string);
        		player.spigot().sendMessage(message.create());
        		return true;
        	}

    	} else if(args[0].equalsIgnoreCase("edit")){
        	if(!(sender instanceof Player)) {
        		sender.sendMessage("This command can only be used by players.");
        		return true;
        	} else {	        	
        		Player player = (Player) sender;
        		sender.sendMessage(prefix + " Edit commands for: " + id);
        		MessageHandler.getInstance().sendEditCommands(player, id);
        		
        		return true;	
        	}
    		
    	} else if(args[0].equalsIgnoreCase("delete")){
    		Heads.getInstance().deleteHead(id);
    		sender.sendMessage(prefix + " Head " + id + " has been removed. Note that this might cause issues.");
    		return true;
    		
    	} else if(args[0].equalsIgnoreCase("sethint")){
    		StringBuilder sb = new StringBuilder("");
    		for (int i = 2; i < args.length; i++) {
    		    sb.append(args[i]).append(' ');
    		}
    		String hint = sb.toString().substring(0, sb.toString().length() - 1);
    		Heads.getInstance().setHint(id,hint);
    		sender.sendMessage(ChatColor.GREEN + "Head " + id + " now has its hint set as:");
    		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', hint));
    		return true;
    		
    		
    	} else if(args[0].equals("setname")) {
    		StringBuilder sb = new StringBuilder("");
    		for (int i = 2; i < args.length; i++) {
    		    sb.append(args[i]).append(' ');
    		}
    		String name = sb.toString().substring(0, sb.toString().length() - 1);
    		if(Heads.getInstance().setName(id,name,true)) {
        		sender.sendMessage(ChatColor.GREEN + "Head " + id + " now has its name set as:");
        		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', name));
    		} else {
    			sender.sendMessage(ChatColor.RED + "A head with the name " + name + " already exists.");
    		}
    		return true;
    		
    		
    	} else if(args[0].equals("addcommand")) {
    		StringBuilder sb = new StringBuilder("");
    		for (int i = 2; i < args.length; i++) {
    		    sb.append(args[i]).append(' ');
    		}
    		String command = sb.toString().substring(0, sb.toString().length() - 1);
    		Heads.getInstance().addCommand(id,command);
    		sender.sendMessage(ChatColor.GREEN + "Head " + id + " now has its command set as:");
    		sender.sendMessage(command);
    		
    		return true;
    	} else if(args[0].equals("seecommands")) {
    		if(!Heads.getInstance().hasCommand(id)) {
    			sender.sendMessage(ChatColor.RED + "The head with ID " + ChatColor.GREEN + id + ChatColor.RED + " has no commands set.");
    			return true;
    		}
    		sender.sendMessage(ChatColor.GOLD + "Commands for Head: " + ChatColor.GREEN + id);
    		for(String s : Heads.getInstance().getCommands(id)) {
    			sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.YELLOW + s);
    		}
    		return true;
    	} else if(args[0].equals("clearcommands")) {
    		StringBuilder sb = new StringBuilder("");
    		for (int i = 2; i < args.length; i++) {
    			sb.append(args[i]).append(' ');
    		}
    		String command = sb.toString().substring(0, sb.toString().length() - 1);
    		Heads.getInstance().clearCommands(id);
    		sender.sendMessage(ChatColor.GREEN + "Head " + id + " now has all commands cleared.");
    		sender.sendMessage(command);
    		return true;
    	}
    	
        return true;
    }
    
    private int getHeadID(String string) {
    	int id = -1;
    	try {
    		id = Integer.parseInt(string);
    	} catch(NumberFormatException ex) {
    		id = Heads.getInstance().getHeadIDFromName(string);
    	}
    	
		return id;
	}

	private boolean helpCommand(CommandSender sender) {
    	if(!(sender instanceof Player)) {
    		sender.sendMessage(prefix + " /hha help" + ChatColor.WHITE + " - this.");
    		sender.sendMessage(prefix + " /hha find <id>" + ChatColor.WHITE + " - get head location.");
    		sender.sendMessage(prefix + " /hha findforuser <name> <id>" + ChatColor.WHITE + " - set head as found.");
    		sender.sendMessage("");
    		sender.sendMessage(prefix + " /hha sethint <id> <msg>"+ ChatColor.WHITE + " - set hint.");
    		sender.sendMessage(prefix + " /hha setname <id> <name>"+ ChatColor.WHITE + " - set name.");
    		sender.sendMessage(prefix + " /hha setcommand <id> <cmd>"+ ChatColor.WHITE + " - set command.");
    		sender.sendMessage(prefix + " /hha delete <id>"+ ChatColor.WHITE + " - delete head.");
    		sender.sendMessage("");
    		sender.sendMessage(prefix + " /hha debug (on/off)" + ChatColor.WHITE + " - toggle debug.");
    		sender.sendMessage(prefix + " /hha notifyadmins (on/off)" + ChatColor.WHITE + " - toggle debug in chat.");
    		sender.sendMessage(prefix + " /hha forcesfave (heads/users)"+ ChatColor.WHITE + " - forcesave file");
    	} else {
    		Player player = (Player) sender;
    		
    		
    		ComponentBuilder message = new ComponentBuilder("");
    		//Title
    		TextComponent title = new TextComponent(prefix + " --- Help ---");
    		title.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
    				new Text("")));
    		message.append(title);
    		TextComponent hoverForExplanation = new TextComponent(ChatColor.GOLD + "\nHover for explanation.\n");
    		message.append(hoverForExplanation);
    		
    		//Help
    		TextComponent help = new TextComponent(ChatColor.YELLOW + "/hha help\n");
    		help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
    				new Text(ChatColor.WHITE + "Shows this screen.")));
    		message.append(help);
			
    		if(true) { //TODO permission check admin.player
    			message.append(new TextComponent("\n"));
        		TextComponent findforuser = new TextComponent(ChatColor.YELLOW + "/hha findforuser <playername> <id/name>\n");
        		findforuser.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Marks the specified head as found for the user.")));
        		findforuser.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha findforuser"));
        		message.append(findforuser);
        		
        		TextComponent seelistas = new TextComponent(ChatColor.YELLOW + "/hha seelistas <playername> (true/false)\n");
        		seelistas.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Shows you the list of found heads, as if you were them.\n" +
        										   "The boolean true/false is to determine if you want\n"
        										   + "to see head IDs (true) or names (false/unset).")));
        		seelistas.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha seelistas"));
        		message.append(seelistas);
    		}
    		//Find
    		if(true) { //TODO permission check admin.heads
    			message.append(new TextComponent("\n"));
        		TextComponent find = new TextComponent(ChatColor.YELLOW + "/hha find <id/name>\n");
        		find.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Gives you the location of the specified head\n" +
        										   "and a clickable message to teleport to it.")));
        		find.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha find"));
        		message.append(find);
        		
        		TextComponent edit = new TextComponent(ChatColor.YELLOW + "/hha edit <id>\n");
        		edit.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Gives you clickable messages so you\n" +
        										   "can quickly edit head information.")));
        		edit.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha edit <id>"));
        		message.append(edit);
        		
        		TextComponent add = new TextComponent(ChatColor.YELLOW + "/hha add (on/off)\n");
        		add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Toggles addmode. Any head you place will be added\n" +
        										   "to the hunt. (on = Enabled).")));
        		add.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha add on"));
        		message.append(add);
        		
        		TextComponent sethint = new TextComponent(ChatColor.YELLOW + "/hha sethint <id/name> <msg>\n");
        		sethint.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Sets the hint of the specified head to be your msg.\n" +
        										   "Hints are seen when a player uses /hh list.")));
        		sethint.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha sethint"));
        		message.append(sethint);
        		
        		TextComponent setname = new TextComponent(ChatColor.YELLOW + "/hha setname <id/name> <msg>\n");
        		setname.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Sets the name of the specified head to be your msg.\n" +
        										   "Names are an alternate way of identifying heads.")));
        		setname.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha setname"));
        		message.append(setname);
        		
        		TextComponent setcommand = new TextComponent(ChatColor.YELLOW + "/hha setcommand <id/name> <msg>\n");
        		setcommand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Sets the command of the specified head to be your msg.\n" +
        										   "The command is run when a player finds the head.\n"
        										   + "You can use the placeholders:\n"
        										   + "<player>, <id>, <found>.\n"
        										   + "Do not include a / at the start of your command.\n"
        										   + "Leave empty to remove command.")));
        		setcommand.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha setcommand"));
        		message.append(setcommand);
        		
        		TextComponent delete = new TextComponent(ChatColor.YELLOW + "/hha delete <id/name>\n");
        		delete.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Deletes the head.\n" +
        										   "WARNING: Not recommended.")));
        		delete.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha delete"));
        		message.append(delete);
    		}
    		
    		if(true) { //TODO permission admin.log
    			message.append(new TextComponent("\n"));
        		TextComponent debug = new TextComponent(ChatColor.YELLOW + "/hha debug (on/off)\n");
        		debug.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Enables/disables debugmessages to the console.\n" +
        										   "Will be reset upon next reload/reboot")));
        		debug.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha debug on"));
        		message.append(debug);
        		
        		TextComponent notifyadmins = new TextComponent(ChatColor.YELLOW + "/hha notifyadmins (on/off)\n");
        		notifyadmins.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Enables/disables debugmessages to all admins in chat.\n" +
        										   "Will be reset upon next reload/reboot")));
        		notifyadmins.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha notifyadmins on"));
        		message.append(notifyadmins);
        		
        		TextComponent forcesave = new TextComponent(ChatColor.YELLOW + "/hha forcesave (heads/users)\n");
        		forcesave.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Saves the specified file.\n" +
        										   "If no file is specified, both are saved.")));
        		forcesave.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha forcesave"));
        		message.append(forcesave);
        		
        		TextComponent purgepowerless = new TextComponent(ChatColor.YELLOW + "/hha purgepowerless\n");
        		purgepowerless.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new Text(ChatColor.WHITE + "Clears all users from the file, that has not\n" +
        										   "found any heads. Warning: Can lagg a bit!")));
        		purgepowerless.setClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/hha purgepowerless"));
        		message.append(purgepowerless);
    		}
    		
    		message.append(title);
			//Send the message
			player.spigot().sendMessage(message.create());
			
    	}
    	return true;
    }
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> commands = ImmutableList.of("help","add","find","sethint","delete","debug","notifyadmins","forcesave","setname","findforuser","seelistas","setname","addcommand","clearcommands","seecommands","purgepowerless","reloadconfig");
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<String>(commands.size()));
		default:
			return null;
    	}
			
	}
}

package tv.tirco.headhunter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

import tv.tirco.headhunter.config.Messages;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class HeadHunterCommand implements CommandExecutor,TabCompleter {
	
	String prefix = MessageHandler.getInstance().translateTags(MessageHandler.getInstance().prefix);
	List<String> commands = ImmutableList.of("count","list","hint","help","top");
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(!(sender instanceof Player)) {
    		sender.sendMessage(prefix + " " + Messages.getInstance().getMessage("this-command-can-only-be-used-by-players"));
    		return true;
    	}
    	//ARGS - LIST, COUNT, TOP, HELP, HINT
    	
    	Player player = (Player) sender;
    	
    	if(!UserManager.hasPlayerDataKey(player)) {
    		player.sendMessage(Messages.getInstance().getMessage("error-please-wait-until-load"));
    		return true;
    	}

    	if(args.length < 1) {
			String s = MessageHandler.getInstance().translateTags(Messages.getInstance().getMessageCountCommand(), player);
  			player.sendMessage(Messages.getInstance().getMessage("help-list"));
  			player.sendMessage(Messages.getInstance().getMessage("help-count"));
  			player.sendMessage(Messages.getInstance().getMessage("help-hint"));
  			player.sendMessage(Messages.getInstance().getMessage("help-top"));
			player.sendMessage(s);
        	return true;
    	} else {
    		if(args[0].equalsIgnoreCase("count")) {
    			String s = MessageHandler.getInstance().translateTags(Messages.getInstance().getMessageCountCommand(), player);
    			player.sendMessage(s);
    		} else if(args[0].equalsIgnoreCase("hint")) {
    			
    			int id;
    			
    			if(args.length < 2) {
    				PlayerData pData = UserManager.getPlayer(player);
    				List<Integer> notFound = pData.getNotFound();
    				if(notFound.isEmpty()) {
    					sender.sendMessage(prefix + " " + Messages.getInstance().getMessage("error-no-hint-found-all"));
    					return true;
    				}
    				Random rand = new Random();
    				id = notFound.get(rand.nextInt(notFound.size()));
    			} else {
        			id = getHeadID(args[1]);
                	if(id == -1) {
                		sender.sendMessage(prefix + String.format(Messages.getInstance().getMessage("error-could-not-parse"),args[1]));
                		return true;
                	}
    			}
    			MessageHandler.getInstance().sendHintMessage(player, id);

    			
    		} else if(args[0].equalsIgnoreCase("list")) {
    			if(!UserManager.hasPlayerDataKey(player)){
    				player.sendMessage(Messages.getInstance().getMessage("error-unable-to-do-that-now"));
    				return true;
    			}
    			
    			PlayerData pData = UserManager.getPlayer(player);
    			player.sendMessage(Messages.getInstance().getMessage("list-header"));
        		Boolean asIDs = false;
        		int page = 0;
        		
        		//Parse if arg 2 or 3 is either "true" or a number.
        		if(args.length >= 2) {
        			if(args[1].equalsIgnoreCase("true")) {
        				asIDs = true;
        			} else {
        				try {
        					page = Integer.parseInt(args[1]);
        					
        				} catch(NumberFormatException ex) {
        					
        				}
        			}
        			if(args.length >= 3) {
        				if(args[2].equalsIgnoreCase("true")) {
        					asIDs = true;
        				} else if(page != 0) {
            				try {
            					page = Integer.parseInt(args[2]);
            					
            				} catch(NumberFormatException ex) {
            					
            				}
        				}
        			}
        		}
        		if(!asIDs && page == 0) {
        			page = 1;
        		}
            	MessageHandler.getInstance().seeList(pData, player, asIDs, page);

    			return true;

    		} else if(args[0].equalsIgnoreCase("top")) {
    			for(String s : Heads.getInstance().getTopPlayerList()) {
    				sender.sendMessage(s);
    			}
    			return true;
    		} else { //Help command or wrong command.
    			player.sendMessage(prefix + Messages.getInstance().getMessage("help"));
      			player.sendMessage(Messages.getInstance().getMessage("help-list"));
      			player.sendMessage(Messages.getInstance().getMessage("help-count"));
      			player.sendMessage(Messages.getInstance().getMessage("help-hint"));
      			player.sendMessage(Messages.getInstance().getMessage("help-top"));
    			return true;
    		}
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
    

    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		switch (args.length) {
		case 1:
			return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<String>(commands.size()));
		case 2:
			if(args[0].equalsIgnoreCase("hint")) {
				return StringUtil.copyPartialMatches(args[1], Heads.getInstance().getHeadNames(), new ArrayList<String>(Heads.getInstance().getHeadNames().size()));
			} else {
				return null;
			}
		default:
			return null;
    	}
			
	}
}

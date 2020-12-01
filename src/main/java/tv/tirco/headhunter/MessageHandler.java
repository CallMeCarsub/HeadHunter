package tv.tirco.headhunter;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class MessageHandler {

	private static MessageHandler instance;
	public String prefix = "HeadHunter";
	private int headsPrPage = 16;
	
	private boolean debug = false;
	private boolean debugToAdmins = false;
	
	public void updatePrefix(String prefix) {
		this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
	}
	
	public static MessageHandler getInstance() {
		if (instance == null) {
			instance = new MessageHandler();
		}
		return instance;
	}
	
	/*
	 * Debug messages are only sendt if enabled in config.
	 */
	public void debug(String msg) {
		if(debug) {
			Bukkit.getLogger().log(Level.INFO, prefix + msg);
		}
		if(debugToAdmins) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.hasPermission("headhunter.admin")) {
					p.sendMessage(prefix + msg);
				}
			}
		}
		
	}
	
	public String translateTags(String s, Player p) {
		String foundSkulls = "?";
		if(!UserManager.hasPlayerDataKey(p)) {

		} else {
			PlayerData pData = UserManager.getPlayer(p);
			foundSkulls = ""+pData.getAmountFound();
		}
		
		
		//<found>,<playername> 
		s = s.replace("<playername>", p.getName());
		s = s.replace("<found>", foundSkulls);
		return translateTags(s);
	}
	
	public String translateTags(String s) {
		//<max>
		s = s.replace("<max>", "" + Heads.getInstance().getHeadAmount());
		s = ChatColor.translateAlternateColorCodes('&', s);
		return s;
	}

	/*
	 * Log messages are always sendt to console.
	 */
	public void log(String msg) {
		Bukkit.getLogger().log(Level.INFO, prefix + msg);
		if(debugToAdmins) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.hasPermission("headhunter.admin")) {
					p.sendMessage(prefix + msg);
				}
			}
		}
	}

	
	public void setDebugState(boolean b) {
		this.debug = b;
	}
	
	public void setDebugToAdminState(boolean b) {
		this.debugToAdmins = b;
	}
	
	public boolean getDebugState() {
		return this.debug;
	}
	
	public boolean getDebugToAdminState() {
		return this.debugToAdmins;
	}

	
	/**
	 * Displays a list of found heads, with hints to a specified player.
	 * @param pData The PlayerData to see the information from.
	 * @param player The player that should see the information.
	 * @param page 
	 */
    public void seeList(PlayerData pData, Player player, Boolean asIDs, int page) {
		ComponentBuilder message = new ComponentBuilder("");
		player.sendMessage(ChatColor.GOLD + "(" + ChatColor.GREEN + " Found. " + ChatColor.GOLD + "/"+ ChatColor.RED + " Not Found. " + ChatColor.GOLD + ")");
		
		if(asIDs) {
			for(int i : Heads.getInstance().getHeads().keySet() ) {
				String c = (pData.hasFound(i) ? c = (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : (ChatColor.RED + ""));
				
				String name = c + "[" + i + "] ";

				
				TextComponent string = new TextComponent(name);
				Text hint = new Text(ChatColor.translateAlternateColorCodes('&', Heads.getInstance().getHint(i)));
				string.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hint));
				message.append(string);

			}
		} else {
			int wordCount = 0;
			//lines pr page = 4 - Total of 16 heads / page.
			//Page will always be 1 or higher.
			if(page < 1) {
				page = 1;
			}
			
			int max = headsPrPage*page;
			int min = max - headsPrPage;
			int current = 0;
			for(int i : Heads.getInstance().getHeads().keySet() ) {
				if(current < min) {
					current++;
					continue;
				} else if(current >= max) {
					break;
				}
				current ++;
				
				String c = (pData.hasFound(i) ? c = (ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH) : (ChatColor.RED + "" ));
				
				String name = "";
				if(Heads.getInstance().hasName(i)) {
					if(pData.hasFound(i)) {
						name = c + "[" +ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Heads.getInstance().getName(i))) + "]"+ ChatColor.RESET + " ";
						
						
					} else {
						name = c + "[" +ChatColor.translateAlternateColorCodes('&', Heads.getInstance().getName(i) + c + "] ");
					}
					
				} else {
					name = c + "[" + i + "]" + ChatColor.RESET + " ";
				}
				
				wordCount ++;
				if(wordCount >= 4) {
					name += "\n";
					wordCount = 0;
				}
				
				TextComponent string = new TextComponent(name);
				Text hint = new Text(ChatColor.translateAlternateColorCodes('&', Heads.getInstance().getHint(i)));
				string.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hint));
				message.append(string);

			}
			
		}

		player.spigot().sendMessage(message.create());
		player.sendMessage(ChatColor.GOLD + "" +  ChatColor.ITALIC + "Hover a number to see its hint.");
		if(page != 0 && !asIDs) {
			TextComponent dash = new TextComponent(ChatColor.GOLD + " --- ");
			TextComponent prev = new TextComponent(" <<< ");
			TextComponent pageNumber = new TextComponent(ChatColor.AQUA +" Page " + page);
			TextComponent next = new TextComponent(" >>> ");
			
			//Prev button
			String command = "";
			String targetName = pData.getPlayer().getName();
			if(player.getName().equalsIgnoreCase(targetName)) {
				command = "/hh list ";
			} else {
				command = "/hha seelistas " + targetName + " ";
			}
			if(page > 1) {
				prev.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + (page-1)) );
				prev.setColor(ChatColor.GOLD);
			} else {
				prev.setColor(ChatColor.DARK_GRAY);
			}
			
			//Next button
			if((page) * headsPrPage < Heads.getInstance().getHeadAmount()) {
				next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + (page+1)) );
				next.setColor(ChatColor.GOLD);
			} else {
				next.setColor(ChatColor.DARK_GRAY);
			}
			
			player.spigot().sendMessage(new ComponentBuilder("").append(dash).append(prev).append(pageNumber).append(next).append(dash).create());
		}
		
    }



}

package tv.tirco.headhunter;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class MessageHandler {

	private static MessageHandler instance;
	public String prefix = "HeadHunter";
	
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

    
	public void seeList(PlayerData pData, Player player) {
		seeList(pData, player, false);
	}
	
	/**
	 * Displays a list of found heads, with hints to a specified player.
	 * @param pData The PlayerData to see the information from.
	 * @param player The player that should see the information.
	 */
    public void seeList(PlayerData pData, Player player, Boolean asIDs) {
		ComponentBuilder message = new ComponentBuilder("");
		player.sendMessage(ChatColor.GOLD + "(" + ChatColor.GREEN + " Found. " + ChatColor.GOLD + "/"+ ChatColor.RED + " Not Found. " + ChatColor.GOLD + ")");
		
		for(int i : Heads.getInstance().getHeads().keySet() ) {
			ChatColor c = (pData.hasFound(i) ? c = ChatColor.GREEN : ChatColor.RED);
			
			String name = "";
			if(!asIDs && Heads.getInstance().hasName(i)) {
				//Grab custom name
				name = c + ChatColor.translateAlternateColorCodes('&', Heads.getInstance().getName(i));
			} else {
				//Get ID as name.
				name = c + "" + i;
			}
			
			TextComponent string = new TextComponent(name);
			Text hint = new Text(ChatColor.translateAlternateColorCodes('&', Heads.getInstance().getHint(i)));
			string.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hint));
			message.append(string);

		}
		player.spigot().sendMessage(message.create());
		player.sendMessage(ChatColor.GOLD + "" +  ChatColor.ITALIC + "Hover a number to see its hint.");
    }



}

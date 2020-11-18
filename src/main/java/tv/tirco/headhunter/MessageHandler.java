package tv.tirco.headhunter;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
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



}

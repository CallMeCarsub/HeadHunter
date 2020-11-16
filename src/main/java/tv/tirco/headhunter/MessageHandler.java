package tv.tirco.headhunter;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class MessageHandler {

	private static MessageHandler instance;
	public String prefix = "&3[&bHeadHunter&3]";
	
	private static boolean debug = false;
	private static boolean debugToAdmins = false;
	
	public static MessageHandler getInstance() {
		if (instance == null) {
			instance = new MessageHandler();
		}
		return instance;
	}
	
	public void debug(String msg) {
		if(debug) {
			Bukkit.getLogger().log(Level.INFO, msg);
		}
		
	}
	
	/*public static void debugs(String msg) {
		if(debug) {
			Bukkit.getLogger().log(Level.INFO, msg);
		}
		
	}*/
	
	public String translateTags(String s, Player p) {
		String foundSkulls = "?";
		if(!UserManager.hasPlayerDataKey(p)) {

		} else {
			PlayerData pData = UserManager.getPlayer(p);
			foundSkulls = ""+pData.getAmountFound();
		}
		
		
		//<found>,<playername> 
		s = ChatColor.translateAlternateColorCodes('&', s);
		s = s.replace("<playername>", p.getName());
		s = s.replace("<found>", foundSkulls);
		return translateTags(s);
	}
	
	public String translateTags(String s) {
		//<max>
		s = s.replace("<max>", "" + Heads.getInstance().getHeadAmount());
		return s;
	}

	public static void log(String string) {
		
		
	}

	
	public void setDebugState(boolean b) {
		this.debug = b;
	}
	
	public void setDebugToAdminState(boolean b) {
		this.debugToAdmins = b;
	}



}

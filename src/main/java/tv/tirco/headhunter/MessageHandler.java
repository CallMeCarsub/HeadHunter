package tv.tirco.headhunter;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class MessageHandler {

	private static MessageHandler instance;
	public String prefix = "&3[&bHeadHunter&3]";
	
	public static MessageHandler getInstance() {
		if (instance == null) {
			instance = new MessageHandler();
		}
		return instance;
	}
	
	public void debug(String msg) {
		if(Config.debug) {
			Bukkit.getLogger().log(Level.INFO, msg);
		}
		
	}
	
	public String translateTags(String s, Player p) {
		//<found>,<playername> 
		s = ChatColor.translateAlternateColorCodes('&', s);
		s = s.replace("<playername>", p.getName());
		s = s.replace("<found>", "Temp: pData.getFoundSkulls");
		return translateTags(s);
	}
	
	public String translateTags(String s) {
		//<max>
		s = s.replace("<max>", "" + Heads.getInstance().getHeadAmount());
		return s;
	}

}

package tv.tirco.headhunter.config;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.headhunter.MessageHandler;

public class Messages extends AutoUpdateConfigLoader {
	private static Messages instance;

	private Messages() {
		super("messages.yml");
		validate();
	}

	public static Messages getInstance() {
		if (instance == null) {
			instance = new Messages();
		}

		return instance;
	}
	
	public void reload() {
		instance = new Messages();
	}

	@Override
	protected void loadKeys() {

	}

	@Override
	protected boolean validateKeys() {
		// Validate all the settings!
		List<String> reason = new ArrayList<String>();
		return noErrorsInConfig(reason);
	}

	@SuppressWarnings("unused")
	private String getStringIncludingInts(String key) {
		String str = config.getString(key);

		if (str == null) {
			str = String.valueOf(config.getInt(key));
		}

		if (str.equals("0")) {
			str = "No value set for '" + key + "'";
		}
		return str;
	}

	
	/* MESSAGES */
	public String getMessagePrefix() {
		return config.getString("messages.prefix", "&3[&bHeadHunter&3] ");
	}
	
	public String getMessageAnnounceFindAll() {
		return config.getString("messages.announcefindingallmessage", "&6<playername>&f has found all &c<max>&f heads!");
	}
	
	public String getMessageCount() {
		return config.getString("messages.countmessage", "&aYou have found &6<found>&a out of &6<max>&a heads. This head had ID <idfound>.");
	}
	
	public String getMessageAlreadyFound() {
		return config.getString("messages.repeatmessage", "&aYou have already found this skull.");
	}

	


	public String getMessageCountCommand() {
		return config.getString("messages.countcommandmessage", "&aYou have found &6<found>&a out of &6<max>&a heads.");
	}

	public String getMessage(String key) {
		String message = config.getString("messages."+key,"&cMissing Message");
		if(message.equals("&cMissing Message")) {
			MessageHandler.getInstance().log(ChatColor.RED + "Error - Tried to pull language key " + ChatColor.YELLOW + key + ChatColor.RED + " but it returned a missing string!");
			MessageHandler.getInstance().log(ChatColor.RED + "Add it to your messages.yml - If this is from the default messages.yml, please report this on the spigot page!");
		}
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	public String getUncoloredMessage(String key) {
		String message = config.getString("messages."+key,"&cMissing Message");
		if(message.equals("&cMissing Message")) {
			MessageHandler.getInstance().log(ChatColor.RED + "Error - Tried to pull language key " + ChatColor.YELLOW + key + ChatColor.RED + " but it returned a missing string!");
			MessageHandler.getInstance().log(ChatColor.RED + "Add it to your messages.yml - If this is from the default messages.yml, please report this on the spigot page!");
		}
		return config.getString(message);
	}
}

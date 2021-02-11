package tv.tirco.headhunter.config;

import java.util.ArrayList;
import java.util.List;

import tv.tirco.headhunter.MessageHandler;

public class Config extends AutoUpdateConfigLoader {
	private static Config instance;

	private Config() {
		super("config.yml");
		validate();
	}

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}

		return instance;
	}

	@Override
	protected void loadKeys() {

	}

	@Override
	protected boolean validateKeys() {
		// Validate all the settings!
		List<String> reason = new ArrayList<String>();

		if (getDebug()) {
			MessageHandler.getInstance().setDebugState(true);
			MessageHandler.getInstance().debug("Debugging has been enabled.");
		}
		if (getDebugToAdmins()) {
			MessageHandler.getInstance().setDebugState(true);
			MessageHandler.getInstance().debug("Debug loggint to admins has been enabled.");
		}

		// If the reason list is empty, keys are valid.
			MessageHandler.getInstance().updatePrefix(getMessagePrefix());
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

	// Config Getters
	/* General Settings */
	public String getLocale() {
		return config.getString("General.Locale", "en_us");
	}

	public boolean getDebug() {
		return config.getBoolean("setting.debug", false);
	}
	
	public boolean getDebugToAdmins() {
		return config.getBoolean("setting.debugtoadmins", false);
	}

	public int getSaveInterval() {
		return config.getInt("setting.autosaveinterval", 15);
	}
	
	public boolean getUseParticles() {
		return config.getBoolean("setting.particles", true);
	}
	
	public boolean runCommandOnHeadFound() {
		return config.getBoolean("setting.commandOnHeadFound", true);
	}

	/* Database Purging */
	public int getOldUsersCutoff() { //How long in months we should wait before we remove a user.
		return config.getInt("setting.purgeafter", 0);
	}
	
	public boolean onlyPurgeIfPowerless() {
		return config.getBoolean("setting.onlypurgeemptyplayers", true);
	}
	
	public boolean getAnnounceFindAll() {
		return config.getBoolean("setting.announcefindingall", true);
	}
	
	/* MESSAGES */
	public String getMessagePrefix() {
		return config.getString("messages.prefix", "&3[&bHeadHunter&3] ");
	}
	
	public String getMessageAnnounceFindAll() {
		return config.getString("messages.announcefindingallmessage:", "&6<playername>&f has found all &c<max>&f heads!");
	}
	
	public String getMessageCount() {
		return config.getString("messages.countmessage", "&aYou have found &6<found>&a out of &6<max>&a heads. This head had ID <idfound>.");
	}
	
	public String getMessageAlreadyFound() {
		return config.getString("messages.repeatmessage", "&aYou have already found this skull.");
	}

	
	public boolean getNeedPermToHunt() {
		return config.getBoolean("setting.huntingrequiresperm",false);
	}

	
	public int getTopAmount() {
		return config.getInt("setting.topamountsaved", 10);
	}

	public String getMessageCountCommand() {
		return config.getString("messages.countcommandmessage", "&aYou have found &6<found>&a out of &6<max>&a heads.");
	}
}

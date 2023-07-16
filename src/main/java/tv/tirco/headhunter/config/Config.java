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
	
	public void reload() {
		instance = new Config();
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
			MessageHandler.getInstance().debug(Messages.getInstance().getMessage("debugging-enabled"));
		}
		if (getDebugToAdmins()) {
			MessageHandler.getInstance().setDebugState(true);
			MessageHandler.getInstance().debug(Messages.getInstance().getMessage("debugging-to-admins-enabled"));
		}

		// If the reason list is empty, keys are valid.
			MessageHandler.getInstance().updatePrefix(Messages.getInstance().getMessagePrefix());
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
	
	public boolean getNeedPermToHunt() {
		return config.getBoolean("setting.huntingrequiresperm",false);
	}

	
	public int getTopAmount() {
		return config.getInt("setting.topamountsaved", 10);
	}
	
	
	public List<String> getRewardCommands(int amount) {
		if(config.isSet("ExtraRewards."+amount)) {
			return config.getStringList("ExtraRewards."+amount);
		} else {
			return new ArrayList<String>();
		}
	}
	public List<String> getMaxRewardCommands() {
		if(config.isSet("ExtraRewards.ALL")) {
			return config.getStringList("ExtraRewards.ALL");
		} else {
			return new ArrayList<String>();
		}
	}
}

package tv.tirco.headhunter;

import java.io.File;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import tv.tirco.headhunter.database.DatabaseManager;
import tv.tirco.headhunter.listeners.PlayerClickBlock;
import tv.tirco.headhunter.listeners.PlayerPlaceHead;


public class HeadHunter extends JavaPlugin {
	
	public static Plugin plugin;
	public static String playerDataKey;
	public static DatabaseManager db;
	

	
    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    }

    @Override
    public void onEnable() {
        // Don't log enabling, Spigot does that for you automatically!
    	setupFilePaths();

        // Commands enabled with following method must have entries in plugin.yml
        getCommand("HeadHunter").setExecutor(new HeadHunterCommand(this));
        registerListeners();
        
        setupInstances();
        
        loadConfig();
    }

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerPlaceHead(), this);
		getServer().getPluginManager().registerEvents(new PlayerClickBlock(), this);
		
	}

	private void setupInstances() {
		MessageHandler.getInstance();
		Heads.getInstance();
		
	}
	
	private void loadConfig() {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	// File Manager setup bulk
	File recklessFile;
	public boolean noErrorsInConfigFiles;
	static String mainDirectory;
	static String userFileDirectory;
	static String usersFile;
	
	public static String getMainDirectory() {
		return mainDirectory;
	}

	public static String getFlatFileDirectory() {
		return userFileDirectory;
	}

	public static String getUsersFilePath() {
		return usersFile;
	}
	
	private void setupFilePaths() {
		recklessFile = getFile();
		mainDirectory = getDataFolder().getPath() + File.separator;
		userFileDirectory = mainDirectory + "userFiles" + File.separator;
		usersFile = userFileDirectory + "HeadHunter.Users";
		fixFilePaths();
	}
	
	private void fixFilePaths() {
		File currentFlatfilePath = new File(userFileDirectory);
		currentFlatfilePath.mkdirs();
	}
}

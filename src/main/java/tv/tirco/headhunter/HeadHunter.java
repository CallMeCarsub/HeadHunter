package tv.tirco.headhunter;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Charsets;

import tv.tirco.headhunter.database.DatabaseManager;
import tv.tirco.headhunter.database.DatabaseManagerFactory;
import tv.tirco.headhunter.listeners.PlayerClickBlock;
import tv.tirco.headhunter.listeners.PlayerJoinListener;
import tv.tirco.headhunter.listeners.PlayerPlaceHead;


public class HeadHunter extends JavaPlugin {
	
	public static Plugin plugin;
	public static HeadHunter headHunter;
	public final static String playerDataKey = "HeadHunter: Tracked";
	public static DatabaseManager db;
	

	
    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    }

    @Override
    public void onEnable() {
    	plugin = this;
    	headHunter = this;
        // Don't log enabling, Spigot does that for you automatically!
    	setupFilePaths();

        // Commands enabled with following method must have entries in plugin.yml
        getCommand("HeadHunter").setExecutor(new HeadHunterCommand());
        registerListeners();
        
        setupInstances();
        
        loadConfig();
        
        db = DatabaseManagerFactory.getDatabaseManager();
    }

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerPlaceHead(), this);
		getServer().getPluginManager().registerEvents(new PlayerClickBlock(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		
	}

	private void setupInstances() {
		MessageHandler.getInstance();
		Heads.getInstance();
		
	}
	
	private void loadConfig() {
		// TODO Auto-generated method stub
		Config.getInstance();
		
	}
	
	public InputStreamReader getResourceAsReader(String fileName) {
		InputStream in = getResource(fileName);
		return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
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

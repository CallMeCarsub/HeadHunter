package tv.tirco.headhunter;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Charsets;

import tv.tirco.headhunter.config.Config;
import tv.tirco.headhunter.database.DatabaseManager;
import tv.tirco.headhunter.database.DatabaseManagerFactory;
import tv.tirco.headhunter.database.SaveTimerTask;
import tv.tirco.headhunter.database.UserManager;
import tv.tirco.headhunter.listeners.PlayerBreakBlock;
import tv.tirco.headhunter.listeners.PlayerClickBlock;
import tv.tirco.headhunter.listeners.PlayerJoinListener;
import tv.tirco.headhunter.listeners.PlayerPlaceHead;
import tv.tirco.headhunter.papi.PapiExpansion;


public class HeadHunter extends JavaPlugin {
	
	public static Plugin plugin;
	public static HeadHunter headHunter;
	public final static String playerDataKey = "HeadHunter: Tracked";
	public static DatabaseManager db;
	
	public PapiExpansion placeholders;
	public boolean papi = false;
	

	
    @Override
    public void onDisable() {
    	try {
        	UserManager.saveAll();
        	UserManager.clearAll(); //Removes tracking of everyone.
        	Heads.getInstance().saveHeads();
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	
        MessageHandler.getInstance().log("Canceling all tasks...");
        getServer().getScheduler().cancelTasks(this); // This removes our tasks
        MessageHandler.getInstance().log("Unregister all events...");
        HandlerList.unregisterAll(this); // Cancel event registrations

        
        MessageHandler.getInstance().log("Has been disabled.");
    }

    @Override
    public void onEnable() {
    	plugin = this;
    	headHunter = this;
        // Don't log enabling, Spigot does that for you automatically!
    	setupFilePaths();
        loadConfig();

        // Commands enabled with following method must have entries in plugin.yml
        getCommand("HeadHunter").setExecutor(new HeadHunterCommand());
        getCommand("HeadHunterAdmin").setExecutor(new HeadHunterAdminCommand());
        registerListeners();
        
        setupInstances();
        

        
        db = DatabaseManagerFactory.getDatabaseManager();
        
        scheduleTasks();
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			placeholders = new PapiExpansion(this);
            placeholders.register();
            this.papi = true;
		}
        
        db.purgeOldUsers();
        
        //Load our heads after worlds and all are loaded.
        new BukkitRunnable() {
            
            @Override
            public void run() {
            	Heads.getInstance().loadFromFile();
            }
            
        }.runTaskLater(this, 20);



    }

	private void scheduleTasks() {
		if(Config.getInstance().getUseParticles()) {
			new ParticleRunnable().runTaskTimerAsynchronously(this, 60, 60);
		}
		
		long saveInterval = Config.getInstance().getSaveInterval();
		if(saveInterval != 0) {
			long saveIntervalTicks = saveInterval * 1200; //1200 = 1 minute
			new SaveTimerTask().runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);
		}

	}

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerPlaceHead(), this);
		getServer().getPluginManager().registerEvents(new PlayerClickBlock(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerBreakBlock(), this);
		
	}

	private void setupInstances() {
		MessageHandler.getInstance();
		
	}
	
	private void loadConfig() {
		Config.getInstance();
	}

	// File Manager setup bulk
	File mainFile;
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
		mainFile = getFile();
		mainDirectory = getDataFolder().getPath() + File.separator;
		userFileDirectory = mainDirectory + "userFiles" + File.separator;
		usersFile = userFileDirectory + "HeadHunter.Users";
		fixFilePaths();
	}
	
	private void fixFilePaths() {
		File currentFlatfilePath = new File(userFileDirectory);
		currentFlatfilePath.mkdirs();
	}
	
	public InputStreamReader getResourceAsReader(String fileName) {
		InputStream in = getResource(fileName);
		return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
	}
	
	
}

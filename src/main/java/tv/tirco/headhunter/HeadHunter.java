package tv.tirco.headhunter;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import tv.tirco.headhunter.listeners.PlayerClickBlock;
import tv.tirco.headhunter.listeners.PlayerPlaceHead;


public class HeadHunter extends JavaPlugin {
    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    }

    @Override
    public void onEnable() {
        // Don't log enabling, Spigot does that for you automatically!

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
}

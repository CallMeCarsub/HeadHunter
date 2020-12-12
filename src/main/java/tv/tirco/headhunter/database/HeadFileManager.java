package tv.tirco.headhunter.database;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.google.common.collect.BiMap;
import net.md_5.bungee.api.ChatColor;
import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.Heads;
import tv.tirco.headhunter.MessageHandler;

public class HeadFileManager {
	
	protected static final HeadHunter plugin = HeadHunter.headHunter;
	static File directory = plugin.getDataFolder(); //Probably a better way to do this?
	static String filename = "Heads.yml";
	static FileConfiguration yamlFile;
	
	
	
	public static void saveHeads() {
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		//setup variables.
		File f = new File(directory,filename);
		yamlFile = new YamlConfiguration();
		
		BiMap<Integer, Location> heads = Heads.getInstance().getHeads();
		//HashMap<Integer,String> headHints = Heads.getInstance().getHeadsHints();
		
		for(Integer i : heads.keySet()) {
			yamlFile.set("heads.id" + i + ".x", heads.get(i).getX());
			yamlFile.set("heads.id" + i + ".y", heads.get(i).getY());
			yamlFile.set("heads.id" + i + ".z", heads.get(i).getZ());
			yamlFile.set("heads.id" + i + ".world", heads.get(i).getWorld().getName());
			yamlFile.set("heads.id" + i + ".hint", Heads.getInstance().getHint(i, true));
			yamlFile.set("heads.id" + i + ".command", Heads.getInstance().getCommand(i));
			yamlFile.set("heads.id" + i + ".name", Heads.getInstance().getName(i));
		}
		
		MessageHandler.getInstance().debug(ChatColor.GOLD + " Saving topScores...");
		LinkedHashMap<UUID,Integer> topScores = Heads.getInstance().getSortedTopMap();
		MessageHandler.getInstance().debug(ChatColor.GOLD + " Retreived scores from heads.java. Looping:");
		for(UUID uuid : topScores.keySet()) {
			//MessageHandler.getInstance().debug(ChatColor.GOLD + " UUID is " + uuid);
			yamlFile.set("scores." + uuid, topScores.get(uuid));
			//MessageHandler.getInstance().debug(ChatColor.GOLD + " Set path scores."+uuid + " to " + topScores.get(uuid));
		}
		MessageHandler.getInstance().debug(ChatColor.GOLD + " Loop complete.");

		try {
			yamlFile.save(f);
		} catch (IOException e) {
			//Failed to save file?
			MessageHandler.getInstance().log("Error while saving Heads.");
			e.printStackTrace();
		}
		MessageHandler.getInstance().debug("All Heads have been saved to file.");
		
	}
	
	public static void loadHeads() {
		if(!directory.exists()) {
			directory.mkdir();
		}
		
		MessageHandler.getInstance().debug(ChatColor.GOLD + "Loading Heads...");

		File f = new File(directory,filename);
		yamlFile = new YamlConfiguration();
		
		//HashBiMap<Integer, Location> heads = HashBiMap.create();
		//HashMap<Integer,String> headHints = Heads.getInstance().getHeadsHints();
		
		if(!f.exists()) {
			MessageHandler.getInstance().log(ChatColor.GOLD + "Heads did not exist! Creating new file");
			return;
		}
		//Loop through all files and make inventories out of them.
			yamlFile = new YamlConfiguration();
	         try
	          {
	              yamlFile.load(f);
	          } catch (IOException e){
	        	  MessageHandler.getInstance().debug(ChatColor.RED + "Error" + ChatColor.WHITE + " while parsing file " + f.getName() + " (IOException)");
	              e.printStackTrace();
	          } catch (InvalidConfigurationException e) {
	        	  MessageHandler.getInstance().debug(ChatColor.RED + "Error" + ChatColor.WHITE + " while parsing file " + f.getName() + " (InvalidConfigurationException)");
	              e.printStackTrace();
	          }
	          
	         if(yamlFile.contains("heads")){
	        	 MessageHandler.getInstance().log(ChatColor.GOLD + " Parsing through heads...");
	        	 for(String key : yamlFile.getConfigurationSection("heads").getKeys(false)) {
	        		 try {
	        			 
	        			 Integer id = Integer.parseInt(key.substring(2));
	        			 Double X = yamlFile.getDouble("heads." +key+".x");
		        		 Double Y = yamlFile.getDouble("heads." +key+".y");
		        		 Double Z = yamlFile.getDouble("heads." +key+".z");
		        		 String world = yamlFile.getString("heads." +key+".world");
		        		 String hint = yamlFile.getString("heads." +key+".hint");
		        		 World worldObject = Bukkit.getWorld(world);
		        		 if(worldObject == null) {
		        			 MessageHandler.getInstance().log("Warning: Could not find the world" + world + " defaulting to overworld.");
		        			 worldObject = Bukkit.getWorlds().get(0);
		        		 }
		        		 Location loc = new Location(worldObject, X, Y, Z);
		        		 Heads.getInstance().addHead(id,loc,true);
		        		 Heads.getInstance().setHint(id, hint);
		        		 
		        		 //Name
		        		 String name = yamlFile.getString("heads."+ key +".name");
		        		 if(name != null && !name.isEmpty()) {
		        			 Heads.getInstance().setName(id, name, false);
		        		 }
		        		 
		        		 //Command
		        		 String command = yamlFile.getString("heads."+ key +".command");
		        		 if(command != null && !command.isEmpty()) {
		        			 Heads.getInstance().setCommand(id, command);
		        		 }
	        		 } catch(Exception ex) {
	        			 ex.printStackTrace();
	        		 }
	        		 
	        	 }
	        	 
	         }
	         
	         if(yamlFile.contains("scores")){
	        	 MessageHandler.getInstance().log(ChatColor.GOLD + " Parsing through scores...");
	        	 for(String key : yamlFile.getConfigurationSection("scores").getKeys(false)) {
	        		 //Stored as "scores.<uuid>: score
	        		 try {
	        			 String uuid = key;
	        			 UUID ID = UUID.fromString(uuid);
	        			 int score = yamlFile.getInt("scores." +uuid);
	        			 OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(ID);
	        			 Heads.getInstance().updateTopScore(oPlayer, score);
	        			 MessageHandler.getInstance().debug(ChatColor.GOLD + " Added " + oPlayer.getName() + " with the score " + score );
	        		 } catch(Exception ex) {
	        			 ex.printStackTrace();
	        		 }
	        		 
	        	 }
	        	 MessageHandler.getInstance().debug(ChatColor.GOLD + " Updating sorted score list");
	        	 Heads.getInstance().updatedSortedList();
	        	 
	         }

		
	      MessageHandler.getInstance().log(ChatColor.GOLD + " Heads Loaded!");
			
		}
	}

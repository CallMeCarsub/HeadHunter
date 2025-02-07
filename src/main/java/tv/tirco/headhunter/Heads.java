package tv.tirco.headhunter;

import java.util.*;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.headhunter.config.Config;
import tv.tirco.headhunter.database.HeadFileManager;

public class Heads {
	
	//Data
	private static Heads instance;
	private boolean changed;
	private int nextID;
	
	//Head info
	private HashMap<Integer,String> hints;
	private HashBiMap<Integer, Location> heads;
	private HashBiMap<Integer, String> headNames;
	private HashMap<Integer,List<String>> commands;
	private List<Integer> deletedHeads;
	
	//Scoreboard
	private HashMap<UUID,Integer> top;
	private LinkedHashMap<UUID,Integer> sortedTop;
	
	public static Heads getInstance() {
		if (instance == null) {
			instance = new Heads();
			
		}
		return instance;
	}
	
	public Heads() {
		heads = HashBiMap.create();
		hints = new HashMap<Integer,String>();
		headNames = HashBiMap.create();
		commands = new HashMap<Integer,List<String>>();
		deletedHeads = new ArrayList<Integer>();
		
		
		top = new HashMap<UUID,Integer>();
		sortedTop = new LinkedHashMap<UUID,Integer>();
		this.setChanged(false);
		this.nextID = 0;
	}
	
	/*
	 * Misc
	 */

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	/*
	 * Scoreboard
	 */
	
	public LinkedHashMap<UUID,Integer> getSortedTopMap(){
		return this.sortedTop;
	}
	
	public void updateTopScore(Player player, int score) {
		top.put(player.getUniqueId(), score);
	}

	public void updateTopScore(UUID uuid, int score) {
		top.put(uuid, score);
		
	}

	public void updateTopScore(OfflinePlayer oPlayer, int score) {
		top.put(oPlayer.getUniqueId(), score);
		
	}
	
	
	public int getTopPlayerScore(OfflinePlayer oPlayer) {
		if(sortedTop.containsKey(oPlayer.getUniqueId())) {
			return sortedTop.get(oPlayer.getUniqueId());
		} else {
			return 0;
		}
	}
	
	public int getTopPlayerScore(UUID uuid) {
		if(sortedTop.containsKey(uuid)) {
			return sortedTop.get(uuid);
		} else {
			return 0;
		}
	}
	
	public UUID getTopPlayerPlayer(int slot) {
		List<UUID> sortedTopIterator = new ArrayList<>(sortedTop.keySet());
		//MessageHandler.getInstance().debug("TopPlayer check- Sorted Iterator size - 1 = " + (sortedTopIterator.size() - 1) + " requesting slot " + slot);
		if(sortedTopIterator.size() - 1 >= slot) {
			return sortedTopIterator.get(slot);
		}
		
		//MessageHandler.getInstance().debug("returning null");
		return null;
	}
	
	public List<String> getTopPlayerList() {
		List<String> top = new ArrayList<String>();
		if(this.sortedTop.isEmpty()) {
			top.add("No top list available.");
			return top;
		}
		int spot = 1;
		for(UUID id : sortedTop.keySet()) {

			OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(id); 
			top.add(ChatColor.GREEN + "#" + spot + ChatColor.WHITE + " - " + ChatColor.GOLD + oPlayer.getName() + ChatColor.WHITE + " - " + ChatColor.GOLD + sortedTop.get(id));
			spot ++;
		}
		return top;
	}
	
	public void updatedSortedList(){
		//MessageHandler.getInstance().debug("Updating sorted list.");
		this.sortedTop = sortByValue(top, Config.getInstance().getTopAmount());
	}
	
	public LinkedHashMap<UUID, Integer> sortByValue(HashMap<UUID, Integer> top2, int maxAmount) {
	    List<UUID> mapKeys = new ArrayList<>(top2.keySet());
	    List<Integer> mapValues = new ArrayList<>(top2.values());
	    Collections.sort(mapValues);
	    //Collections.sort(mapKeys);

	    LinkedHashMap<UUID, Integer> sortedMap =
	        new LinkedHashMap<>();

	    Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Integer val = valueIt.next();
	        Iterator<UUID> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	           UUID key = keyIt.next();
	            Integer comp1 = top2.get(key);
	            Integer comp2 = val;

	            if (comp1.equals(comp2)) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    LinkedHashMap<UUID, Integer> reverseMap = new LinkedHashMap<>();
	    List<Entry<UUID,Integer>> list = new ArrayList<>(sortedMap.entrySet());
	    
	    int sorted = 0;
		LuckPerms luckPerms = LuckPermsProvider.get();
	    for( int i = list.size() -1; i >= 0 ; i --){
	        Entry<UUID,Integer> e = list.get(i);
			User user = luckPerms.getUserManager().loadUser(e.getKey()).join();
			CachedPermissionData cachedPermissionData = user.getCachedData().getPermissionData();
			// if bypass no leaderboard or they don't have a no leaderboard
			if(!cachedPermissionData.checkPermission("cmc.events.noleaderboard").asBoolean()) {
				reverseMap.put(e.getKey(), e.getValue());
				sorted++;
			}
			if (sorted >= maxAmount) {
				break;
			}
	    }
	    return reverseMap;
	}


	/*
	 * Save & Load
	 */
	
	public void loadFromFile() {
		HeadFileManager.loadHeads();
	}
	
	public void saveHeads() {
		updatedSortedList();
		HeadFileManager.saveHeads();
	}
	
	/*
	 * Head Management
	 */

	public void fixHeads() {
		List<Integer> oldIds = new ArrayList<>();
		Map<Integer, Integer> oldToNewId = new HashMap<>();
		int currentCounter = 1;
		for(int i = 0; i <= nextID; i++){
			if(heads.containsKey(i)){
				oldIds.add(i);
				oldToNewId.put(i, currentCounter);
				currentCounter++;
			}
		}
		HeadHunter.headHunter.getLogger().info("Created new IDs for " + oldToNewId.size() + " heads, currentCounter: " + currentCounter);
		HeadHunter.headHunter.getLogger().info("Sorting...");
		HashMap<Integer,String> newHints = new HashMap<>();
		HashBiMap<Integer, Location> newHeads = HashBiMap.create();
		HashBiMap<Integer, String> newHeadNames = HashBiMap.create();
		HashMap<Integer,List<String>> newCommands = new HashMap<>();

		oldIds.forEach((old) -> {
			int sorted = oldToNewId.get(old);
			newHeads.put(sorted, heads.get(old).clone());
			if(hints.containsKey(old)) newHints.put(sorted, hints.get(old));
			if(headNames.containsKey(old)) newHeadNames.put(sorted, headNames.get(old));
			if(commands.containsKey(old)) newCommands.put(sorted, new ArrayList<>(commands.get(old)));
		});
		hints = newHints;
		heads = newHeads;
		headNames = newHeadNames;
		commands = newCommands;
		nextID = currentCounter;
		HeadHunter.headHunter.getLogger().info("Finished resorting all heads!");
	}
	
	public int addHead(Location loc) {
		if(heads.containsValue(loc)) {
			return -1;
		} else {
			heads.put(nextID, loc);
			changed = true;
			int returnID = nextID;
			this.nextID +=1;
			return returnID;
		}
	}
	
	public boolean addHead(Integer id, Location loc, boolean fromConfig) {
		heads.put(id, loc);
		if(!fromConfig) {
			this.setChanged(true);
		}
		if(id >= nextID) {
			nextID = id +1;
		}
		return true; 
	}
	
	/*
	 * Head Management
	 * Location
	 */
	
	public Location getLocFromValues(double x, double y, double z, String worldname) {
		World world = Bukkit.getWorld(worldname);
		if(world == null) {
			world = Bukkit.getWorlds().get(0);
			MessageHandler.getInstance().debug("Unabled to find world " + worldname + " defaulted to " + world.getName());
		}
		
		Location loc = new Location(Bukkit.getWorld(worldname), x, y, z);
		return loc;
	}
	
	public Location getLocFromValues(double x, double y, double z, World world) {
		Location loc = new Location(world, x, y, z);
		return loc;
	}
	
	public Location getLocFromHeadId(int id) {
		if(heads.containsKey(id)) {
			return heads.get(id);
		}
		return null;
	}

	public BiMap<Integer,Location> getHeads() {
		return heads;
	}

	public void setHeads(HashBiMap<Integer,Location> heads) {
		this.heads = heads;
	}

	public boolean isHead(Location loc) {
		return heads.containsValue(loc);
	}

	public int getHeadId(Location loc) {
		int i = heads.inverse().get(loc);

		return i;
	}

	public int getHeadAmount() {
		return heads.size();
	}
	
	public boolean headExists(Integer i) {
		return heads.containsKey(i);
	}
	
	/*
	 * Head Management
	 * Hints
	 */
	
	public String getHint(Integer i) {
		return getHint(i, false);
	}
	
	public String getHint(Integer i, boolean toSave) {
		String name = "No name";
		if(hasName(i)) {
			name = getName(i);
		}

		String defaultHint = "";
		if(!toSave) {
			defaultHint = ChatColor.GREEN + "Name: " + ChatColor.translateAlternateColorCodes('&', name)
			+ " " + ChatColor.GREEN + "ID: " + ChatColor.GOLD + i + "." + ChatColor.RESET + "\n";
		}

		if(hints.containsKey(i)) {
			String hint = hints.get(i);
			if(hint != null) {
				return defaultHint + hints.get(i);
			}
		}
		return defaultHint + "No hint available.";
	}
	
		
	public void setHint(int i, String s) {
		if(s == null) {
			s = "No hint available.";
		}
		hints.put(i, s);
		setChanged(true);
	}
	
	/*
	 * Head Management
	 * Commands
	 */
	
	/**
	 * Set a command for the specified head.
	 * @param i - The ID of the head.
	 * @param s - The command.
	 */
	public void addCommand(int i, String s) {
		if(s == null || s.isEmpty()) {
			return;
		}
		List<String> commands = getCommands(i); //Can not be null.
		commands.add(s);
		this.commands.put(i, commands);
		setChanged(true);
	}
	
	public void clearCommands(int i) {
		commands.put(i, new ArrayList<String>());
		setChanged(true);
	}
	
	/**
	 * Get the command linked to a head.
	 * @param ID of the head you want to get the command of.
	 * @return the command (as string).
	 */
	public List<String> getCommands(int i) {
		if(commands.containsKey(i)) {
			List<String> commandList = commands.get(i);
			if(commandList != null) {
				return commands.get(i);
			}
		}
		return new ArrayList<String>();
	}
	
	public boolean hasCommand(int i) {
		return commands.containsKey(i) && !commands.get(i).isEmpty();
	}

	/**
	 * Clears all information about a head from memory. 
	 * This will also make it so head is not saved on next save.
	 * @param id - The ID of the head to remove.
	 */
	public void deleteHead(int id) {
		if(heads.containsKey(id)) {
			heads.remove(id);
			deletedHeads.add(id);
			setChanged(true);
		}
		if(headNames.containsKey(id)) {
			headNames.remove(id);
		}
		if(commands.containsKey(id)) {
			commands.remove(id);
		}
		if(hints.containsKey(id)){
			hints.remove(id);
		}
		
	}

	/*
	 * Head Management
	 * Names
	 */
	
	/**
	 * Get if the head has a name linked to it.
	 * @param ID of the head
	 * @return if name is set or not.
	 */
	public Boolean hasName(int i) {
		return headNames.containsKey(i);
	}
	
	/**
	 * Get the name of the head. (If any)
	 * @param ID of the head
	 * @return Name of the head, if any. Null if not.
	 */
	public String getName(int i) {
		if(headNames.containsKey(i)) {
			return headNames.get(i);
		} else {
			return "";
		}
	}
	

	/**
	 * Oops! Unique names!
	 * @param id - The ID to set the name for.
	 * @param name - The name to put.
	 */
	public boolean setName(Integer id, String name, boolean fromCommand) {
		if(headNames.containsValue(name)) {
			if(!fromCommand) {//Warn console if this was done from config.
				MessageHandler.getInstance().log("WARNING: Tried to set name of head with ID " + id + " to " + name + ". This is not allowed, as names are unique.");
			}
			return false;
		} 
		headNames.put(id, name);
		setChanged(true);
		return true;
	}
	
	/**
	 * 
	 * @param Name of the head. CaseSensitive.
	 * @return ID of the head.
	 */
	public int getHeadIDFromName(String name) {
		if(headNames.containsValue(name)) {
			return headNames.inverse().get(name);
		}
		return -1;
	}
	
	/**
	 * 
	 * @param loc Location to check if it's near
	 * @param distance Max distance of head
	 * @param ignore List of heads to ignore.
	 * @return
	 */
	public List<Location> getHeadsNear(Location loc, double distance, List<Integer> ignore) {
		List<Location> test = new ArrayList<Location>();
		for(Integer i : getHeads().keySet()) {
			if(ignore.contains(i)) {
				continue;
			}
			Location hLoc = getHeads().get(i);
			if(hLoc.getWorld().equals(loc.getWorld())) {
				if(hLoc.distance(loc) < distance) {
					test.add(hLoc);
				}
			}
		}
		return test;
	}

	public Set<String> getHeadNames() {
		return this.headNames.values();
	}

	
	public void setCommands(Integer id, List<String> commands) {
		this.commands.put(id,commands);
	}

	public boolean headsDeleted() {
		return !deletedHeads.isEmpty();
	}
	

	public List<Integer> getDeletedHeads() {
		return deletedHeads;
	}
	
	public void clearDeletedHeads() {
		this.deletedHeads.clear();
	}
}

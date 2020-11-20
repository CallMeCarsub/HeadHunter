package tv.tirco.headhunter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import tv.tirco.headhunter.config.Config;
import tv.tirco.headhunter.database.HeadFileManager;

public class Heads {
	
	private static Heads instance;
	private boolean changed;
	private int nextID;
	private HashMap<Integer,String> hints;
	private HashBiMap<Integer, Location> heads;
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
		top = new HashMap<UUID,Integer>();
		sortedTop = new LinkedHashMap<UUID,Integer>();
		this.setChanged(false);
		this.nextID = 0;
	}
	
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

	    for( int i = list.size() -1; i >= 0 ; i --){
	        Entry<UUID,Integer> e = list.get(i);
	        reverseMap.put(e.getKey(), e.getValue());
	        sorted++;
	        if(sorted >= maxAmount) {
	        	break;
	        }
	    }
	    return reverseMap;
	}


	
	public void loadFromFile() {
		HeadFileManager.loadHeads();
	}
	
	public void saveHeads() {
		updatedSortedList();
		HeadFileManager.saveHeads();
	}
	
	public boolean addHead(Location loc) {
		if(heads.containsValue(loc)) {
			return false;
		} else {
			heads.put(nextID, loc);
			changed = true;
			this.nextID +=1;
			return true;
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

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
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

	
	public String getHint(Integer i) {
		if(hints.containsKey(i)) {
			String hint = hints.get(i);
			if(hint != null) {
				return hints.get(i);
			}
		}
		return "No hint available.";
	}
	
	public void setHint(int i, String s) {
		if(s == null) {
			s = "No hint available.";
		}
		hints.put(i, s);
	}

	public void deleteHead(int id) {
		if(heads.containsKey(id)) {
			heads.remove(id);
		}
		
	}


}

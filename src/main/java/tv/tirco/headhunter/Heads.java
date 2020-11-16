package tv.tirco.headhunter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import tv.tirco.headhunter.database.HeadFileManager;

public class Heads {
	
	private static Heads instance;
	private boolean changed;
	private int nextID;
	
	public static Heads getInstance() {
		if (instance == null) {
			instance = new Heads();
			
		}
		return instance;
	}
	
	public Heads() {
		heads = HashBiMap.create();
		this.setChanged(false);
		this.nextID = 0;
	}
	
	public void loadFromFile() {
		HeadFileManager.loadHeads();
	}
	
	public void saveHeads() {
		if(changed) {
			HeadFileManager.saveHeads();
		}
	}
	
	private HashBiMap<Integer, Location> heads;
	
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
}

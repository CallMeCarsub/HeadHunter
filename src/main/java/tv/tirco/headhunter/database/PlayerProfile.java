package tv.tirco.headhunter.database;

import java.util.HashMap;
import java.util.UUID;

public class PlayerProfile {
	
	private final String playerName;
	private UUID uuid;
	private boolean loaded;
	private volatile boolean changed;
	
	private HashMap<Integer,Boolean> found;
	
	// When loading from file etc.
	/**
	 * 
	 */
	public PlayerProfile(String playerName, UUID uuid, HashMap<Integer,Boolean> found) {
		this.playerName = playerName;
		this.setUuid(uuid);
		this.setFound(found);
		this.setLoaded(true);
	}

	public String getPlayerName() {
		return playerName;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public HashMap<Integer,Boolean> getFound() {
		return found;
	}

	public void setFound(HashMap<Integer,Boolean> found) {
		this.found = found;
	}
	
	public boolean hasFound(int id) {
		if(found.containsKey(id)) {
			return found.get(id);
		}
		return false;
	}
	
	public void find(int id) {
		found.put(id, true);
		changed = true;
	}
	

}

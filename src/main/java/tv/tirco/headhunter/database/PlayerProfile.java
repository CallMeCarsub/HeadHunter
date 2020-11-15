package tv.tirco.headhunter.database;

import java.util.HashMap;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.MessageHandler;

public class PlayerProfile {
	
	private final String playerName;
	private UUID uuid;
	private boolean loaded;
	private volatile boolean changed;
	
	private HashMap<Integer,Boolean> found;
	
	// When loading from file etc.
	
	public PlayerProfile(String playerName, UUID uuid, HashMap<Integer,Boolean> found) {
		this.playerName = playerName;
		this.setUuid(uuid);
		this.setFound(found);
		this.setLoaded(true);
		
		//New user, no "found" file.
		if(this.found == null) {
			this.found = new HashMap<Integer,Boolean>();
		}
	}
	
		public PlayerProfile(String playerName) {
		this(playerName, null);
	}

	public PlayerProfile(String playerName, UUID uuid) {
		this.uuid = uuid;
		this.playerName = playerName;
	}

	public PlayerProfile(String playerName, boolean isLoaded) {
		this(playerName);
		this.loaded = isLoaded;
	}

	public PlayerProfile(String playerName, UUID uuid, boolean isLoaded) {
		this(playerName, uuid);
		this.loaded = isLoaded;
	}


	public void save() {
		if (!changed || !loaded) {
			return;
		}
		MessageHandler.log("Saving PlayerProfile of player " + playerName + " ...");
		PlayerProfile profileCopy = new PlayerProfile(playerName, uuid, found);
		changed = !HeadHunter.db.saveUser(profileCopy);

		if (changed) {
			MessageHandler.log(ChatColor.RED + "PlayerProfile saving failed for player: " + ChatColor.WHITE + playerName
					+ " , uuid: " + uuid);
		}
	}
	
	public void scheduleAsyncSave() {
		new PlayerProfileSaveTask(this).runTaskAsynchronously(HeadHunter.plugin);
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
		if(found.isEmpty()) {
			return false;
		} else if(found.containsKey(id)) {
			return found.get(id);
		}
		return false;
	}
	
	public void find(int id) {
		found.put(id, true);
		changed = true;
	}
	
	public int getAmountFound() {
		int amountFound = 0;
		for(int i : found.keySet()) {
			if(found.get(i)) {
				amountFound ++;
			}
		}
		return amountFound;
	}
	

}

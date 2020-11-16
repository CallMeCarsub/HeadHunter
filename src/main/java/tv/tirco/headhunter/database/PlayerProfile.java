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
	private int foundAmount;
	
	private HashMap<Integer,Boolean> found;
	
	// When loading from file etc.
	
	public PlayerProfile(String playerName, UUID uuid, HashMap<Integer,Boolean> found, int foundAmount) {
		this.playerName = playerName;
		this.setUuid(uuid);
		this.setFound(found);
		this.setLoaded(true);
		this.foundAmount = foundAmount;
		
		//New user, no "found" file.
		if(this.found == null) {
			this.found = new HashMap<Integer,Boolean>();
		}
	}
	
	public PlayerProfile(String playerName) {
		this(playerName, null);
		//New user, no "found" file.
		if(this.found == null) {
			this.found = new HashMap<Integer,Boolean>();
		}
		//Update found amount.
		getAmountFound();
		

	}

	public PlayerProfile(String playerName, UUID uuid) {
		this.uuid = uuid;
		this.playerName = playerName;
		//New user, no "found" file.
		if(this.found == null) {
				this.found = new HashMap<Integer,Boolean>();
		}
		//Update found amount.
		getAmountFound();
	}

	public PlayerProfile(String playerName, boolean isLoaded) {
		this(playerName);
		this.loaded = isLoaded;
		//New user, no "found" file.
		if(this.found == null) {
			this.found = new HashMap<Integer,Boolean>();
		}
	}

	public PlayerProfile(String playerName, UUID uuid, boolean isLoaded) {
		this(playerName, uuid);
		this.loaded = isLoaded;
		//New user, no "found" file.
		if(this.found == null) {
			this.found = new HashMap<Integer,Boolean>();
		}
		//Update found amount.
		getAmountFound();
	}


	public void save() {
		if (!changed || !loaded) {
			return;
		}
		
		//Do not save the user if they haven't found any skulls!
		if(getAmountFound() < 1) {
			return;
		}
		
		MessageHandler.getInstance().log("Saving PlayerProfile of player " + playerName + " ...");
		PlayerProfile profileCopy = new PlayerProfile(playerName, uuid, found, foundAmount);
		changed = !HeadHunter.db.saveUser(profileCopy);

		if (changed) {
			MessageHandler.getInstance().log(ChatColor.RED + "PlayerProfile saving failed for player: " + ChatColor.WHITE + playerName
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
		if(found.containsKey(id)) {
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
		//Update internal statistic for saving & scoreboard
		this.foundAmount = amountFound;
		return amountFound;
	}
	

}

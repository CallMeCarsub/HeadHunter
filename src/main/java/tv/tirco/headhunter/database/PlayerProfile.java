package tv.tirco.headhunter.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.Heads;
import tv.tirco.headhunter.MessageHandler;

public class PlayerProfile {
	
	private final String playerName;
	private UUID uuid;
	private boolean loaded;
	private volatile boolean changed;
	private int foundAmount;
	private boolean addMode = false;
	
	private HashMap<Integer,Boolean> found;
	
	// When loading from file etc.
	
	public PlayerProfile(String playerName, UUID uuid, HashMap<Integer,Boolean> found, int foundAmount) {
		this.playerName = playerName;
		this.setUuid(uuid);
		this.setFound(found);
		this.setLoaded(true);
		this.foundAmount = foundAmount;
		this.loaded = true;
		this.changed = true; //Changed so we get an updated last login.
		
		//New user, no "found" file.
		if(this.found == null) {
			this.found = new HashMap<Integer,Boolean>();
		}
		validateHeads();
	}
	
//	public PlayerProfile(String playerName) {
//		this(playerName, null);
//		//New user, no "found" file.
//		if(this.found == null) {
//			this.found = new HashMap<Integer,Boolean>();
//		}
//		//Update found amount.
//		getAmountFound();
//		
//
//	}
//
//	public PlayerProfile(String playerName, UUID uuid) {
//		this.uuid = uuid;
//		this.playerName = playerName;
//		//New user, no "found" file.
//		if(this.found == null) {
//				this.found = new HashMap<Integer,Boolean>();
//		}
//		//Update found amount.
//		getAmountFound();
//	}
//
//	public PlayerProfile(String playerName, boolean isLoaded) {
//		this(playerName);
//		this.loaded = isLoaded;
//		//New user, no "found" file.
//		if(this.found == null) {
//			this.found = new HashMap<Integer,Boolean>();
//		}
//	}
//
	public PlayerProfile(String playerName, UUID uuid, boolean isLoaded) {
		this.uuid = uuid;
		this.playerName = playerName;
		this.loaded = isLoaded;
		//New user, no "found" file.
		if(this.found == null) {
			this.found = new HashMap<Integer,Boolean>();
		}
		//Update found amount.
		getAmountFound();
	}


	public void save() {
		if ((!changed || !loaded)) {
			MessageHandler.getInstance().debug("Not saving profile for " + playerName + ". Loaded: " + loaded + " Changed:" + changed);
			return;
		}
		
		
		MessageHandler.getInstance().debug("Saving PlayerProfile of player " + playerName + " ...");
		PlayerProfile profileCopy = new PlayerProfile(playerName, uuid, found, foundAmount);
		changed = !HeadHunter.db.saveUser(profileCopy);

		if (changed) {
			MessageHandler.getInstance().log(ChatColor.RED + "PlayerProfile saving failed for player: " + ChatColor.WHITE + playerName
					+ " , uuid: " + uuid);
		}
	}
	
	public void validateHeads() {
		//Fixes deleted heads.
		HashMap<Integer,Boolean> validatedList = new HashMap<Integer,Boolean>();
		for(Integer i : found.keySet()) {
			if(Heads.getInstance().headExists(i)) {
				validatedList.put(i, found.get(i));
			}
		}
		found = validatedList;
		getAmountFound();
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
		getAmountFound();
		Heads.getInstance().updateTopScore(uuid, foundAmount);
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

	
	public void toggleAddMode() {
		this.addMode = !addMode;
	}
	
	public void setAddMode(boolean state) {
		this.addMode = state;
	}
	
	public boolean getAddMode() {
		return addMode;
	}

	public List<Integer> getFoundIDs() {
		List<Integer> foundIDs = new ArrayList<Integer>();
		foundIDs.addAll(this.found.keySet());
//		for(Integer i : this.found.keySet()) {
//			if(found.get(i)) {
//				foundIDs.add(i);
//			}
//		}
		return foundIDs;
	}
	
	public List<Integer> getNotFoundIDs() {
		List<Integer> notFound = new ArrayList<Integer>(); 
		notFound.addAll(Heads.getInstance().getHeads().keySet());
		for(int i : this.found.keySet()) {
			if(found.get(i)) {
				notFound.remove(i);
			}
		}
		return notFound;
	}
	

}

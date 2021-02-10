package tv.tirco.headhunter.database;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.Heads;

public class PlayerData {
	//Class used to access PlayerProfile information.
	
	private Player player;
	private PlayerProfile profile;

	private final FixedMetadataValue playerMetadata;
	
	public PlayerData(Player player, PlayerProfile profile) {
		String playerName = player.getName();
		UUID uuid = player.getUniqueId();

		// final Map<AttributeType, AttributeManager> attributeManagers = new
		// HashMap<AttributeType, AttributeManager>();

		this.setPlayer(player);
		this.playerMetadata = new FixedMetadataValue(HeadHunter.plugin, playerName);
		this.profile = profile;

		if (profile.getUuid() == null) {
			profile.setUuid(uuid);
		}
		
		Heads.getInstance().updateTopScore(player, getAmountFound());

	}
	
	public void save() {
		profile.save();
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public UUID getUuid() {
		return profile.getUuid();
	}
	
	public boolean isLoaded() {
		return profile.isLoaded();
	}
	
	public boolean isChanged() {
		return profile.isChanged();
	}
	
	public HashMap<Integer,Boolean> getFound() {
		return profile.getFound();
	}
	
	public List<Integer> getFoundIDs() {
		return profile.getFoundIDs();
	}
	
	public void setFound(HashMap<Integer,Boolean> found) {
		profile.setFound(found);
	}
	
	public boolean hasFound(int id) {
		return profile.hasFound(id);
	}
	
	public void find(int id) {
		profile.find(id);
	}

	public PlayerProfile getProfile() {
		return profile;
	}

	public int getAmountFound() {
		return profile.getAmountFound();
	}

	
	public void logout(boolean syncSave) {
		Player thisPlayer = getPlayer();

		if (syncSave) {
			getProfile().save();
		} else {
			getProfile().scheduleAsyncSave();
		}

		UserManager.remove(thisPlayer);
	}

	
	public void toggleAddMode() {
		profile.toggleAddMode();
	}
	
	public void setAddMode(Boolean state) {
		profile.setAddMode(state);
	}
	
	public boolean getAddMode() {
		return profile.getAddMode();
	}

	public FixedMetadataValue getPlayerMetadata() {
		return playerMetadata;
	}

}

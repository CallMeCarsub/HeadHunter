package tv.tirco.headhunter.database;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import tv.tirco.headhunter.HeadHunter;

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

}

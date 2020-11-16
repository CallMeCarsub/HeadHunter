package tv.tirco.headhunter.database;


import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import com.google.common.collect.ImmutableList;

import net.md_5.bungee.api.ChatColor;
import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.MessageHandler;

public class UserManager {

	private UserManager() {
	}

	/**
	 * Track a new user.
	 * 
	 * @param recklessPlayer the player profile to start tracking.
	 */
	public static void track(PlayerData pData) {
		pData.getPlayer().setMetadata(HeadHunter.playerDataKey, new FixedMetadataValue(HeadHunter.plugin, pData));
	}

	/**
	 * Remove a user.
	 * 
	 * @param player - the player object
	 */
	public static void remove(Player player) {
		player.removeMetadata(HeadHunter.playerDataKey, HeadHunter.plugin);
	}

	/**
	 * Clear all users.
	 */
	public static void clearAll() {
		for (Player player : HeadHunter.plugin.getServer().getOnlinePlayers()) {
			remove(player);
		}
	}

	/**
	 * Save all users on this thread
	 */
	public static void saveAll() {
		ImmutableList<Player> onlinePlayers = ImmutableList.copyOf(HeadHunter.plugin.getServer().getOnlinePlayers());
		// VoidRPG.plugin.debug("Saving players... (" + onlinePlayers.size() + ")" );

		for (Player player : onlinePlayers) {
			try {
				getPlayer(player).getProfile().save();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * get the RecklessPlayer of a player by name.
	 * 
	 * @param playerName The name of the player whose McMMOPlayer to retreive.
	 * @return the players RecklessPlayer object
	 */
	public static PlayerData getPlayer(String playerName) {
		return retrieveRecklessPlayer(playerName, false);
	}

	public static PlayerData getOfflinePlayer(OfflinePlayer player) {
		if (player instanceof Player) {
			return getPlayer((Player) player);
		}

		return retrieveRecklessPlayer(player.getName(), true);
	}

	public static PlayerData getOfflinePlayer(String playerName) {
		return retrieveRecklessPlayer(playerName, true);
	}

	public static PlayerData getPlayer(Player player) {
		return (PlayerData) player.getMetadata(HeadHunter.playerDataKey).get(0).value();
	}

	private static PlayerData retrieveRecklessPlayer(String playerName, boolean offlineValid) {
		Player player = HeadHunter.plugin.getServer().getPlayerExact(playerName);

		if (player == null) {
			if (!offlineValid) {
				MessageHandler.getInstance().debug("A valid PlayerData object could not be found for " + playerName + ".");
			}

			return null;
		}

		return getPlayer(player);
	}

	public static boolean hasPlayerDataKey(Entity entity) {
		return entity != null && entity.hasMetadata(HeadHunter.playerDataKey);
	}

	public static Collection<PlayerData> getPlayers() {
		Collection<PlayerData> playerCollection = new ArrayList<PlayerData>();

		for (Player player : HeadHunter.plugin.getServer().getOnlinePlayers()) {
			if (hasPlayerDataKey(player)) {
				playerCollection.add(getPlayer(player));
			}
		}

		return playerCollection;
	}

	public static void updateOffHandStats(PlayerData pData) {

	}

	

	public static void profileCleanup(String playerName) {
		Player player = HeadHunter.plugin.getServer().getPlayerExact(playerName);

		if (player != null) {
			UserManager.remove(player);
			new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(HeadHunter.plugin, 1); // 1 Tick delay to
																								// ensure the player is
																								// marked as online
																								// before we begin
																								// loading
		}
	}

	public static void sendErrorMessage(Player player, String error) {
		player.sendMessage("Your account seems to not be loaded properly.");
		player.sendMessage("Please relogg in an attempt to fix this issue.");
		player.sendMessage("If it percists, contact an administrator.");
		player.sendMessage(ChatColor.RED + "ERROR: " + error);
	}

}

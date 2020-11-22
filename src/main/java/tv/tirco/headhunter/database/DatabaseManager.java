package tv.tirco.headhunter.database;

import java.util.List;
import java.util.UUID;

import tv.tirco.headhunter.config.Config;

public interface DatabaseManager {
	
	// one month in milliseconds.
	public final long PURGE_TIME = 2630000000L * Config.getInstance().getOldUsersCutoff();

//	/**
//	 * Purge users with a level of 0 from the database.
//	 */
//	public void purgePowerlessUsers();

	/**
	 * Purge users who haven't logged in for a set time.
	 */
	public void purgeOldUsers();

	/**
	 * Remove a user from the database.
	 * 
	 * @param playerName The name of the user to remove.
	 * @return true if the user was successfully removed, false if not.
	 */
	public boolean removeUser(String playerName);

	/**
	 * Save a user to the database.
	 * 
	 * @param profile The profile of the player to save.
	 * @return true if successful, false on failure.
	 */
	public boolean saveUser(PlayerProfile profile);

	/**
	 * Add a new user to the database.
	 * 
	 * @param playerName The name of the player to be added to teh database
	 * @param uuid       The UUID of the player to be added to the database
	 * @return
	 */
	public void newUser(String playerName, UUID uuid);

//	/**
//	 * Load a player from the database.
//	 * 
//	 * @param uuid the UUID of the player to load from the database.
//	 * @return the player's data, or an unloaded PlayerProfile if not found
//	 */
//	public PlayerProfile loadPlayerProfile(UUID uuid);
//
//	/**
//	 * Load a player from the database. Attempt to use uuid, fall back on playername
//	 *
//	 * @param uuid       The uuid of the player to load from the database
//	 * @param createNew  Whether to create a new record if the player is not found
//	 * @return The player's data, or an unloaded PlayerProfile if not found and
//	 *         createNew is false
//	 */
//	public PlayerProfile loadPlayerProfile(UUID uuid, boolean createNew);

	/**
	 * Get all users currently stored in the database.
	 *
	 * @return list of playernames
	 */
	public List<String> getStoredUsers();

	/**
	 * Called when the plugin disables
	 */
	public void onDisable();

	/**
	 * 
	 * @param playerName
	 * @param uuid
	 * @param create
	 * @param retry
	 * @return
	 */
	PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create, boolean retry);
}

package tv.tirco.headhunter.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.MessageHandler;

public class PlayerFileManager implements DatabaseManager {

	// FLATFILE VERSION
	//PlayerName 0: UUID 1: LastSeen 2: 3+ -> unlocked 0=0: BREAK:
	static int PLAYERNAME_POSITION = 0;
	static int UUID_POSITION = 1;
	static int LAST_SEEN_POSITION = 2;
	static int AMOUNT_FOUND_POSITION = 3;

	private static final Object fileWritingLock = new Object();
    public static final int TIME_CONVERSION_FACTOR = 1000;
    public static final int TICK_CONVERSION_FACTOR = 20;

	// private static final long PURGE_TIME = 999999999; //TODO set this to a
	// reachable time

	@SuppressWarnings("unused")
	private final long UPDATE_WAIT_TIME = 600000L; // 10 minutes - TODO add autoUpdate?

	private final File usersFile;

	public PlayerFileManager() {
		usersFile = new File(HeadHunter.getUsersFilePath());
		checkStructure();
	}

	public void onDisable() {

	}

	public void purgePowerlessUsers() {
		//Should never be used? - Powerless users are not saved.
		int purgedUsers = 0;

		MessageHandler.log("Purging powerless users...");

		BufferedReader in = null;
		FileWriter out = null;
		String usersFilePath = HeadHunter.getUsersFilePath();

		// This code is O(n) instead of O(n�)
		synchronized (fileWritingLock) {
			try {
				in = new BufferedReader(new FileReader(usersFilePath));
				StringBuilder writer = new StringBuilder();
				String line;

				while ((line = in.readLine()) != null) {
					ArrayList<String> character = new ArrayList<String>(Arrays.asList(line.split(":")));
					boolean powerless = false;
					
					//String lastSeen = character.get(2);
					//String uuid = character.get(1);
					//String name = character.get(0);
					
					character.remove(character.size()-1); //Remove BREAK
					character.remove(2); //Remove LastSeen
					character.remove(1); //Remove UUID
					character.remove(0); //Remove Name
					
					powerless = true;
					for(String s : character) {
						
						String[] found = s.split("=");
						if(found[1].equalsIgnoreCase("1")) {
							powerless = false;
						}
					}

					// If they're still around, rewrite them to the file.
					if (!powerless) {
						writer.append(line).append("\r\n");
					} else {
						purgedUsers++;
					}
				}

				// Write the new file
				out = new FileWriter(usersFilePath);
				out.write(writer.toString());
			} catch (IOException e) {
				// RecklessRPG.p.logError("Exception while reading " + usersFilePath + " (Are
				// you sure you formatted it correctly?)" + e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}

		MessageHandler.log("Purged " + purgedUsers + " users from the database.");

	}

	@SuppressWarnings("deprecation")
	public void purgeOldUsers() {
		int removedPlayers = 0;
		long currentTime = System.currentTimeMillis();

		MessageHandler.log("Purging old users...");

		BufferedReader in = null;
		FileWriter out = null;
		String usersFilePath = HeadHunter.getUsersFilePath();

		// This code is O(n) instead of O(n�)
		synchronized (fileWritingLock) {
			try {
				in = new BufferedReader(new FileReader(usersFilePath));
				StringBuilder writer = new StringBuilder();
				String line;

				while ((line = in.readLine()) != null) {
					String[] character = line.split(":");
					String name = character[PLAYERNAME_POSITION];
					long lastPlayed = 0;
					boolean rewrite = false;
					try {
						lastPlayed = Long.parseLong(character[LAST_SEEN_POSITION]) * TIME_CONVERSION_FACTOR;
					} catch (NumberFormatException e) {
					}
					if (lastPlayed == 0) {
						OfflinePlayer player = HeadHunter.plugin.getServer().getOfflinePlayer(name);
						lastPlayed = player.getLastPlayed();
						rewrite = true;
					}

					if (currentTime - lastPlayed > PURGE_TIME) {
						removedPlayers++;
					} else {
						if (rewrite) {
							// Rewrite their data with a valid time
							character[LAST_SEEN_POSITION] = Long.toString(lastPlayed);
							String newLine = org.apache.commons.lang.StringUtils.join(character, ":");
							writer.append(newLine).append("\r\n");
						} else {
							writer.append(line).append("\r\n");
						}
					}
				}

				// Write the new file
				out = new FileWriter(usersFilePath);
				out.write(writer.toString());
			} catch (IOException e) {
				MessageHandler.log("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)"
						+ e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}

		MessageHandler.log("Purged " + removedPlayers + " users from the database.");
	}

	public boolean removeUser(String playerName) {
		boolean worked = false;

		BufferedReader in = null;
		FileWriter out = null;
		String usersFilePath = HeadHunter.getUsersFilePath();

		synchronized (fileWritingLock) {
			try {
				in = new BufferedReader(new FileReader(usersFilePath));
				StringBuilder writer = new StringBuilder();
				String line;

				while ((line = in.readLine()) != null) {
					// Write out the same file but when we get to the player we want to remove, we
					// skip his line.
					if (!worked && line.split(":")[PLAYERNAME_POSITION].equalsIgnoreCase(playerName)) {
						// RecklessRPG.p.log("User found, removing...");
						worked = true;
						continue; // Skip the player
					}

					writer.append(line).append("\r\n");
				}

				out = new FileWriter(usersFilePath); // Write out the new file
				out.write(writer.toString());
			} catch (Exception e) {
				MessageHandler.log("Exception while reading " + usersFilePath + " (Are you sure you formatted it correctly?)"
						+ e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}

		UserManager.profileCleanup(playerName);

		return worked;
	}

	public boolean saveUser(PlayerProfile profile) {
		String playerName = profile.getPlayerName();
		UUID uuid = profile.getUuid();
		String amountFound = ""+profile.getAmountFound();

		BufferedReader in = null;
		FileWriter out = null;
		String usersFilePath = HeadHunter.getUsersFilePath();

		synchronized (fileWritingLock) {
			try {
				// open the file
				in = new BufferedReader(new FileReader(usersFilePath));
				StringBuilder writer = new StringBuilder();
				String line;

				while ((line = in.readLine()) != null) {
					// read the line in and copy it to the output if it's not the player we want to
					// edit.
					String[] character = line.split(":");
					if (!(uuid != null && character[UUID_POSITION].equalsIgnoreCase(uuid.toString()))
							&& !character[PLAYERNAME_POSITION].equalsIgnoreCase(playerName)) {
						writer.append(line).append("\r\n");
					} else {
						// otherwise write the new player information
						writer.append(playerName).append(":"); // PlayerName - line 0
						writer.append(uuid != null ? uuid.toString() : "NULL").append(":"); // UUID - 1
						writer.append(String.valueOf(System.currentTimeMillis() / TIME_CONVERSION_FACTOR))
						.append(":"); // LastLogin - 2
						writer.append(amountFound).append(":");//AMOUNT_FOUND_POSITION - 3
						
						//Loop through all possible skulls and save the ones that are true.
						for(int i : profile.getFound().keySet()) {
							if(profile.getFound().get(i)) {
								writer.append(i+"=1").append(":");
							}
						}					
						writer.append("BREAK").append(":");
						writer.append("\r\n");
					}

				}

				// write new file
				out = new FileWriter(usersFilePath);
				out.write(writer.toString());
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}
	}

	public void newUser(String playerName, UUID uuid) {
		BufferedWriter out = null;
		synchronized (fileWritingLock) {
			try {
				// open the file to write the player
				out = new BufferedWriter(new FileWriter(HeadHunter.getUsersFilePath(), true));

				// add the player + stats to the end. All stats are 0 by default.
				//PlayerName 0: UUID 1: LastSeen 2: 3+ -> unlocked 0=0: BREAK:
				out.append(playerName).append(":"); // PlayerName - line 0
				out.append(uuid != null ? uuid.toString() : "NULL").append(":"); // UUID - 1
				out.append(String.valueOf(System.currentTimeMillis() / TIME_CONVERSION_FACTOR)).append(":"); //Time - 2
				out.append("0:"); //AMOUNT_FOUND_POSITION 3
				out.append("0=0:");//Unlocked 4 - ?
				out.append("BREAK:"); // unspent level points - Final
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}
	}

	public PlayerProfile loadPlayerProfile(String playerName, boolean create) {
		return loadPlayerProfile(playerName, null, false);
	}

	public PlayerProfile loadPlayerProfile(UUID uuid) {
		return loadPlayerProfile("", uuid, false);
	}

	public PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create) {
		BufferedReader in = null;
		String usersFilePath = HeadHunter.getUsersFilePath();

		synchronized (fileWritingLock) {
			try {
				// Open the user file
				in = new BufferedReader(new FileReader(usersFilePath));
				String line;

				while ((line = in.readLine()) != null) {
					// Find if the line contains the player we want.
					String[] character = line.split(":");

					// Compare names because we don't have a valid uuid for that player even
					// if input uuid is not null
					if (character[UUID_POSITION].equalsIgnoreCase("NULL")) {
						if (!character[PLAYERNAME_POSITION].equalsIgnoreCase(playerName)) {
							continue;
						}
					}
					// If input uuid is not null then we should compare uuids
					else if ((uuid != null && !character[UUID_POSITION].equalsIgnoreCase(uuid.toString()))
							|| (uuid == null && !character[PLAYERNAME_POSITION].equalsIgnoreCase(playerName))) {
						continue;
					}

					// Update playerName in database after name change
					if (!character[PLAYERNAME_POSITION].equalsIgnoreCase(playerName)) {
						MessageHandler.log("Name change detected: " + character[PLAYERNAME_POSITION] + " => " + playerName);
						character[PLAYERNAME_POSITION] = playerName;
					}

					return loadFromLine(character);
				}

				// Didn't find the player, create a new one
				if (create) {
					Bukkit.getConsoleSender().sendMessage("Didn't find player, creating new one...");
					if (uuid == null) {
						Bukkit.getConsoleSender().sendMessage("UUID of new player is NULL");
						newUser(playerName, uuid);
						return new PlayerProfile(playerName, true);
					}

					newUser(playerName, uuid);
					return new PlayerProfile(playerName, uuid, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// I have no idea why it's necessary to inline tryClose() here, but it removes
				// a resource leak warning, and I'm trusting the compiler on this one.
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}

		// Return unloaded profile
		if (uuid == null) {
			return new PlayerProfile(playerName);
		}

		return new PlayerProfile(playerName, uuid);
	}

	private PlayerProfile loadFromLine(String[] character) {
		HashMap<Integer, Boolean> found = new HashMap<Integer, Boolean>(); // Skill levels

		UUID uuid;
		try {
			uuid = UUID.fromString(character[UUID_POSITION]);
		} catch (Exception e) {
			uuid = null;
		}
		String playerName = character[PLAYERNAME_POSITION];
		int amountFound = 0;
		try {
			amountFound = Integer.parseInt(character[AMOUNT_FOUND_POSITION]);
		} catch(NumberFormatException ex){
			MessageHandler.getInstance().debug("Error while loading Amount Found for Player " + playerName + ". This will automatically be recounted later, but something must be wrong with saving/loading!");
		}
		
		
		ArrayList<String> characterList = new ArrayList<String>(Arrays.asList(character));
		
		//String lastSeen = character.get(2);
		//String uuid = character.get(1);
		//String name = character.get(0);
		
		characterList.remove(characterList.size()-1); //Remove BREAK
		characterList.remove(3); //Remove AmountFound
		characterList.remove(2); //Remove LastSeen
		characterList.remove(1); //Remove UUID
		characterList.remove(0); //Remove Name
		
		for(String s : characterList) {
			
			String[] foundLine = s.split("=");
			//TODO implement skip if line does not contain =
			if(foundLine[1].equalsIgnoreCase("1")) {
				//Get Found[0] as integer.
				try{
					Integer pos = Integer.parseInt(foundLine[0]);
					found.put(pos, true);
				} catch(NumberFormatException ex) {
					MessageHandler.getInstance().debug("Failed to parse " + s + " for player " + playerName);
					continue;
				}
			}
		}
		

		return new PlayerProfile(playerName, uuid, found, amountFound);
	}

	public List<String> getStoredUsers() {
		ArrayList<String> users = new ArrayList<String>();
		BufferedReader in = null;
		String usersFilePath = HeadHunter.getUsersFilePath();

		synchronized (fileWritingLock) {
			try {
				// Open the user file
				in = new BufferedReader(new FileReader(usersFilePath));
				String line;

				while ((line = in.readLine()) != null) {
					String[] character = line.split(":");
					users.add(character[PLAYERNAME_POSITION]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// Ignore
					}
				}
			}
		}
		return users;
	}

	/**
	 * Checks that the file is present and valid
	 */
	private void checkStructure() {
		if (usersFile.exists()) {
			BufferedReader in = null;
			FileWriter out = null;
			String usersFilePath = HeadHunter.getUsersFilePath();

			synchronized (fileWritingLock) {
				try {
					in = new BufferedReader(new FileReader(usersFilePath));
					StringBuilder writer = new StringBuilder();
					String line;
					HashSet<String> usernames = new HashSet<String>();
					HashSet<String> players = new HashSet<String>();

					while ((line = in.readLine()) != null) {
						// Remove empty lines from the file
						if (line.isEmpty()) {
							continue;
						}

						// Length checks depend on last character being ':'
						if (line.charAt(line.length() - 1) != ':') {
							line = line.concat(":");
						}
						boolean updated = false;
						String[] character = line.split(":");

						// Prevent the same username from being present multiple times
						if (!usernames.add(character[PLAYERNAME_POSITION])) {
							character[PLAYERNAME_POSITION] = "_INVALID_OLD_USERNAME_'";
							updated = true;
							if (character.length < UUID_POSITION + 1 || character[UUID_POSITION].equals("NULL")) {
								continue;
							}
						}

						// Prevent the same player from being present multiple times
						if (character.length >= 12 && (!character[UUID_POSITION].isEmpty()
								&& !character[UUID_POSITION].equals("NULL") && !players.add(character[UUID_POSITION]))) {
							continue;
						}

						// If they're valid, rewrite them to the file.
						if (!updated) {
							writer.append(line).append("\r\n");
							continue;
						}

						if (updated) {
							line = new StringBuilder(org.apache.commons.lang.StringUtils.join(character, ":"))
									.append(":").toString();
						}

						writer.append(line).append("\r\n");
					}

					// Write the new file
					out = new FileWriter(usersFilePath);
					out.write(writer.toString());
				} catch (IOException e) {
					// logError("Exception while reading " + usersFilePath + " (Are
					// you sure you formatted it correctly?)" + e.toString());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							// Ignore
						}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			}
			return;
		}

		usersFile.getParentFile().mkdir();

		try {
			new File(HeadHunter.getUsersFilePath()).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean create, boolean retry) {
		// Retry is not used here.
		return loadPlayerProfile(playerName, uuid, create);
	}

	@Override
	public PlayerProfile loadPlayerProfile(UUID uuid, boolean createNew) {
		return loadPlayerProfile("", uuid, createNew);
	}


}

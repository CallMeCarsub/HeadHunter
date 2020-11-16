package tv.tirco.headhunter.database;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.MessageHandler;

public class PlayerProfileLoadingTask extends BukkitRunnable {
	private static final int MAX_TRIES = 5;
	private final Player player;
	private int attempt = 0;

	public PlayerProfileLoadingTask(Player player) {
		this.player = player;
	}

	public PlayerProfileLoadingTask(Player player, int attempt) {
		this.player = player;
		this.attempt = attempt;
	}

	// ASYNC TASK
	// DO NOT MODIFY PlayerData FROM HERE!!!
	@Override
	public void run() {
		// Is the player online?
		if (!player.isOnline()) {
			MessageHandler.getInstance().debug("Aborting profile loading recovery for " + player.getName() + " - player logged out");
			return;
		}
		// increase counter and try to load.

		attempt++;
		//VoidRPG.VoidRPGPlugin.debug("Begin loading profile for player " + player.getName() + " attempt: " + attempt);

		PlayerProfile profile = HeadHunter.db.loadPlayerProfile(player.getName(), player.getUniqueId(), true, true);
		if (profile.isLoaded()) {
			MessageHandler.getInstance().debug("Profile is loaded, applying...");
			new ApplySuccessfulProfile(new PlayerData(player, profile)).runTask(HeadHunter.plugin);
			return;
		}
		// failed max times.
		if (attempt >= MAX_TRIES) {
			MessageHandler.getInstance().log("Giving up on attempting to load the PlayerProfile for " + player.getName());

			return;
		}

		// retry
		new PlayerProfileLoadingTask(player, attempt).runTaskLaterAsynchronously(HeadHunter.plugin, 100 * attempt);
	}

	private class ApplySuccessfulProfile extends BukkitRunnable {

		private final PlayerData pData;

		private ApplySuccessfulProfile(PlayerData pData) {
			this.pData = pData;
		}

		@Override
		public void run() {
			if (!player.isOnline()) {
				//VoidRPG.log("Aborting profile loading recovery for " + player.getName() + " - player logged out");
				return;
			}

			UserManager.track(pData);
			//VoidRPG.log("Debug - Profile Applied and tracked.");

		}

	}

}

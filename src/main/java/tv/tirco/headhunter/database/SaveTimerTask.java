package tv.tirco.headhunter.database;

import org.bukkit.scheduler.BukkitRunnable;

import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.MessageHandler;


public class SaveTimerTask extends BukkitRunnable {
	@Override
	public void run() {
		// do save stuff
		// VoidCore.log("AutoSave - Saving players...");
		int count = 1;
		for (PlayerData pData : UserManager.getPlayers()) {
			new PlayerProfileSaveTask(pData.getProfile()).runTaskLaterAsynchronously(HeadHunter.plugin, count);
			count++;
		}
		if (count > 1) {
			MessageHandler.getInstance().debug("AutoSave - Saved " + (count - 1) + " players");
		}
	}
}

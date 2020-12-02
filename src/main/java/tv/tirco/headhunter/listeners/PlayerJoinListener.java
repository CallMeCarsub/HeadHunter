package tv.tirco.headhunter.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.PlayerProfileLoadingTask;
import tv.tirco.headhunter.database.UserManager;

public class PlayerJoinListener implements Listener {

	/**
	 * Monitor PlayerJoinEvents.
	 * <p>
	 * These events are monitored for the purpose of initialising player variables,
	 * as well as handling important join messages.
	 *
	 * @param event The event to monitor
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		// .debug("- PlayerJoinEvent activated..");

		if (isNPCEntity(player)) {
			return;
		}

        //Delay loading for 3 seconds in case the player has a save task running, its hacky but it should do the trick
        new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(HeadHunter.plugin, 60);
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (!UserManager.hasPlayerDataKey(player)) {
			return;
		}

        //There's an issue with using Async saves on player quit
        //Basically there are conditions in which an async task does not execute fast enough to save the data if the server shutdown shortly after this task was scheduled
		PlayerData pData = UserManager.getPlayer(player);
		pData.logout(true); //Force saving sync, as we are having weird issues with some players not getting saved.
	}
	
    public static boolean isNPCEntity(Entity entity) {
        return (entity == null || entity.hasMetadata("NPC") || entity instanceof NPC || entity.getClass().getName().equalsIgnoreCase("cofh.entity.PlayerFake"));
    }
}

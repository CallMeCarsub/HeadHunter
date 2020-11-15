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

		new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(HeadHunter.plugin, 1);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (!UserManager.hasPlayerDataKey(player)) {
			return;
		}

		PlayerData pData = UserManager.getPlayer(player);
		pData.logout(false);
	}
	
    public static boolean isNPCEntity(Entity entity) {
        return (entity == null || entity.hasMetadata("NPC") || entity instanceof NPC || entity.getClass().getName().equalsIgnoreCase("cofh.entity.PlayerFake"));
    }
}

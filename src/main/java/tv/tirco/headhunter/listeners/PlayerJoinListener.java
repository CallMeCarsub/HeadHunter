package tv.tirco.headhunter.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.database.PlayerProfileLoadingTask;

public class PlayerJoinListener {

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
		
		//Location Check - In a dungeon.
		//We do a backwards check as the playerdata isn't loaded yet.

		new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(HeadHunter.plugin, 1); // 1 Tick delay to ensure
																							// the player is marked as
																							// online before we begin
																							// loading
		
	}
	
    public static boolean isNPCEntity(Entity entity) {
        return (entity == null || entity.hasMetadata("NPC") || entity instanceof NPC || entity.getClass().getName().equalsIgnoreCase("cofh.entity.PlayerFake"));
    }
}

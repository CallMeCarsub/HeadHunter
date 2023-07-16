package tv.tirco.headhunter.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tv.tirco.headhunter.Heads;
import tv.tirco.headhunter.MessageHandler;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class PlayerBreakBlock implements Listener {
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerBreakHead(BlockBreakEvent event) {
		
		if(!(event.getBlock().getType() == Material.PLAYER_HEAD || event.getBlock().getType() == Material.PLAYER_WALL_HEAD)) {
			return;
		}

		Location loc = event.getBlock().getLocation(); 
		//Is the block the player is clicking a loaded skull?


		if(Heads.getInstance().isHead(loc)) {
			Player p = event.getPlayer();
			event.setCancelled(true);
			if(!p.hasPermission("headhunter.admin")) {
				return;
			}
			if(p.getGameMode() != GameMode.CREATIVE) {
				return;
			}
			PlayerData pData = UserManager.getPlayer(p);
			if(!pData.getAddMode()) {
				return;
			}
			event.setCancelled(false);
			Player player = event.getPlayer();
			int headID = Heads.getInstance().getHeadId(loc);
			Heads.getInstance().deleteHead(headID);
			String prefix = MessageHandler.getInstance().translateTags(MessageHandler.getInstance().prefix);
			player.sendMessage(prefix + " Head " + headID + " has been removed. Note that this might cause issues.");
		}
		
	}

}

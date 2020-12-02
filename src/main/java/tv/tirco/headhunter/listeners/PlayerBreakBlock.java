package tv.tirco.headhunter.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tv.tirco.headhunter.Heads;
import tv.tirco.headhunter.MessageHandler;

public class PlayerBreakBlock implements Listener {
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerBreakHead(BlockBreakEvent event) {
		
		if(!(event.getBlock().getType() == Material.PLAYER_HEAD || event.getBlock().getType() == Material.PLAYER_WALL_HEAD)) {
			return;
		}

		Location loc = event.getBlock().getLocation(); 
		//Is the block the player is clicking a loaded skull?
		
		if(Heads.getInstance().isHead(loc)) {
			event.setCancelled(true);
			Player player = event.getPlayer();
			//int headID = Heads.getInstance().getHeadId(loc);
			player.sendMessage(MessageHandler.getInstance().prefix 
					+" You can't break this block, as it's part of HeadHunters. It needs to be removed from the database first.");
		}
		
	}

}

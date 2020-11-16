package tv.tirco.headhunter.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import tv.tirco.headhunter.Heads;

public class PlayerPlaceHead implements Listener {
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerPlaceHead(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		if(!p.hasPermission("headhunter.admin")) {
			return;
		}
		
		if(p.getGameMode() != GameMode.CREATIVE) {
			return;
		}
		
		//Check block type
		Block block = event.getBlockPlaced();
		if(!block.getType().equals(Material.PLAYER_HEAD)) {
			return;
		}
		
		//Check if item in hand has correct lore?
		//TODO
		
		//Add Data to HM
		Location loc = block.getLocation();
		
		if(Heads.getInstance().addHead(loc)) {
			//Announce
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(player.hasPermission("headhunter.admin")) {
					player.sendMessage(p.getName() + " Just placed a new HeadHunter skull at " 
				+ loc.getBlockX() +", " 
				+ loc.getBlockY() +", "
				+ loc.getBlockZ() +". ");
				}
			}
		} else {
			p.sendMessage("Failed to add a new skull to this location, as it already exists.");
		}
		
		return;

		
	}
	

}

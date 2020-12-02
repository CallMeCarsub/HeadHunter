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
import tv.tirco.headhunter.MessageHandler;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

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
		if(!block.getType().equals(Material.PLAYER_HEAD) && !block.getType().equals(Material.PLAYER_WALL_HEAD)) {
			return;
		}
		
		//Check if player is in add mode.
		if(!UserManager.hasPlayerDataKey(p)) {
			return;
		}
		PlayerData pData = UserManager.getPlayer(p);
		if(!pData.getAddMode()) {
			return;
		}
		
		//Add Data to HM
		Location loc = block.getLocation();
		int ID = Heads.getInstance().addHead(loc);
		if(ID > -1) {
			//Announce
			for(Player player : Bukkit.getServer().getOnlinePlayers()) {
				if(player.hasPermission("headhunter.admin")) {
					
					player.sendMessage(MessageHandler.getInstance().prefix + " "+ p.getName() + " Just placed a new HeadHunter skull at " 
				+ loc.getBlockX() +", " 
				+ loc.getBlockY() +", "
				+ loc.getBlockZ() +". ID: " + ID );
				}
			}
			//Send edit messages:
			MessageHandler.getInstance().sendEditCommands(p, ID);
		} else {
			p.sendMessage("Failed to add a new skull to this location, as it already exists.");
		}
		
		return;

		
	}
	

}

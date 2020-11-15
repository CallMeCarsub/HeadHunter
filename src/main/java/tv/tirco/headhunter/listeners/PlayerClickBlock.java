package tv.tirco.headhunter.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import tv.tirco.headhunter.Heads;
import tv.tirco.headhunter.MessageHandler;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class PlayerClickBlock implements Listener {
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerClickHead(PlayerInteractEvent event) {
		
		Player p = event.getPlayer();
		
		//Not right clicking a block? Then we don't care! GTFO
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		//Ignore if not mainhand
		if(!event.getHand().equals( EquipmentSlot.HAND)) {
			return;
		}
		
		if(!UserManager.hasPlayerDataKey(p)) {
			return;
		} 
		PlayerData pData = UserManager.getPlayer(p);
		
		Location loc = event.getClickedBlock().getLocation(); 
		//Is the block the player is clicking a loaded skull?
		
		if(Heads.getInstance().isHead(loc)) {
			int headID = Heads.getInstance().getHeadId(loc);
			//int maxHeads = Heads.getInstance().getHeadAmount();
			//TODO save to player
			if(pData.hasFound(headID)) {
				p.sendMessage("You have already found this Skull.");
				return;
			}
			
			pData.find(headID);
			
			//TODO notify to player if new.
			String s = "&aYou have found &6<found>&a out of &6<max>&a heads.";
			
			p.sendMessage(MessageHandler.getInstance().translateTags(s, p));
			
		} else {
			return;
		}
		
	}

}

package tv.tirco.headhunter.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import tv.tirco.headhunter.Heads;
import tv.tirco.headhunter.MessageHandler;
import tv.tirco.headhunter.config.Config;
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
		
		//Perm check
		if(Config.getInstance().getNeedPermToHunt() && !p.hasPermission("headhunter.basic")) {
			return;
		}
		
		PlayerData pData = UserManager.getPlayer(p);
		
		Location loc = event.getClickedBlock().getLocation(); 
		//Is the block the player is clicking a loaded skull?
		
		if(Heads.getInstance().isHead(loc)) {
			int headID = Heads.getInstance().getHeadId(loc);
			if(pData.hasFound(headID)) {
				//Send Already Found Message.
				String s = MessageHandler.getInstance().translateTags(Config.getInstance().getMessageAlreadyFound(), p);
				p.sendMessage(s);
				return;
			}
			
			pData.find(headID);
			//Send Counting Message
			String s = MessageHandler.getInstance().translateTags(Config.getInstance().getMessageCount(), p);
			p.sendMessage(s);
			p.playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 2f);
			
			//Player has found all!
			if(pData.getAmountFound() >= Heads.getInstance().getHeadAmount()) {
				String announce = MessageHandler.getInstance().translateTags(Config.getInstance().getMessageAnnounceFindAll(), p);
				if(Config.getInstance().getAnnounceFindAll()) {
					//Send to all players.
					for(Player player : Bukkit.getOnlinePlayers()) {
						player.sendMessage(announce);
						player.playSound(player.getLocation(), Sound.AMBIENT_BASALT_DELTAS_MOOD, 2, 2);
					}
					
				} else {
					//Only send to finding player
					p.sendMessage(announce);
				}
				p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2, 2);
			}
			
			//minecraft:block.beacon.power_select Pitch 2
			//ambient.basalt_deltas.mood -> Big Thud. Pitch 2 & 0.1
			//minecraft:ui.toast.challenge_complete. Pitch 2
			
		} else {
			return;
		}
		
	}

}

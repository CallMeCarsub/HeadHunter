package tv.tirco.headhunter;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import tv.tirco.headhunter.config.Config;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;


public class ParticleRunnable extends BukkitRunnable {

	public ParticleRunnable() {
		
	}
	
	@Override
	public void run() {
		//Also remove players on quit.
		for(PlayerData pData : UserManager.getPlayers()) {
			Player p = pData.getPlayer();
			//Don't show particles if they don't have hunting perm.
			if(Config.getInstance().getNeedPermToHunt() && !p.hasPermission("headhunter.basic")) {
				continue;
			}
			
			List<Location> locs = Heads.getInstance().getHeadsNear(p.getLocation(),
					10.00, pData.getFoundIDs());
			for(Location l : locs) {
				p.spawnParticle(Particle.END_ROD, l.getX()+0.5, l.getY()+0.5, l.getZ()+0.5, 1, 0.4, 0.3, 0.4, 0);
			}

		}
	}
	     
}


//for(Integer i: Heads.getInstance().getHeads().keySet()) {
//if(!pData.hasFound(i.intValue())){
//	Location l = Heads.getInstance().getHeads().get(i);
//	if(l.getWorld() == null || l.getWorld() != p.getWorld()) {
//		if(i > 30 && i < 40) {
//			if(l.getWorld() == null) {
//				p.sendMessage("l.getWorld == null");
//			} else {
//				p.sendMessage("p.getWorld != l.getWorld");
//			}
//		}
//
//		return;
//	}
//	if(l.distance(p.getLocation()) < 10.00) {
//
//		
//		p.spawnParticle(Particle.END_ROD, l.getX()+0.5, l.getY()+0.5, l.getZ()+0.5, 1, 0.4, 0.3, 0.4, 0);
//	} else {
//		if(i > 30 && i < 40) {
//			p.sendMessage("Distance from head " + i + " = " + l.distance(p.getLocation()));
//		}
//	}
//}
//
//}
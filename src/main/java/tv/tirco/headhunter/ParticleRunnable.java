package tv.tirco.headhunter;

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
			
			for(Integer i: Heads.getInstance().getHeads().keySet()) {
				if(!pData.hasFound(i.intValue())){
					Location l = Heads.getInstance().getHeads().get(i);
					if(l.distance(p.getLocation()) < 10.00) {
						p.spawnParticle(Particle.END_ROD, l.getX()+0.5, l.getY()+0.5, l.getZ()+0.5, 1, 0.3, 0.3, 0.3, 0);
					}
				}
				
			}
		}
	}
	
	
	
	
	    
	    
	    

	             
	      
}

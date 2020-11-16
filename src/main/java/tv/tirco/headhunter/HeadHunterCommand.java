package tv.tirco.headhunter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

public class HeadHunterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if(!(sender instanceof Player)) {
    		sender.sendMessage("This command can only be used by players.");
    		return true;
    	}
    	
    	Player player = (Player) sender;
    	
    	if(!UserManager.hasPlayerDataKey(player)) {
    		//TODO message.
    		return true;
    	}
    	PlayerData pData = UserManager.getPlayer(player);
    	player.sendMessage("You have found " + pData.getAmountFound() + "/" + Heads.getInstance().getHeadAmount() + " Heads.");

        return true;
    }
}

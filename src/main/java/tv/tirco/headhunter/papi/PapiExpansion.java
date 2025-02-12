package tv.tirco.headhunter.papi;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import tv.tirco.headhunter.HeadHunter;
import tv.tirco.headhunter.Heads;
import tv.tirco.headhunter.database.PlayerData;
import tv.tirco.headhunter.database.UserManager;

/**
 * This class will be registered through the register-method in the 
 * plugins onEnable-method.
 */
public class PapiExpansion extends PlaceholderExpansion {

    private HeadHunter plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public PapiExpansion(HeadHunter plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "headhunter";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        //if(player == null){
        //    return "";
        //}

        // %headhunter_player_found_amount%
        if(identifier.equals("player_found_amount")){
        	if(player == null) {
        		return null;
        	}
        	if(!UserManager.hasPlayerDataKey(player)) {
        		return null;
        	}
        	PlayerData pData = UserManager.getPlayer(player);
        	int amount = pData.getAmountFound();
            return ""+amount;
        }

        // %headhunter_maxheads%
        if(identifier.equals("maxheads")){
        	return Heads.getInstance().getHeadAmount() + "";
        }
        
        
        // %headhunter_stats_top_13%
        if(identifier.matches("stats_top_[0-9][0-9]*")) {
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Sending PlaceHolder Data");
        	int number = Integer.valueOf(identifier.split("_")[2]) - 1;
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Number = " + number);
        	UUID uuid = Heads.getInstance().getTopPlayerPlayer(number); 
        	if(uuid == null) {
        		//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- UUID was null. Defaulting to no data");
        		return "No data";
        	}
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- UUID was not null. " + uuid.toString());
        	OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(uuid);
        	int score = Heads.getInstance().getTopPlayerScore(oPlayer);
        	String name = oPlayer.getName();
        	if(name == null) {
        		name = "Unknown Player";
        	}
        	return name + " -> " + score;
        }
        
        if(identifier.matches("stats_top_name_[0-9][0-9]*")) {
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Sending PlaceHolder Data");
        	int number = Integer.valueOf(identifier.split("_")[3]) - 1;
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Number = " + number);
        	UUID uuid = Heads.getInstance().getTopPlayerPlayer(number); 
        	if(uuid == null) {
        		//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- UUID was null. Defaulting to no data");
        		return "No data";
        	}
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- UUID was not null. " + uuid.toString());
        	OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(uuid);
        	String name = oPlayer.getName();
        	if(name == null) {
        		name = "Unknown Player";
        	}
        	return name;
        }
        if(identifier.matches("stats_top_score_[0-9][0-9]*")) {
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Sending PlaceHolder Data");
        	int number = Integer.valueOf(identifier.split("_")[3]) - 1;
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- Number = " + number);
        	UUID uuid = Heads.getInstance().getTopPlayerPlayer(number); 
        	if(uuid == null) {
        		//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- UUID was null. Defaulting to no data");
        		return "No data";
        	}
        	//MessageHandler.getInstance().debug(ChatColor.GOLD + " -- UUID was not null. " + uuid.toString());
        	OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(uuid);
        	int score = Heads.getInstance().getTopPlayerScore(oPlayer);
        	return ""+score;
        }
 
        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%) 
        // was provided
        return null;
    }
    
    

}

## Commands
# * /headhunter or /hh
* /hh list  -- Shows a list of all heads you have found, + allows you to hover them for hints.
* /hh count -- Shows the amount of heads you have found.
* /hh top -- Shows the leaderboard.
* /hh help

# * /headhunteradmin or /hha
* /hha help
* /hha find (id) -- Gives you the coords and a clickable button to TP to it.
* /hha delete (id) -- Deletes the specified ID from database. Block is not deleted.
* /hha sethint (id) (msg) -- Sets a message as the hint for the specified ID.
* /hha add on/off. toggles addingmode. Skulls placed will automatically be added to the list.
* /hha debug -- Toggles debuging (Until reboot)
* /hha notifyadmins -- Toggles debugmessages being sendt to admings. (Until reboot)

## Permissions
- headhunter.basic -- Allows use of base command. If config has it enabled, it is also required to collect heads.
- headhunter.admin -- Allows use of adding and removing heads.

## Placeholders
* %headhunter_player_found_amount% - shows the amount of heads the player has found.
* %headhunter_maxheads% - shows the amount of heads that can be found.
* %headhunter_stats_top_#% - replace # with a number. Displays the name and score of the player at the stated rank. Do not use a number higher than how many people are stated to be saved in the config file.
* %headhunter_stats_top_name_#% - replace # with a number. Shows the name of the player at that rank.
* %headhunter_stats_top_score_#% - replace # with a number. Shows the score of the player at that rank.


## Credits

* Kudos to Oracle for the Eclipse IDE and Apache for Maven.
* Thank you to the Bukkit and Spigot communities for providing a better Minecraft server wrapper

## License

Zlib was chosen as the basis for this project (BukkitPlugin) as it is highly permissive and easy for people to understand. The license has only been modified for this project to reflect authorship and creation year.

Copyright (c) 2020 Tirco

This software is provided 'as-is', without any express or implied
warranty. In no event will the authors be held liable for any damages
arising from the use of this software.

Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:

1. The origin of this software must not be misrepresented; you must not
claim that you wrote the original software. If you use this software
in a product, an acknowledgment in the product documentation would be
appreciated but is not required.

2. Altered source versions must be plainly marked as such, and must not be
misrepresented as being the original software.

3. This notice may not be removed or altered from any source
distribution.

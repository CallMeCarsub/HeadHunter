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

## License

Copyright (c) 2021 Tirco
HeadHunter is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

HeadHunter is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

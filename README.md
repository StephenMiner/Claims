# Claims

Claims
And how to use them
The extensive guide
The commands and what they do:
/giveWand 
Purpose: This command is mainly meant for admin use but this will directly give a player the wand to claim regions free of charge
Permission: claims.commands.givewand

/claimShovel
Purpose: This command is meant for general player use and requires a golden shovel to be held by the player in order for them to receive a wand to claim regions.
Permission: claims.commands.shovel

/claimBlocks
Purpose: Tells the sender how many claim-blocks they have if any

/claimsReload 
Purpose: Reloads the configuration files. Important for when you edit values in the settings.yml
Permission: claims.commands.reload

/claims [claim-id] [sub-command] [arg]
Purpose: This command is for the general use of players and so does not have a permission attached to it. This allows players to interact with their owned regions through the sub commands listed below

showBorder [true/false] 
This will display a border around the specified claim 
allowExplosions [true/false]
Defines whether explosians such as tnt will be allowed in a claim (default = false)
allowInteraction [true/false]
Defines whether interactions such as using fencegates will be allowed in a claim (default = false)
allowEntityInteraction [true/false]
Defines whether interactions such as using armorstands will be allowed in a claim (default = false)
allowLiquidFlow [true/false]
Defines whether liquids will be able to flow into the claim (default = false)
delete [confirm]
Deletes the claim. Must type confirm as the next argument as shown above!
trust [player]
Trusts a player in the region orriding all editing restrictions
untrust [player] 
Untrusts a player in a claim

/forceDelete [owner] [name]
Purpose: This command will force the deletion of a region not owned by you!
Permission: "claims.commands.forcedelete"

How Does it work?
When you have a wand which is a special item you get from /giveWand or /claimShovel you simply left click to set one corner right click to set another. Then the plugin will make sure you have enough claim blocks and if you do you will be asked to type a region name out in chat. This is how the plugin will save your region and you identify it. 

The player can then use the variety of /claims sub-commands to customize their regions as all protections are on by default 

An important thing is that operators or people with the “claims.regions.override” permission will override region protections but will get screamed at by the server to make sure that this person knows they are messing with a region. This is more so so an admin doesn’t accidentally edit a region when they aren’t supposed to.

What do regions protect against?
No matter what regions will prevent entities from being killed by untrusted players as well as blocks from burning and chests/containers from being used. If the settings are left as they are by default, explosions, block interactions like using doors, using armorstands, and liquids flowing into a region, are all protected by the plugin.


Now onto the settings.yml
This part is pretty simple there are two things to think about. And they are pretty straight forward. The “hours-between-addition” will define the hours in between awarding players x amount of claimblocks as defined by “claim-blocks-per.” Note that if you want to have less than 1 hour as a value, you are allowed to use decimals.


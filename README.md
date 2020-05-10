# Here are the commands:
/locator on - Allows for locator to run (On by default).
/locator off - Disallows for locator to run.
/locator get - Player recieves the 'Deserter Location' compass item.
/locator addPlayer [playername] - Manually adds a player to the deserters list.
/locator removePlayer [playername] - Manually removes a player to the deserters list.
/locator errorPer [0.0 - 100.0] - Allows for the compass to not work all the time (0% error by Default).
/locator setLocation - Sets the location to command issuer's position to see who deserts ((0,0,0) by Default).
/locator desertDist [# of Blocks] - Sets the radius around 'setLocation' before a player is considered a deserter (100 Blocks by Default).
/locator listDeserters - Lists all current deserters.
/locator help - Do I need to tell you what this does?
# How to set it up in-game:
1. go to a spot that will be the center point for the deserter detection and type “/locator setLocation”
2. Type “/locator desertDist” to set the radius around the point chosen in the previous step for when players run outside of the radius, they are deserting.
3. When you want to give a player the compass to track deserters type "/locator get" it will tell you what to do.

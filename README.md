# Beehave

A server-side only fabric mod that provide information about beehives and bees.  

This is a Fabric mod, [Fabric API](https://modrinth.com/mod/fabric-api) is required.

Recommend to use along with [MoreBeeInfo](https://modrinth.com/mod/morebeeinfo).

## Main feature

- Right-click on a beehive block or bee nest block,
bees' information will be printed into chat HUD.
Including: bees' count, is a bee adult or baby,
how many ticks has a bee stayed in the hive,
and how many ticks in total need to be occupied before leave the hive.
- Right-click on a bee entity, bee's information will be printed into chat HUD.
If the bee got a hive to enter, position of the hive will be displayed;
if not, the bee will be marked "Homeless"

All the operation above will be automatically filtered
if the player is holding items that got other functions.

## Plans

- Add blacklist & whitelist that limits the items held by players to display the information
- Read custom name NBT in BeehiveBlockEntity so that a bee's custom name can be displayed
- Configurations and commands for language switch
- More language support
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
  ![](https://cdn-raw.modrinth.com/data/e0YVwkW5/images/49bcad6da7d3c05e070e416c277e14982ad54a61.png)
- Right-click on a bee entity, bee's information will be printed into chat HUD.
  If the bee got a hive to enter, position of the hive will be displayed;
  if not, the bee will be marked "Homeless"
  ![](https://cdn-raw.modrinth.com/data/e0YVwkW5/images/2956e8851b0db469131875c78ae62080abcd45cd.png)

All the operation above will be automatically filtered
if the player is holding items that got other functions.

## Plans

- Add blacklist & whitelist that limits the items held by players to display the information
- More language support
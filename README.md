# Beehave

A server-side only mod that provide information about beehives and bees.

For Fabric version, [Fabric API](https://modrinth.com/mod/fabric-api) is required.

For forge version, view [other branches](https://github.com/Nova-Committee/beehave/branches) maintained by [MikhailTapio](https://github.com/MikhailTapio)

Recommend to use along with [MoreBeeInfo](https://modrinth.com/mod/morebeeinfo).

Thank [strrraicato_](https://space.bilibili.com/314823790) for the name "Beehave" provided by them.

## Main feature

- Right-click on a beehive block or bee nest block,
  bees' information will be printed into the chat HUD.
  Included: bees' count, is a bee adult or baby,
  how many ticks has a bee stayed in the hive,
  and how many ticks are needed to be occupied before leaving the hive.
  ![](https://cdn-raw.modrinth.com/data/e0YVwkW5/images/49bcad6da7d3c05e070e416c277e14982ad54a61.png)
- Right-click on a bee entity, bee's information will be printed into chat HUD.
  If the bee gets a hive to enter, the position of the hive will be displayed;
  if not, the bee will be marked "Homeless"
  ![](https://cdn-raw.modrinth.com/data/e0YVwkW5/images/2956e8851b0db469131875c78ae62080abcd45cd.png)

All the operations above will be automatically filtered
if the player is holding items that got other functions.

## Plans

- Add a interface to control a whitelist that limits the items held by players to display the information
- More language support
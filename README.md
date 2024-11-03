# Beehave

A server-side only mod that provide information about beehives and bees.

For Fabric version, [Fabric API](https://modrinth.com/mod/fabric-api) is required.

For forge version, view [other branches](https://github.com/Nova-Committee/beehave/branches) maintained by [MikhailTapio](https://github.com/MikhailTapio)

Recommended to use along with [MoreBeeInfo](https://modrinth.com/mod/morebeeinfo).

Thank [strrraicato_](https://space.bilibili.com/314823790) for the name "Beehave" provided by them.

## Main feature

- Right-click on a beehive block or bee nest block,
  bees' information will be printed into the chat HUD.
  Included: bees' count, is a bee adult or baby,
  how many ticks has a bee stayed in the hive,
  and how many ticks are needed to be occupied before leaving the hive.
  If a row is highlighted, it means corresponding bee is available to come out,
  while not yet due to the environment.
  ![](https://cdn.modrinth.com/data/e0YVwkW5/images/2bfbd00087f1ee63881445520d4a52c35a26b409.png)
- Right-click on a bee entity, bee's information will be printed into chat HUD.
  If the bee gets a hive to enter, the position of the hive will be displayed;
  if not, the bee will be marked "Homeless".
  And particles will be generated to indicate the position of the beehive.
  ![](https://cdn.modrinth.com/data/e0YVwkW5/images/13888884f7d9b6d21bf23aa6c2b44a426bac0bcd.png)

All the operations above will be automatically filtered
if the player is holding items that got other functions.

## Plans
- Add a blacklist & whitelist that limits the items held by players to display the information
- More language support

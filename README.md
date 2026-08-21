# ArcParties

ArcParties is a Minecraft Spigot plugin that adds a party system.

While this plugin does function on its own, there's no built-in use for parties. It is expected that you will use it as a library for other plugins.

# Example Usage
in your JavaPlugin body:
```java
public static PartyAPI partyAPI;
```
In your onEnable():
```java
ArcParties partyPlugin = (ArcParties) Bukkit.getPluginManager().getPlugin("ArcParties");
partyAPI = partyPlugin.getAPI();
```
You can now query data about the player's party, such as in this example scoreboard:
```java
Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

Objective objective = scoreboard.registerNewObjective("scoreboard", Criteria.DUMMY, "My Cool Server");
objective.setDisplaySlot(DisplaySlot.SIDEBAR);
Optional<Party> optionalParty = partyAPI.getPlayerParty(player.getUniqueId());
if  (optionalParty.isPresent()) {
    Party party = optionalParty.get();
    Score score = objective.getScore("§cParty Leader: " + Bukkit.getOfflinePlayer(party.getLeader()).getName());
    score.setScore(3);
}

player.setScoreboard(scoreboard);
```
Additionally, there are custom Bukkit events to listen for, These being PartyCreateEvent, PartyInviteEvent, PartyDisbandEvent, PartyJoinEvent, PartyLeaveEvent, and PartyTransfer event
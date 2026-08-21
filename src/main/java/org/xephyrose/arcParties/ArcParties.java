package org.xephyrose.arcParties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ArcParties extends JavaPlugin {
    private static final Map<UUID, Party> playerPartyMap = new HashMap<>(); // Player -> Party
    static final Map<UUID, ArrayList<UUID>> pendingInvites = new HashMap<>(); // Inviter -> Invited

    private ArcPartiesAPI api;
    private final Commands commands = new Commands();
    final ConfigManager configManager = new ConfigManager();

    @Override
    public void onEnable() {
        api = new ArcPartiesAPI(this);
        saveDefaultConfig();

        this.getCommand("party").setExecutor(commands);
        this.getCommand("party").setTabCompleter(commands);
        this.getCommand("arcparties").setExecutor(commands);
        this.getCommand("arcparties").setTabCompleter(commands);
    }

    public PartyAPI getAPI() {
        return api;
    }

    Party createParty(Player leader) {
        Party party = new Party(leader.getUniqueId());
        playerPartyMap.put(leader.getUniqueId(), party);
        PartyCreateEvent event = new PartyCreateEvent(leader, party);
        Bukkit.getPluginManager().callEvent(event);
        return party;
    }

    void addToParty(UUID player, Party party) {
        party.addMember(player);
        playerPartyMap.put(player, party);
    }

    void removeFromParty(UUID player) {
        Party party = playerPartyMap.get(player);
        if (party == null) return;

        party.removeMember(player);
        playerPartyMap.remove(player);
    }

    void disbandParty(Party party) {
        for (UUID member : party.getMembers()) {
            playerPartyMap.remove(member);
        }
    }

    Party getPlayerParty(UUID player) {
        return playerPartyMap.get(player);
    }

    boolean hasParty(UUID player) {
        return playerPartyMap.containsKey(player);
    }
}
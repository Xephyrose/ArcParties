package org.xephyrose.arcParties;

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

    Party createParty(UUID leader) {
        Party party = new Party(leader);
        playerPartyMap.put(leader, party);
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

        if (party.getSize() == 0 || (party.isLeader(player) && party.getSize() == 1)) {
            disbandParty(party);
        } else if (party.isLeader(player)) {
            UUID newLeader = party.getMembers().iterator().next();
        }
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
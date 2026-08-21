package org.xephyrose.arcParties;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class ArcPartiesAPI implements PartyAPI {
    private final ArcParties plugin;

    public ArcPartiesAPI(ArcParties plugin) {
        this.plugin = plugin;
    }

    @Override
    public Party createParty(Player leader) {
        return plugin.createParty(leader);
    }

    @Override
    public Optional<Party> getPlayerParty(UUID player) {
        Party party = plugin.getPlayerParty(player);
        return Optional.ofNullable(party);
    }

    @Override
    public boolean hasParty(UUID player) {
        return plugin.hasParty(player);
    }

    @Override
    public boolean addToParty(UUID player, Party party) {
        if (plugin.hasParty(player)) {
            return false;
        }

        plugin.addToParty(player, party);
        return true;
    }

    @Override
    public void removeFromParty(UUID player) {
        plugin.removeFromParty(player);
    }
}
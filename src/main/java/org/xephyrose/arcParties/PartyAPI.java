package org.xephyrose.arcParties;

import java.util.Optional;
import java.util.UUID;

public interface PartyAPI {
    /**
     * Creates a new party with leader as the leader
     * @param leader The party leader
     * @return The new party
     */
    Party createParty(UUID leader);

    /**
     * Gets the party a player is in
     * @param player The player to check
     * @return Optional of their party if they're in one, empty otherwise
     */
    Optional<Party> getPlayerParty(UUID player);

    /**
     * Checks if a player is in a party
     * @param player The player to check
     */
    boolean hasParty(UUID player);

    /**
     * Adds a player to a party
     * @return true if successful, false if not
     */
    boolean addToParty(UUID player, Party party);

    /**
     * Removes a player from their party
     */
    void removeFromParty(UUID player);
}
package org.xephyrose.arcParties;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.UUID;

public class PartyDisbandedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final HashSet<UUID> players;

    public PartyDisbandedEvent(HashSet<UUID> players) {
        this.players = players;
    }

    public HashSet<UUID> getPlayers() {
        return players;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
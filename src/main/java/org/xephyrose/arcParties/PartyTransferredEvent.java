package org.xephyrose.arcParties;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyTransferredEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player oldLeader;
    private final Player newLeader;
    private final Party party;

    public PartyTransferredEvent(Player oldLeader, Player newLeader, Party party) {
        this.oldLeader = oldLeader;
        this.newLeader = newLeader;
        this.party = party;
    }

    public Player getOldLeader() {
        return oldLeader;
    }

    public Player getNewLeader() {
        return newLeader;
    }

    public Party getParty() {
        return party;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

package org.xephyrose.arcParties;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyTransferEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;
    private final Player currentLeader;
    private final Player newLeader;
    private final Party party;

    public PartyTransferEvent(Player currentLeader, Player newLeader, Party party) {
        this.currentLeader = currentLeader;
        this.newLeader = newLeader;
        this.party = party;
    }

    public Player getCurrentLeader() {
        return currentLeader;
    }

    public Player getNewLeader() {
        return newLeader;
    }

    public Party getParty() {
        return party;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

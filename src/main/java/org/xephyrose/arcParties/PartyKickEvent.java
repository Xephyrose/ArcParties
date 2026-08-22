package org.xephyrose.arcParties;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyKickEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;
    private final Player kicker;
    private final Player kicked;
    private final Party party;

    public PartyKickEvent(Player kicker, Player kicked, Party party) {
        this.kicker = kicker;
        this.kicked = kicked;
        this.party = party;
    }

    public Player getKicker() {
        return kicker;
    }

    public Player getKicked() {
        return kicked;
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
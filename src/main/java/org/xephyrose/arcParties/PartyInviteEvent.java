package org.xephyrose.arcParties;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyInviteEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;
    private final Player inviter;
    private final Player invited;

    public PartyInviteEvent(Player inviter, Player invited) {
        this.inviter = inviter;
        this.invited = invited;
    }

    public Player getInviter() {
        return inviter;
    }

    public Player getInvited() {
        return invited;
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
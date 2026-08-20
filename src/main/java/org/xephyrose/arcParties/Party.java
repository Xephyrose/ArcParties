package org.xephyrose.arcParties;

import java.util.HashSet;
import java.util.UUID;

public class Party {
    private UUID leader;
    private HashSet<UUID> members;

    public Party(UUID leader) {
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
    }

    public void addMember(UUID player) {
        members.add(player);
    }

    public void removeMember(UUID player) {
        members.remove(player);
    }

    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }

    public boolean isMember(UUID player) {
        return members.contains(player);
    }

    public int getSize() {
        return members.size();
    }

    public HashSet<UUID> getMembers() {
        return members;
    }

    public UUID getLeader() {
        return leader;
    }

    public void transferLeadership(UUID newLeader) {
        leader = newLeader;
    }
}
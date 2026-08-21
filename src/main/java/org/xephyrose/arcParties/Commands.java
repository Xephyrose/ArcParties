package org.xephyrose.arcParties;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Commands implements CommandExecutor, TabCompleter {

    private ArcParties getPlugin() {
        return ArcParties.getPlugin(ArcParties.class);
    }

    public void announceToParty(Party party, String announcement) {
        for (UUID memberId : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(announcement);
            }
        }
    }

    public void announceToParty(Party party, String announcement, Player cause_by) {
        for (UUID memberId : party.getMembers()) {
            if (!memberId.equals(cause_by.getUniqueId())) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && member.isOnline()) {
                    member.sendMessage(announcement);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // FIXME
//        if (command.getName().equalsIgnoreCase("arcparties")) {
//            if (args[0].equalsIgnoreCase("reload"))
//            {
//                getPlugin().configManager.reload();
//            }
//        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("party")) {
            if (args.length == 0) {
                player.sendMessage("§cUsage: /party <invite/join/kick/leave/list/transfer/disband>");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "invite" -> handleInvite(player, args);
                case "join" -> handleJoin(player, args);
                case "kick" -> handleKick(player, args);
                case "leave" -> handleLeave(player);
                case "list" -> handleList(player);
                case "transfer" -> handleTransfer(player, args);
                case "disband" -> handleDisband(player, args);
                default -> player.sendMessage("§cUnknown arguments. Use /party <invite|join|kick|transfer>");
            }
        }
        return true;
    }

    private void handleInvite(Player inviter, String[] args) {
        if (args.length < 2) {
            inviter.sendMessage("§cUsage: /party invite <player>");
            return;
        }

        Party existingParty = getPlugin().getPlayerParty(inviter.getUniqueId());
        if (existingParty != null && !existingParty.isLeader(inviter.getUniqueId())) {
            inviter.sendMessage("§cOnly the party leader can invite players!");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            inviter.sendMessage("§cPlayer not found!");
            return;
        }

        if (getPlugin().hasParty(target.getUniqueId())) {
            inviter.sendMessage("§cThat player is already in a party!");
            return;
        }

        PartyInviteEvent event = new PartyInviteEvent(inviter, target);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {return;}

        ArrayList<UUID> invites = ArcParties.pendingInvites.getOrDefault(target.getUniqueId(), new ArrayList<>());
        if (invites.contains(inviter.getUniqueId())) {
            inviter.sendMessage("§cThat player has already been invited!");
            return;
        }

        invites.add(inviter.getUniqueId());
        ArcParties.pendingInvites.put(target.getUniqueId(), invites);

        inviter.sendMessage("§aInvited " + target.getName() + " to the party!");
        target.sendMessage("§6" + inviter.getName() + " §ainvited you to their party! Use §6/party join " + inviter.getName() + " §ato join!");

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            ArrayList<UUID> currentInvites = ArcParties.pendingInvites.get(target.getUniqueId());
            if (currentInvites != null) {
                currentInvites.remove(inviter.getUniqueId());
                if (currentInvites.isEmpty()) {
                    ArcParties.pendingInvites.remove(target.getUniqueId());
                }
            }
        }, 20 * 60);
    }

    private void handleJoin(Player joiner, String[] args) {
        if (args.length < 2) {
            joiner.sendMessage("§cUsage: /party join <player>");
            return;
        }

        if (getPlugin().hasParty(joiner.getUniqueId())) {
            joiner.sendMessage("§cYou are already in a party!");
            return;
        }

        ArrayList<UUID> invites = ArcParties.pendingInvites.get(joiner.getUniqueId());
        if (invites == null || invites.isEmpty()) {
            joiner.sendMessage("§cYou don't have any pending invites!");
            return;
        }

        Player inviter = Bukkit.getPlayer(args[1]);
        if (inviter == null) {
            joiner.sendMessage("§cPlayer not found!");
            return;
        }

        if (!invites.contains(inviter.getUniqueId())) {
            joiner.sendMessage("§cYou don't have an invite from that player!");
            return;
        }

        invites.remove(inviter.getUniqueId());
        if (invites.isEmpty()) {
            ArcParties.pendingInvites.remove(joiner.getUniqueId());
        }

        Party party = getPlugin().getPlayerParty(inviter.getUniqueId());

        if (party == null) {
            party = getPlugin().createParty(inviter);
        } else {
            if (!party.isMember(inviter.getUniqueId())) {
                joiner.sendMessage("§cThat player is no longer a member of that party!");
                return;
            }
        }

        PartyJoinEvent event = new PartyJoinEvent(joiner, party);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {return;}

        getPlugin().addToParty(joiner.getUniqueId(), party);

        joiner.sendMessage("§aYou joined " + inviter.getName() + "'s party!");
        announceToParty(party, "§6" + joiner.getName() + " §ahas joined the party!", joiner);

    }

    private void handleKick(Player kicker, String[] args) {
        if (args.length < 2) {
            kicker.sendMessage("§cUsage: /party kick <player>");
            return;
        }

        Party party = getPlugin().getPlayerParty(kicker.getUniqueId());
        if (party == null) {
            kicker.sendMessage("§cYou are not in a party!");
            return;
        }

        if (!party.isLeader(kicker.getUniqueId())) {
            kicker.sendMessage("§cOnly the party leader can kick members!");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            kicker.sendMessage("§cPlayer not found!");
            return;
        }

        if (target.getUniqueId().equals(kicker.getUniqueId())) {
            kicker.sendMessage("§cYou cannot kick yourself!");
            return;
        }

        if (!party.isMember(target.getUniqueId())) {
            kicker.sendMessage("§cThat player is not in your party!");
            return;
        }

        PartyKickEvent event = new PartyKickEvent(kicker, target, party);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {return;}

        getPlugin().removeFromParty(target.getUniqueId());

        target.sendMessage("§cYou have been kicked from the party!");
        kicker.sendMessage("§aKicked " + target.getName() + " from the party!");
        announceToParty(party, "§6" + target.getName() + " §chas been kicked from the party!", target);
    }

    private void handleLeave(Player player) {
        Party party = getPlugin().getPlayerParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage("§cYou are not in a party!");
            return;
        }
        if (party.isLeader(player.getUniqueId())) {
            player.sendMessage("§cYou must transfer or disband the party to leave it!");
            return;
        }

        PartyLeaveEvent event = new PartyLeaveEvent(player, party);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {return;}

        getPlugin().removeFromParty(player.getUniqueId());

        player.sendMessage("§cYou left the party!");
        announceToParty(party, "§6" + player.getName() + " §chas left the party!", player);
    }

    private void handleList(Player player) {
        Party party = getPlugin().getPlayerParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage("§cYou are not in a party!");
            return;
        }

        StringBuilder playerList = new StringBuilder();
        Set<UUID> members = party.getMembers();

        for (UUID memberId : members) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                if (!playerList.isEmpty()) {
                    playerList.append(", ");
                }
                playerList.append(member.getName());
            }
        }

        String listMessage = "Party members: " + (!playerList.isEmpty() ?
                playerList.toString() : "No members online");
        player.sendMessage(listMessage);
    }

    private void handleTransfer(Player transferer, String[] args) {
        if (args.length < 2) {
            transferer.sendMessage("§cUsage: /party transfer <player>");
            return;
        }

        Party party = getPlugin().getPlayerParty(transferer.getUniqueId());
        if (party == null) {
            transferer.sendMessage("§cYou are not in a party!");
            return;
        }

        if (!party.isLeader(transferer.getUniqueId())) {
            transferer.sendMessage("§cOnly the party leader can transfer leadership!");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            transferer.sendMessage("§cPlayer not found!");
            return;
        }

        if (target.getUniqueId().equals(transferer.getUniqueId())) {
            transferer.sendMessage("§cYou are already the leader!");
            return;
        }

        if (!party.isMember(target.getUniqueId())) {
            transferer.sendMessage("§cThat player is not in your party!");
            return;
        }

        PartyTransferEvent event = new PartyTransferEvent(transferer, target, party);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {return;}

        party.transferLeadership(target.getUniqueId());
        announceToParty(party, "§6" + target.getName() + " §ais now the party leader!");
    }

    private void handleDisband(Player player, String[] args) {
        Party party = getPlugin().getPlayerParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage("§cYou are not in a party!");
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage("§cOnly the party leader can disband the party!");
            return;
        }

        PartyDisbandEvent event = new PartyDisbandEvent(player, party);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {return;}

        getPlugin().disbandParty(party);

        player.sendMessage("§cThe party was disbanded!");
        announceToParty(party, "§6" + player.getName() + " §chas disbanded the party!", player);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        String commandName = command.getName().toLowerCase();

        if (commandName.equals("party")) {
            if (args.length == 1) {
                List<String> completions = Arrays.asList("invite", "join", "kick", "transfer", "leave", "list", "disband");
                return completions.stream()
                        .filter(c -> c.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "kick":
                    case "transfer":
                        Party party = getPlugin().getPlayerParty(player.getUniqueId());
                        if (party != null && party.isLeader(player.getUniqueId())) {
                            return party.getMembers().stream()
                                    .map(Bukkit::getPlayer)
                                    .filter(p -> p != null && p.isOnline() && !p.getUniqueId().equals(player.getUniqueId()))
                                    .map(Player::getName)
                                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                    .collect(Collectors.toList());
                        }
                        break;

                    case "invite":
                        return Bukkit.getOnlinePlayers().stream()
                                .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                                .filter(p -> !getPlugin().hasParty(p.getUniqueId()))
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                .collect(Collectors.toList());

                    case "join":
                        ArrayList<UUID> invites = ArcParties.pendingInvites.get(player.getUniqueId());
                        if (invites != null) {
                            return invites.stream()
                                    .map(Bukkit::getPlayer)
                                    .filter(p -> p != null && p.isOnline())
                                    .map(Player::getName)
                                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                                    .collect(Collectors.toList());
                        }
                        break;
                }
            }
        }
        // FIXME
//        else if (commandName.equals("arcparties")) {
//            if (args.length == 1) {
//                List<String> completions = new ArrayList<>();
//                if (player.hasPermission("arcparties.reload")) {
//                    completions.add("reload");
//                }
//                return completions.stream()
//                        .filter(c -> c.toLowerCase().startsWith(args[0].toLowerCase()))
//                        .collect(Collectors.toList());
//            }
//
//            if (args.length == 2 && args[0].equalsIgnoreCase("reload")) {
//                List<String> completions = Arrays.asList("config", "messages");
//                return completions.stream()
//                        .filter(c -> c.toLowerCase().startsWith(args[1].toLowerCase()))
//                        .collect(Collectors.toList());
//            }
//        }

        return new ArrayList<>();
    }
}
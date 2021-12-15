package com.burchard36.rust.data.json;

import com.burchard36.json.JsonDataFile;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.ClanRole;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static com.burchard36.ApiLib.convert;

public class JsonRustClan extends JsonDataFile {

    @SerializedName(value = "clan_uuid")
    public String clanUuid;

    @SerializedName(value = "clan_name")
    public String clanName;

    @SerializedName(value = "clanTag")
    public String clanTag;

    @SerializedName(value = "main_clan_owner_uuid")
    public String clanOwner;

    @SerializedName(value = "clan_owner_uuids")
    public List<String> clanOwners;

    @SerializedName(value = "clan_moderator_uuids")
    public List<String> clanModerators;

    @SerializedName(value = "clan_member_uuids")
    public List<String> clanMembers;

    public transient List<UUID> invitedMembers = new ArrayList<>();

    public JsonRustClan(final UUID uuid) {
        super(Rust.INSTANCE, "/data/clans/" + uuid.toString() + ".json");
    }

    public List<UUID> getOwnerUuids() {
        final List<UUID> owners = new ArrayList<>();
        clanOwners.forEach((owner) -> owners.add(UUID.fromString(owner)));
        return owners;
    }

    public List<UUID> getModeratorUuids() {
        final List<UUID> moderators = new ArrayList<>();
        clanModerators.forEach((mod) -> moderators.add(UUID.fromString(mod)));
        return moderators;
    }

    public List<UUID> getMemberUuids() {
        final List<UUID> members = new ArrayList<>();
        clanMembers.forEach((member) -> members.add(UUID.fromString(member)));
        return members;
    }

    public List<UUID> getAllMembers() {
        final List<UUID> list = new ArrayList<>(this.getOwnerUuids());
        list.addAll(this.getModeratorUuids());
        list.addAll(this.getMemberUuids());
        list.add(UUID.fromString(this.clanOwner));
        return list;
    }

    public List<OfflinePlayer> getAllMembersAsOfflinePlayers() {
        final List<OfflinePlayer> players = new ArrayList<>();
        this.getAllMembers().forEach((member) -> players.add(Bukkit.getOfflinePlayer(member)));
        return players;
    }

    public final boolean isMainOwner(final Player player) {
        return this.isMainOwner(player.getUniqueId());
    }

    public final boolean isMainOwner(final UUID uuid) {
        return this.clanOwner.equals(uuid.toString());
    }

    public final ClanRole getRoleOf(final Player player) {
        return this.getRoleOf(player.getUniqueId());
    }

    public final ClanRole getRoleOf(final UUID uuid) {
        if (this.clanOwner.equals(uuid.toString())) return ClanRole.OWNER;
        if (this.getOwnerUuids().contains(uuid)) return ClanRole.OWNER;
        if (this.getModeratorUuids().contains(uuid)) return ClanRole.MODERATOR;
        if (this.getMemberUuids().contains(uuid)) return ClanRole.MEMBER;
        return ClanRole.NONE;
    }

    public final ClanRole getNextRole(final ClanRole role) {
        if (role == ClanRole.OWNER) return ClanRole.OWNER;
        if (role == ClanRole.MODERATOR) return ClanRole.MODERATOR;
        if (role == ClanRole.MEMBER) return ClanRole.MODERATOR;
        return ClanRole.MEMBER;
    }

    public final ClanRole getPreviousRole(final ClanRole role) {
        if (role == ClanRole.OWNER) return ClanRole.MODERATOR;
        if (role == ClanRole.MODERATOR) return ClanRole.MEMBER;
        return ClanRole.NONE;
    }

    private void removeFromRole(final UUID uuid, final ClanRole role) {
        switch (role) {
            case OWNER -> this.clanOwners.remove(uuid.toString());
            case MODERATOR -> this.clanModerators.remove(uuid.toString());
            case MEMBER -> this.clanMembers.remove(uuid.toString());
        }
    }

    private void addToRole(final UUID uuid, final ClanRole role) {
        switch (role) {
            case OWNER -> this.clanOwners.add(uuid.toString());
            case MODERATOR -> this.clanModerators.add(uuid.toString());
            case MEMBER -> this.clanMembers.add(uuid.toString());
        }
    }

    private void removePlayerFromClan(final UUID uuid) {
        this.removeFromRole(uuid, this.getRoleOf(uuid));
        if (this.isInThisClan(uuid)) this.removePlayerFromClan(uuid);
    }

    public final boolean hasAuthorityOver(final UUID authority, final UUID toCheckAgainst) {
        return this.getRoleOf(authority).getWeight() > this.getRoleOf(toCheckAgainst).getWeight() || this.isMainOwner(authority);
    }

    public final boolean canInvitePlayers(final UUID toCheck) {
        return this.getRoleOf(toCheck).getWeight() >= ClanRole.MODERATOR.getWeight();
    }

    public final void promotePlayer(final Player promoter, final UUID toPromote) {
        if (!this.isInThisClan(toPromote)) {
            promoter.sendMessage(convert("&c&oYou cannot promote this member because they are not in this clan"));
            return;
        }

        if (promoter.getUniqueId().equals(toPromote)) {
            promoter.sendMessage(convert("&c&oYou cannot promote yourself!"));
            return;
        }

        if (!hasAuthorityOver(promoter.getUniqueId(), toPromote)) {
            promoter.sendMessage(convert("&c&oYou cannot promote this player because you do not have authority over them!"));
            return;
        }

        final ClanRole currentRole = this.getRoleOf(toPromote);
        if (currentRole == ClanRole.OWNER) {
            promoter.sendMessage(convert("&c&oYou cannot promote this member any further as they already have the OWNER role"));
            return;
        }

        final ClanRole nextRole = this.getNextRole(currentRole);
        this.addToRole(toPromote, nextRole);
        this.removeFromRole(toPromote, currentRole);
        promoter.sendMessage(convert("&a&oYou have successfully promoted the player to " + nextRole.name()));
    }

    public final void demotePlayer(final Player demoter, final UUID toDemote) {
        if (!this.isInThisClan(toDemote)) {
            demoter.sendMessage(convert("&c&oYou cannot demote this member because they are not a member of this clan!"));
            return;
        }
        if (demoter.getUniqueId().equals(toDemote)) {
            demoter.sendMessage(convert("&c&oYou cannot demote yourself!"));
            return;
        }

        if (!hasAuthorityOver(demoter.getUniqueId(), toDemote)) {
            demoter.sendMessage(convert("&c&oYou cannot demote this member because you do not have authority over them!"));
            return;
        }

        final ClanRole currentRole = this.getRoleOf(toDemote);
        final ClanRole nextRole = this.getPreviousRole(currentRole);
        if (nextRole == ClanRole.NONE) {
            demoter.sendMessage(convert("&c&oYou cannot demote this player any further, if you want them removed from your clan do &e&o/clan kick <player>"));
            return;
        }

        this.addToRole(toDemote, nextRole);
        this.removeFromRole(toDemote, currentRole);
        demoter.sendMessage(convert("&a&oSuccessfully set the members role to: " + nextRole.name()));
    }

    public final void invitePlayer(final UUID invitee, final UUID toInvite) {
        final Player inviter = Bukkit.getPlayer(invitee);
        if (inviter == null) return;
        if (!this.canInvitePlayers(invitee)) {
            inviter.sendMessage(convert("&c&oYou dont have permissions to invite this player")); // ignore NPC, will never be null
            return;
        }

        final JsonPlayerData toInviteData = Rust.INSTANCE.getPlayerDataManager().getPlayerData(toInvite);
        if (toInviteData.isInAClan(Rust.INSTANCE)) {
            inviter.sendMessage(convert("&c&oThis player is in a clan, you cannot invite them!"));
            return;
        }

        if (this.invitedMembers.contains(toInvite)) {
            inviter.sendMessage(convert("&c&oThis member already has a invite to this clan!"));
            return;
        }

        this.invitedMembers.add(toInvite);
        final Player invitedPlayer = Bukkit.getPlayer(toInvite);
        if (invitedPlayer != null) {
            invitedPlayer.sendMessage(convert("&a&oYou have received an invite to the clan &e&l&o: " + this.clanName + "&a&o. You were invited by &e&l&o: " + inviter.getName()));
            invitedPlayer.sendMessage(convert("&a&oType &e&o/clan join " + this.clanName + "&a&o to accept this invite!"));
        }
    }

    public final void transferOwnership(final UUID to) {
        this.clanMembers.add(this.clanOwner);
        this.clanOwner = to.toString();
    }

    public final void kickPlayer(final Player kicker, final UUID toKick) {
        if (!this.isInThisClan(toKick)) {
            kicker.sendMessage(convert("&c&oYou cannot kick this player because they are not in this clan!"));
            return;
        }

        if (kicker.getUniqueId().equals(toKick)) {
            kicker.sendMessage(convert("&c&oYou cannot kick yourself!"));
            return;
        }

        final ClanRole kickerRole = this.getRoleOf(kicker.getUniqueId());
        if (kickerRole.getWeight() < ClanRole.MODERATOR.getWeight()) {
            kicker.sendMessage(convert("&c&oOnly clan moderators are allowed to kick members!"));
            return;
        }

        if (!this.hasAuthorityOver(kicker.getUniqueId(), toKick)) {
            kicker.sendMessage(convert("&c&oYou cannot kick this player because you do not have authority over them!"));
            return;
        }

        this.removePlayerFromClan(toKick);
        kicker.sendMessage(convert("&c&oSuccessfully kicked the member from the clan!"));
    }

    public final boolean isInThisClan(final Player player) {
        return this.isInThisClan(player.getUniqueId());
    }

    public final boolean isInThisClan(final UUID uuid) {
        return this.getAllMembers().contains(uuid);
    }
}

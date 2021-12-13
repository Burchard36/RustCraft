package com.burchard36.rust.data.json;

import com.burchard36.json.JsonDataFile;
import com.burchard36.rust.Rust;
import com.burchard36.rust.data.ClanRole;
import com.google.gson.annotations.SerializedName;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static com.burchard36.ApiLib.convert;

public class JsonRustClan extends JsonDataFile {

    @SerializedName(value = "clan_uuid")
    public String clanUuid;

    @SerializedName(value = "main_clan_owner_uuid")
    public String clanOwner;

    @SerializedName(value = "clan_owner_uuids")
    public List<String> clanOwners;

    @SerializedName(value = "clan_moderator_uuids")
    public List<String> clanModerators;

    @SerializedName(value = "clan_member_uuids")
    public List<String> clanMembers;

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

    public final boolean hasAuthorityOver(final UUID authority, final UUID toCheckAgainst) {
        return this.getRoleOf(authority).getWeight() > this.getRoleOf(toCheckAgainst).getWeight() || this.isMainOwner(authority);
    }

    public final void promotePlayer(final Player promoter, final UUID toPromote) {
        if (promoter.getUniqueId().equals(toPromote)) {
            promoter.sendMessage(convert("You cannot promote yourself!"));
            return;
        }

        if (!hasAuthorityOver(promoter.getUniqueId(), toPromote)) {
            promoter.sendMessage(convert("&cYou cannot promote this player because you do not have authority over them!"));
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

    public final boolean isInThisClan(final Player player) {
        return this.isInThisClan(player.getUniqueId());
    }

    public final boolean isInThisClan(final UUID uuid) {
        return this.getAllMembers().contains(uuid);
    }
}

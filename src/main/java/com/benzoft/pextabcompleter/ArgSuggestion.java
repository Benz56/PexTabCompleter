package com.benzoft.pextabcompleter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum ArgSuggestion {
    ONLINE_PLAYERS(() -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList())),
    WORLDS(() -> Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList())),
    GROUPS(() -> new ArrayList<>(PermissionsEx.getPermissionManager().getGroupNames())),
    RANK_LADDERS(() -> PermissionsEx.getPermissionManager().getGroupList().stream().map(PermissionGroup::getRankLadder).distinct().collect(Collectors.toList()));

    @Getter
    private final Supplier<List<String>> supplier;
}

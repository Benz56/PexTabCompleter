package com.benzoft.pextabcompleter;

import com.benzoft.pextabcompleter.permissiontree.PermissionNode;
import com.benzoft.pextabcompleter.permissiontree.PermissionTree;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.benzoft.pextabcompleter.ArgSuggestion.*;

public final class PexTabCompleter extends JavaPlugin {

    private final static BiFunction<Stream<String>, String, List<String>> STRING_FILTER = (strings, currentInput) -> strings.filter(string -> string.toLowerCase().startsWith(currentInput.toLowerCase())).collect(Collectors.toList());

    private final List<PexCommand> pexCommands = Arrays.asList(
            new PexCommand(this, "config <node> [value]"),
            new PexCommand(this, "backend <backend>"),
            new PexCommand(this, "import <backend>"),
            new PexCommand(this, "help [page] [count]"),
            new PexCommand(this, "convert uuid"),
            new PexCommand(this, "toggle debug"),
            new PexCommand(this, "users list"),
            new PexCommand(this, "hierarchy [world]", ImmutableMap.of(1, WORLDS.getSupplier())),
            new PexCommand(this, "user *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier())),
            new PexCommand(this, "user * list *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 3, WORLDS.getSupplier())),
            new PexCommand(this, "user * superperms", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier())),
            new PexCommand(this, "user * prefix [newprefix] *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "user * suffix [newsuffix] *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "user * toggle debug", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier())),
            new PexCommand(this, "user * check <permission> *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "user * get <option> *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "user * delete", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier())),
            new PexCommand(this, "user * add <permission> *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "user * remove <permission> *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "user * swap <permission> <targetPermission> *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "user * timed add <permission> [lifetime] *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 6, WORLDS.getSupplier())),
            new PexCommand(this, "user * timed remove <permission> *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 6, WORLDS.getSupplier())),
            new PexCommand(this, "user * set <option> <value> *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 6, WORLDS.getSupplier())),
            new PexCommand(this, "user * group list *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "user * group add * * [lifetime]", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 4, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "user * group set * *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 4, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "user * group remove * *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 4, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "users cleanup * [threshold]", ImmutableMap.of(2, GROUPS.getSupplier())),
            new PexCommand(this, "group * swap <permission> <targetPermission> *", ImmutableMap.of(2, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "groups list *", ImmutableMap.of(2, WORLDS.getSupplier())),
            new PexCommand(this, "group * weight [weight]", ImmutableMap.of(1, GROUPS.getSupplier())),
            new PexCommand(this, "group * toggle debug", ImmutableMap.of(1, GROUPS.getSupplier())),
            new PexCommand(this, "group * prefix [newprefix] *", ImmutableMap.of(1, GROUPS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "group * suffix [newsuffix] *", ImmutableMap.of(1, GROUPS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "group * create [parents]", ImmutableMap.of(1, GROUPS.getSupplier())),
            new PexCommand(this, "group * delete", ImmutableMap.of(1, GROUPS.getSupplier())),
            new PexCommand(this, "group * parents *", ImmutableMap.of(1, GROUPS.getSupplier(), 3, WORLDS.getSupplier())),
            new PexCommand(this, "group * parents list *", ImmutableMap.of(1, GROUPS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "group * parents set <parents> *", ImmutableMap.of(1, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "group * parents add <parents> *", ImmutableMap.of(1, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "group * parents remove <parents> *", ImmutableMap.of(1, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "group * list *", ImmutableMap.of(1, GROUPS.getSupplier(), 3, WORLDS.getSupplier())),
            new PexCommand(this, "group * add <permission> *", ImmutableMap.of(1, GROUPS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "group * set <option> <value> *", ImmutableMap.of(1, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "group * remove <permission> *", ImmutableMap.of(1, GROUPS.getSupplier(), 4, WORLDS.getSupplier())),
            new PexCommand(this, "group * timed add <permission> [lifetime] *", ImmutableMap.of(1, GROUPS.getSupplier())),
            new PexCommand(this, "group * timed remove <permission> *", ImmutableMap.of(1, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "group * users", ImmutableMap.of(1, GROUPS.getSupplier())),
            new PexCommand(this, "group * user add * *", ImmutableMap.of(1, GROUPS.getSupplier(), 4, ONLINE_PLAYERS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "group * user remove * *", ImmutableMap.of(1, GROUPS.getSupplier(), 4, ONLINE_PLAYERS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "default group *", ImmutableMap.of(2, WORLDS.getSupplier())),
            new PexCommand(this, "set default group * <value> *", ImmutableMap.of(3, GROUPS.getSupplier(), 5, WORLDS.getSupplier())),
            new PexCommand(this, "group * rank [rank] [ladder]", ImmutableMap.of(1, GROUPS.getSupplier())),
            new PexCommand(this, "promote * *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 2, RANK_LADDERS.getSupplier())),
            new PexCommand(this, "demote * *", ImmutableMap.of(1, ONLINE_PLAYERS.getSupplier(), 2, RANK_LADDERS.getSupplier())),
            new PexCommand(this, "world *", ImmutableMap.of(1, WORLDS.getSupplier())),
            new PexCommand(this, "world * inherit *", ImmutableMap.of(1, WORLDS.getSupplier(), 3, WORLDS.getSupplier())
            ));

    private final PermissionTree permissionTree = new PermissionTree();
    @Getter
    private final Function<String, List<String>> permissionSuggestions = current -> {
        PermissionNode end = permissionTree.getRootNode();
        for (final String node : Arrays.copyOfRange(current.split("\\.", -1), 0, current.split("\\.", -1).length - 1)) {
            if ((end = end.getChildren().get(node)) == null) break;
        }
        return end != null ? end.getChildren().keySet().stream().map(s -> current.substring(0, current.lastIndexOf(".") + 1) + s).collect(Collectors.toList()) : Collections.emptyList();
    };


    @Override
    public void onEnable() {
        Objects.requireNonNull(getPlugin(PermissionsEx.class).getCommand("pex")).setTabCompleter((sender, command, alias, args) -> !sender.isOp() && !sender.hasPermission("pextabcompleter.use") ? null : args.length == 1 ? STRING_FILTER.apply(Stream.of("help", "user", "users", "group", "groups", "toggle", "reload", "config", "backend", "hierarchy", "import", "worlds", "world", "default", "set"), args[0]) : STRING_FILTER.apply(pexCommands.stream().filter(pexCommand -> pexCommand.isCommand(args)).flatMap(pexCommand -> pexCommand.getSuggestions(args).stream()).distinct(), args[args.length - 1]));
        Arrays.asList("promote", "demote").forEach(cmd -> Objects.requireNonNull(getPlugin(PermissionsEx.class).getCommand(cmd)).setTabCompleter((sender, command, alias, args) -> args.length == 1 ? STRING_FILTER.apply(ONLINE_PLAYERS.getSupplier().get().stream(), args[0]) : args.length == 2 ? STRING_FILTER.apply(RANK_LADDERS.getSupplier().get().stream(), args[1]) : Collections.emptyList()));
        Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().getPermissions().forEach(permission -> permissionTree.insert(permission.getName())));
    }
}

package com.benzoft.pextabcompleter;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PexTabCompleter extends JavaPlugin {

    private static final Supplier<List<String>> ONLINE_PLAYERS = () -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    private static final Supplier<List<String>> WORLDS = () -> Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
    private static final Supplier<List<String>> GROUPS = () -> new ArrayList<>(PermissionsEx.getPermissionManager().getGroupNames());
    private static final Supplier<List<String>> RANK_LADDERS = () -> PermissionsEx.getPermissionManager().getGroupList().stream().map(PermissionGroup::getRankLadder).distinct().collect(Collectors.toList());
    private static final List<PexCommand> PEX_COMMANDS = Arrays.asList(
            new PexCommand("config <node> [value]"),
            new PexCommand("backend <backend>"),
            new PexCommand("import <backend>"),
            new PexCommand("help [page] [count]"),
            new PexCommand("convert uuid"),
            new PexCommand("toggle debug"),
            new PexCommand("users list"),
            new PexCommand("hierarchy [world]", ImmutableMap.of(1, WORLDS)),
            new PexCommand("user *", ImmutableMap.of(1, ONLINE_PLAYERS)),
            new PexCommand("user * list *", ImmutableMap.of(1, ONLINE_PLAYERS, 3, WORLDS)),
            new PexCommand("user * superperms", ImmutableMap.of(1, ONLINE_PLAYERS)),
            new PexCommand("user * prefix [newprefix] *", ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)),
            new PexCommand("user * suffix [newsuffix] *", ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)),
            new PexCommand("user * toggle debug", ImmutableMap.of(1, ONLINE_PLAYERS)),
            new PexCommand("user * check <permission> *", ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)),
            new PexCommand("user * get <option> *", ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)),
            new PexCommand("user * delete", ImmutableMap.of(1, ONLINE_PLAYERS)),
            new PexCommand("user * add <permission> *", ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)),
            new PexCommand("user * remove <permission> *", ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)),
            new PexCommand("user * swap <permission> <targetPermission> *", ImmutableMap.of(1, ONLINE_PLAYERS, 5, WORLDS)),
            new PexCommand("user * timed add <permission> [lifetime] *", ImmutableMap.of(1, ONLINE_PLAYERS, 6, WORLDS)),
            new PexCommand("user * timed remove <permission> *", ImmutableMap.of(1, ONLINE_PLAYERS, 6, WORLDS)),
            new PexCommand("user * set <option> <value> *", ImmutableMap.of(1, ONLINE_PLAYERS, 6, WORLDS)),
            new PexCommand("user * group list *", ImmutableMap.of(1, ONLINE_PLAYERS, 5, WORLDS)),
            new PexCommand("user * group add * * [lifetime]", ImmutableMap.of(1, ONLINE_PLAYERS, 4, GROUPS, 5, WORLDS)),
            new PexCommand("user * group set * *", ImmutableMap.of(1, ONLINE_PLAYERS, 4, GROUPS, 5, WORLDS)),
            new PexCommand("user * group remove * *", ImmutableMap.of(1, ONLINE_PLAYERS, 4, GROUPS, 5, WORLDS)),
            new PexCommand("users cleanup * [threshold]", ImmutableMap.of(2, GROUPS)),
            new PexCommand("group * swap <permission> <targetPermission> *", ImmutableMap.of(2, GROUPS, 5, WORLDS)),
            new PexCommand("groups list *", ImmutableMap.of(2, WORLDS)),
            new PexCommand("group * weight [weight]", ImmutableMap.of(1, GROUPS)),
            new PexCommand("group * toggle debug", ImmutableMap.of(1, GROUPS)),
            new PexCommand("group * prefix [newprefix] *", ImmutableMap.of(1, GROUPS, 4, WORLDS)),
            new PexCommand("group * suffix [newsuffix] *", ImmutableMap.of(1, GROUPS, 4, WORLDS)),
            new PexCommand("group * create [parents]", ImmutableMap.of(1, GROUPS)),
            new PexCommand("group * delete", ImmutableMap.of(1, GROUPS)),
            new PexCommand("group * parents *", ImmutableMap.of(1, GROUPS, 3, WORLDS)),
            new PexCommand("group * parents list *", ImmutableMap.of(1, GROUPS, 4, WORLDS)),
            new PexCommand("group * parents set <parents> *", ImmutableMap.of(1, GROUPS, 5, WORLDS)),
            new PexCommand("group * parents add <parents> *", ImmutableMap.of(1, GROUPS, 5, WORLDS)),
            new PexCommand("group * parents remove <parents> *", ImmutableMap.of(1, GROUPS, 5, WORLDS)),
            new PexCommand("group * list *", ImmutableMap.of(1, GROUPS, 3, WORLDS)),
            new PexCommand("group * add <permission> *", ImmutableMap.of(1, GROUPS, 4, WORLDS)),
            new PexCommand("group * set <option> <value> *", ImmutableMap.of(1, GROUPS, 5, WORLDS)),
            new PexCommand("group * remove <permission> *", ImmutableMap.of(1, GROUPS, 4, WORLDS)),
            new PexCommand("group * timed add <permission> [lifetime] *", ImmutableMap.of(1, GROUPS)),
            new PexCommand("group * timed remove <permission> *", ImmutableMap.of(1, GROUPS, 5, WORLDS)),
            new PexCommand("group * users", ImmutableMap.of(1, GROUPS)),
            new PexCommand("group * user add * *", ImmutableMap.of(1, GROUPS, 4, ONLINE_PLAYERS, 5, WORLDS)),
            new PexCommand("group * user remove * *", ImmutableMap.of(1, GROUPS, 4, ONLINE_PLAYERS, 5, WORLDS)),
            new PexCommand("default group *", ImmutableMap.of(2, WORLDS)),
            new PexCommand("set default group * <value> *", ImmutableMap.of(3, GROUPS, 5, WORLDS)),
            new PexCommand("group * rank [rank] [ladder]", ImmutableMap.of(1, GROUPS)),
            new PexCommand("promote * *", ImmutableMap.of(1, ONLINE_PLAYERS, 2, RANK_LADDERS)),
            new PexCommand("demote * *", ImmutableMap.of(1, ONLINE_PLAYERS, 2, RANK_LADDERS)),
            new PexCommand("world *", ImmutableMap.of(1, WORLDS)),
            new PexCommand("world * inherit *", ImmutableMap.of(1, WORLDS, 3, WORLDS)
            ));
    private static final Function<CommandSender, Boolean> PERMISSIBLE = commandSender -> commandSender.isOp() || commandSender.hasPermission("pextabcompleter.use");
    private static final BiFunction<Stream<String>, String, List<String>> STRING_FILTER = (strings, currentInput) -> strings.filter(string -> string.toLowerCase().startsWith(currentInput.toLowerCase())).collect(Collectors.toList());


    @Override
    public void onEnable() {
        Objects.requireNonNull(getPlugin(PermissionsEx.class).getCommand("pex")).setTabCompleter((sender, command, alias, args) -> {
            if (!PERMISSIBLE.apply(sender)) return null;
            if (args.length == 1) {
                return STRING_FILTER.apply(Stream.of("help", "user", "users", "group", "groups", "toggle", "reload", "config", "backend", "hierarchy", "import", "worlds", "world", "default", "set"), args[0]);
            }
            return STRING_FILTER.apply(PEX_COMMANDS.stream().filter(pexCommand -> pexCommand.isCommand(args)).flatMap(pexCommand -> pexCommand.getSuggestions(args).stream()).distinct(), args[args.length - 1]);
        });
        Arrays.asList("promote", "demote").forEach(cmd -> Objects.requireNonNull(getPlugin(PermissionsEx.class).getCommand(cmd)).setTabCompleter((sender, command, alias, args) -> args.length == 1 ? STRING_FILTER.apply(ONLINE_PLAYERS.get().stream(), args[0]) : args.length == 2 ? STRING_FILTER.apply(RANK_LADDERS.get().stream(), args[1]) : Collections.emptyList()));
    }
}

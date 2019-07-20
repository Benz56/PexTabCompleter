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
            PexCommand.builder().command(toArray("config node *")).build(),
            PexCommand.builder().command(toArray("hierarchy [world]")).indexSuggestions(ImmutableMap.of(1, WORLDS)).build(),
            PexCommand.builder().command(toArray("convert uuid")).build(),
            PexCommand.builder().command(toArray("toggle debug")).build(),
            PexCommand.builder().command(toArray("users list")).build(),
            PexCommand.builder().command(toArray("user *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS)).build(),
            PexCommand.builder().command(toArray("user * list *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 3, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * superperms")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS)).build(),
            PexCommand.builder().command(toArray("user * prefix [newprefix] *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * suffix [newsuffix] *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * toggle debug")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS)).build(),
            PexCommand.builder().command(toArray("user * check <permission> *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * get <option> *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * delete")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS)).build(),
            PexCommand.builder().command(toArray("user * add <permission> *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * remove <permission> *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * swap <permission> <targetPermission> *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * timed add <permission> [lifetime] *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 6, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * timed remove <permission> *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 6, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * set <option> <value> *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 6, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * group list *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * group add * * [lifetime]")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 4, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * group set * *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 4, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("user * group remove * *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 4, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("users cleanup * [threshold]")).indexSuggestions(ImmutableMap.of(2, GROUPS)).build(),
            PexCommand.builder().command(toArray("group * swap <permission> <targetPermission> *")).indexSuggestions(ImmutableMap.of(2, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("groups list *")).indexSuggestions(ImmutableMap.of(2, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * weight [weight]")).indexSuggestions(ImmutableMap.of(1, GROUPS)).build(),
            PexCommand.builder().command(toArray("group * toggle debug")).indexSuggestions(ImmutableMap.of(1, GROUPS)).build(),
            PexCommand.builder().command(toArray("group * prefix [newprefix] *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * suffix [newsuffix] *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * create [parents]")).indexSuggestions(ImmutableMap.of(1, GROUPS)).build(),
            PexCommand.builder().command(toArray("group * delete")).indexSuggestions(ImmutableMap.of(1, GROUPS)).build(),
            PexCommand.builder().command(toArray("group * parents *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 3, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * parents list *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * parents set <parents> *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * parents add <parents> *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * parents remove <parents> *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * list *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 3, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * add <permission> *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * set <option> <value> *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * remove <permission> *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 4, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * timed add <permission> [lifetime] *")).indexSuggestions(ImmutableMap.of(1, GROUPS)).build(),
            PexCommand.builder().command(toArray("group * timed remove <permission> *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * users")).indexSuggestions(ImmutableMap.of(1, GROUPS)).build(),
            PexCommand.builder().command(toArray("group * user add * *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 4, ONLINE_PLAYERS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * user remove * *")).indexSuggestions(ImmutableMap.of(1, GROUPS, 4, ONLINE_PLAYERS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("default group *")).indexSuggestions(ImmutableMap.of(2, WORLDS)).build(),
            PexCommand.builder().command(toArray("set default group * <value> *")).indexSuggestions(ImmutableMap.of(3, GROUPS, 5, WORLDS)).build(),
            PexCommand.builder().command(toArray("group * rank [rank] [ladder]")).indexSuggestions(ImmutableMap.of(1, GROUPS)).build(),
            PexCommand.builder().command(toArray("promote * *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 2, RANK_LADDERS)).build(),
            PexCommand.builder().command(toArray("demote * *")).indexSuggestions(ImmutableMap.of(1, ONLINE_PLAYERS, 2, RANK_LADDERS)).build(),
            PexCommand.builder().command(toArray("world *")).indexSuggestions(ImmutableMap.of(1, WORLDS)).build(),
            PexCommand.builder().command(toArray("world * inherit *")).indexSuggestions(ImmutableMap.of(1, WORLDS, 3, WORLDS)).build()
    );
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

    private static String[] toArray(final String command) {
        return command.split(" ");
    }
}

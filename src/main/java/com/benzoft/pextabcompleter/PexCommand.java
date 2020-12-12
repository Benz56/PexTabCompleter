package com.benzoft.pextabcompleter;

import com.benzoft.pextabcompleter.permissiontree.PermissionNode;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class PexCommand {

    private final PexTabCompleter pexTabCompleter;
    private final String[] command;
    private final Map<Integer, Supplier<List<String>>> indexSuggestions;

    PexCommand(final PexTabCompleter pexTabCompleter, final String command) {
        this(pexTabCompleter, command, new HashMap<>());
    }

    PexCommand(final PexTabCompleter pexTabCompleter, final String command, final Map<Integer, Supplier<List<String>>> indexSuggestions) {
        this.pexTabCompleter = pexTabCompleter;
        this.command = command.split(" ");
        this.indexSuggestions = indexSuggestions;
    }

    boolean isCommand(final String[] input) {
        try {
            for (int i = 0; i < input.length; i++) {
                final String fullWord = command[i], inputWord = input[i];
                if (i == input.length - 1 && fullWord.toLowerCase().startsWith(inputWord.toLowerCase())) return true;
                final boolean placeholder = fullWord.equalsIgnoreCase("*") || (fullWord.startsWith("<") && fullWord.endsWith(">")) || (fullWord.startsWith("[") && fullWord.endsWith("]"));
                if (!placeholder && !fullWord.equalsIgnoreCase(inputWord)) return false;
            }
            return true;
        } catch (final ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    List<String> getArgSuggestion(final String[] input) {
        try {
            final int lastWordIndex = input.length - 1;
            final String currentWord = input[lastWordIndex], nextWord = command[lastWordIndex];
            if (nextWord.equalsIgnoreCase("<permission>")) {
                final String prevWord = input[lastWordIndex - 1];
                if (prevWord.equalsIgnoreCase("remove")) { // Suggest permissions that the group/user has.
                    return (command[0].equalsIgnoreCase("user") ? PermissionsEx.getUser(input[1]) : PermissionsEx.getPermissionManager().getGroup(input[1])).getAllPermissions().values().stream().flatMap(Collection::stream).distinct().collect(Collectors.toList());
                } else return getPermissionSuggestions(currentWord);
            } else if (!currentWord.isEmpty() && nextWord.startsWith(currentWord)) return Collections.singletonList(nextWord);
            return nextWord.equalsIgnoreCase("*") ? indexSuggestions.getOrDefault(lastWordIndex, Collections::emptyList).get() : Collections.singletonList(nextWord);
        } catch (final ArrayIndexOutOfBoundsException e) {
            return Collections.emptyList();
        }
    }

    private List<String> getPermissionSuggestions(final String current) {
        PermissionNode end = pexTabCompleter.getPermissionTree().getRootNode();
        for (final String node : Arrays.copyOfRange(current.substring(current.startsWith("-") ? 1 : 0).split("\\.", -1), 0, current.split("\\.", -1).length - 1)) {
            if ((end = end.getChildren().get(node)) == null) break;
        }
        return end != null ? end.getChildren().keySet().stream().map(s -> (current.startsWith("-") && !current.contains(".") ? "-" : "") + current.substring(0, current.lastIndexOf(".") + 1) + s).collect(Collectors.toList()) : Collections.emptyList();
    }
}

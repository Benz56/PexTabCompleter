package com.benzoft.pextabcompleter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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

    List<String> getSuggestions(final String[] input) {
        try {
            final int lastWordIndex = input.length - 1;
            final String currentWord = input[lastWordIndex], nextWord = command[lastWordIndex];
            if (nextWord.equalsIgnoreCase("<permission>")) {
                return pexTabCompleter.getPermissionSuggestions().apply(currentWord);
            } else if (!currentWord.isEmpty() && nextWord.startsWith(currentWord)) return Collections.singletonList(nextWord);
            return nextWord.equalsIgnoreCase("*") ? indexSuggestions.getOrDefault(lastWordIndex, Collections::emptyList).get() : Collections.singletonList(nextWord);
        } catch (final ArrayIndexOutOfBoundsException e) {
            return Collections.emptyList();
        }
    }
}

package com.benzoft.pextabcompleter.permissiontree;

import lombok.Getter;

public class PermissionTree {

    // Visualization of how permissions are split into a tree structure:
    //
    //                                    root(empty)
    //                     ┌───────────────────┴───────────────────┐
    //                    gps                                 pocketgames
    //         ┌───────────┼──────────┐                 ┌──────────┴──────────┐
    //      commands     update     hunger           commands              bypass
    //  ┌──────┼──────┐               |          ┌──────┼──────┐         ┌────┴────┐
    // help  start  stop            ignore     help    menu   duel    enabled    world
    //
    @Getter
    private final PermissionNode rootNode = new PermissionNode();

    public void insert(final String permission) {
        PermissionNode current = rootNode;
        for (final String node : permission.split("\\.")) {
            current = current.insert(node);
        }
    }
}

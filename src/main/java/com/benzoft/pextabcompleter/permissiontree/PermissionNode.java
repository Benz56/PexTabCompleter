package com.benzoft.pextabcompleter.permissiontree;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class PermissionNode {

    @Getter
    private final Map<String, PermissionNode> children = new HashMap<>();

    PermissionNode insert(final String s) {
        return children.compute(s, (key, prev) -> prev != null ? prev : new PermissionNode());
    }
}

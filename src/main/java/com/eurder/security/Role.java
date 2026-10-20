package com.eurder.security;

import java.util.Set;

public enum Role {
    ADMIN(Feature.CREATE_ITEM),
    CUSTOMER();

    private final Set<Feature> features;

    Role(Feature... features) {
        this.features = Set.of(features);
    }

    public boolean hasAccessTo(Feature feature) {
        return features.contains(feature);
    }
}
package com.eurder.eurder.domain;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EurderRepository {

    private final Map<UUID, Eurder> eurdersById = new ConcurrentHashMap<>();

    public Eurder save(Eurder eurder) {
        eurdersById.put(eurder.getId(), eurder);
        return eurder;
    }

    public Optional<Eurder> findById(UUID id) {
        return Optional.ofNullable(eurdersById.get(id));
    }
}

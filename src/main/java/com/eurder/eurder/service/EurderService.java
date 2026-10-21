package com.eurder.eurder.service;

import com.eurder.eurder.domain.Eurder;
import com.eurder.eurder.domain.EurderRepository;
import com.eurder.eurder.domain.ItemGroup;
import com.eurder.item.domain.Item;
import com.eurder.item.domain.ItemRepository;
import com.eurder.shared.InvalidInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EurderService {

    private static final Logger log = LoggerFactory.getLogger(EurderService.class);

    private final EurderRepository eurderRepository;
    private final ItemRepository itemRepository;
    private final Clock clock;

    public EurderService(EurderRepository eurderRepository, ItemRepository itemRepository, Clock clock) {
        this.eurderRepository = eurderRepository;
        this.itemRepository = itemRepository;
        this.clock = clock;
    }

    public Eurder placeEurder(UUID customerId, List<ItemGroupRequest> itemGroupRequests) {
        LocalDate orderDate = LocalDate.now(clock);

        // 1:  build and validate the whole order, without any side effect
        Map<UUID, Item> orderedItems = new HashMap<>();
        List<ItemGroup> itemGroups = new ArrayList<>();
        for (ItemGroupRequest request : itemGroupRequests) {
            Item item = findItem(request.itemId());
            orderedItems.put(item.getId(), item);
            itemGroups.add(ItemGroup.of(item, request.amount(), orderDate));
        }
        Eurder eurder = new Eurder(customerId, orderDate, itemGroups);

        // 2: everything is valid -> apply the changes
        for (ItemGroup itemGroup : eurder.getItemGroups()) {
            orderedItems.get(itemGroup.itemId()).decreaseStock(itemGroup.amount());
        }
        Eurder placedEurder = eurderRepository.save(eurder);
        log.info("Eurder {} placed by customer {} for a total of {}",
                placedEurder.getId(), customerId, placedEurder.getTotalPrice().amount());
        return placedEurder;
    }

    private Item findItem(UUID itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new InvalidInputException("Item not found: " + itemId));
    }
}
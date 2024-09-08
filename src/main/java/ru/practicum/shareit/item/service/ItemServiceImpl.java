package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items;
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.items = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Item createItem(Long userId, Item item) {
        if (userId == null || item == null) {
            throw new ValidationException("User ID or item cannot be null");
        }
        Long id = setCurrentId();
        item.setId(id);
        item.setOwner(userService.getUser(userId));
        items.put(userId, item);
        return item;
    }

    @Override
    public Item updateItem(User user, Item item) {
        Long ownerIid = user.getId();
        items.put(ownerIid, item);
        return item;
    }

    @Override
    public Item getItem(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUser(User user) {
        return items.values().stream().filter(item -> item.getOwner().getId().equals(user.getId())).toList();
    }

    @Override
    public List<Item> searchItems(String text) {
        String lowerCaseText = text.toLowerCase();
        return items.values().stream().filter(item -> item.getName().toLowerCase().contains(lowerCaseText)).toList();
    }

    @Override
    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    private Long setCurrentId() {
        return (long) (items.size() + 1);
    }
}

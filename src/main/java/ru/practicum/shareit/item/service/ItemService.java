package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemService {

    Item createItem(Long userId, Item item);

    Item updateItem(User user, Item item);

    Item getItem(Long itemId);

    List<Item> getItemsByUser(User user);

    List<Item> searchItems(String text);

    void deleteItem(Long itemId);
}

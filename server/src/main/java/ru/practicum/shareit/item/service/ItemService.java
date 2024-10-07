package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {

    Item createItem(Long userId, ItemDto item);

    Item updateItem(Long Id, ItemDto itemDto, Long itemId);

    Item getItem(Long itemId);

    List<Item> getItemsByUser(Long userId);

    List<Item> searchItems(String text);

    void deleteItem(Long itemId);

    ItemDetailsDto getItemDetails(Long itemId, Long userId);

    Comment addComment(Long itemId, Long userId, CommentDto commentDto);

    List<CommentDto> getCommentsForItem(Long itemId);

    boolean isItemAvailable(Long itemId, LocalDateTime start, LocalDateTime end);
}

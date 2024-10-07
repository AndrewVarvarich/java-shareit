package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;


@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Positive @PathVariable Long itemId) {
        log.info("Received GET at /items/{}?X-Sharer-User-Id={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET at /items?X-Sharer-User-Id={}", userId);
        return itemClient.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam("text") String text,
                                           @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET at /items/search?text={}", text);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createItem(@Positive@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated @RequestBody ItemDetailsDto newItemDto) {
        log.info("Received POST at /items X-Sharer-User-Id={} {}", userId, newItemDto);
        return itemClient.createItem(userId, newItemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDetailsDto itemDto,
                                             @Positive @PathVariable Long itemId) {
        log.info("Received PATCH at /items/{}: {}", userId, itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Positive@PathVariable Long itemId,
                                             @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody CommentDto commentDto) {
        log.info("Received POST at /items/{}/comment X-Sharer-User-Id={} {}", itemId, userId, commentDto);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}

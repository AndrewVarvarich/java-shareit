package ru.practicum.shareit.request;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Validated @RequestBody NewItemRequestDto itemRequestDto) {
        log.info("Received POST at /requests");
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET at /requests for userId={}", userId);
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@Positive @PathVariable Long requestId,
                                                 @Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET at /requests/{}", requestId);
        return itemRequestClient.getRequestById(requestId, userId);
    }
}

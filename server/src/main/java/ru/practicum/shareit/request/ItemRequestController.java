package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

import java.util.Collection;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;
    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody NewItemRequestDto itemRequestDto) {
        log.info("Received POST at /requests");
        ItemRequest itemRequest = itemRequestService.createRequest(userId, itemRequestDto);
        ItemRequestDto savedItemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        log.info("Responded to POST /requests: {}", itemRequestDto);
        return savedItemRequestDto;
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET at /requests for userId={}", userId);
        List<ItemRequest> itemRequests = itemRequestService.getRequestsByUserId(userId);
        List<ItemRequestDto> itemRequestDtos = itemRequestMapper.toItemRequestDtoList(itemRequests);
        log.info("Responded to GET /requests: {}", itemRequestDtos);
        return itemRequestDtos;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId) {
        log.info("Received GET at /requests/{}", requestId);
        ItemRequest itemRequest = itemRequestService.getRequestById(requestId);
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        log.info("Responded to GET /requests/{}: {}", requestId, itemRequestDto);
        return itemRequestDto;
    }
}

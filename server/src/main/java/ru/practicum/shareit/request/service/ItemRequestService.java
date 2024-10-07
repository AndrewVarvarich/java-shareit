package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createRequest(Long userId, NewItemRequestDto requestDto);

    void deleteRequest(Long userId, Long requestId);

    List<ItemRequest> getRequestsByUserId(Long userId);

    ItemRequest getRequestById(Long requestId);

}

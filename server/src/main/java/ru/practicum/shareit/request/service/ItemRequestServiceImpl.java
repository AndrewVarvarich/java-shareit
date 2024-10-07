package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemRequest createRequest(Long userId, NewItemRequestDto requestDto) {

        User requestor = userService.getUser(userId);

        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        log.info("Creating request with values: description={}, userId={}", requestDto.getDescription(), userId);
        return requestRepository.save(request);
    }

    @Override
    public void deleteRequest(Long userId, Long requestId) {
        if (userId == null || requestId == null) {
            throw new ValidationException("User ID or request ID cannot be null");
        }

        if (userService.getUser(userId) == null) {
            throw new NotFoundException("User not found");
        }

        if (requestRepository.findById(requestId).isEmpty()) {
            throw new NotFoundException("Request not found");
        }

        requestRepository.deleteById(requestId);
    }

    @Override
    public List<ItemRequest> getRequestsByUserId(Long userId) {
        userService.getUser(userId);
        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
    }

    @Override
    public ItemRequest getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));
    }
}

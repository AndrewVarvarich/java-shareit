package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repo.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.*;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper mapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Item createItem(Long userId, ItemDto item) {
        User user = userService.getUser(userId);
        Item newItem = mapper.toItem(item);
        if (item.getRequestId() != null) {
            ItemRequest request = itemRequestRepository
                    .findById(item.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
            item.setRequestId(request.getId());
        }
        log.info("Available value: {}", item.getAvailable());
        newItem.setOwner(user);
        return itemRepository.save(newItem);
    }

    @Override
    public Item updateItem(Long userId, ItemDto updatedItemDto, Long itemId) {
        userService.getUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessErrorException("Item can be updated only by its owner");
        }

        String name = updatedItemDto.getName();
        String description = updatedItemDto.getDescription();
        Boolean available = updatedItemDto.getAvailable();

        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }

        return itemRepository.save(item);
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public List<Item> getItemsByUser(Long userId) {
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerCaseText = text.toLowerCase();
        List<Item> itemList = itemRepository.search(lowerCaseText);

        if (itemList.isEmpty()) {
            return Collections.emptyList();
        }

        return itemList.stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDetailsDto getItemDetails(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        ItemDetailsDto itemDetailsDto = mapper.toItemDetailsDto(item);

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(commentMapper::toCommentDto)
                .toList();
        log.info("Number of comments: " + comments.size());

        itemDetailsDto.setComments(comments);
        LocalDateTime now = LocalDateTime.now();
        log.info("Current Time: " + now);

        if (item.getOwner().getId().equals(userId)) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, now);
            lastBooking.ifPresent(booking -> {
                itemDetailsDto.setLastBooking(lastBooking.get());
                log.info("Last Booking found: " + booking);
            });
        }

        Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now);
        nextBooking.ifPresent(booking -> {
            itemDetailsDto.setNextBooking(nextBooking.get());
            log.info("Next Booking found: " + booking);
        });

        return itemDetailsDto;
    }

    @Override
    public Comment addComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean isValidBooking = bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId,
                LocalDateTime.now());

        if (!isValidBooking) {
            throw new ValidationException("User has not rented this item or rental period has not ended");
        }

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getCommentsForItem(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public boolean isItemAvailable(Long itemId, LocalDateTime start, LocalDateTime end) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(itemId, start, end);
        return overlappingBookings.isEmpty();
    }
}

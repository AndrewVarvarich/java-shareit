package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repo.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.service.BookingStatus.APPROVED;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingMapper mapper;

    @Override
    public Booking createBooking(Long bookerId, BookingDto bookingDto) {
        Long itemId = bookingDto.getItemId();
        Item item = itemService.getItem(itemId);
        Booking booking = mapper.toBooking(bookingDto, itemService);
        booking.setBooker(userService.getUser(bookerId));
        booking.setItem(item);

        Long ownerId = item.getOwner().getId();
        if (ownerId.equals(bookerId)) {
            throw new ValidationException("Owner cannot book their own item.");
        }
        if (!itemService.isItemAvailable(itemId, booking.getStart(), booking.getEnd())) {
            throw new ValidationException("Item is not available for the selected dates.");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available.");
        }
        booking.setOwner(item.getOwner());
        LocalDateTime now = LocalDateTime.now();
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking changeStatus(Long bookingId, Long ownerId, Boolean approved) {
        if (approved == null) {
            throw new ValidationException("Approved status must be provided");
        }
        Booking booking = bookingRepository
                .findById(bookingId).orElseThrow(() -> new NotFoundException("Booking with id " + bookingId +
                        " not found"));
        if (!booking.getItem().getOwner().getId()
                .equals(ownerId)) {
            throw new AccessErrorException("Only the owner of the item can change booking status");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        } else {
            throw new AccessErrorException("Only the owner of the item or the booker can get booking");
        }    }

    @Override
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public Booking updateBooking(Booking booking, Long bookingId) {
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
            existingBooking.setStart(booking.getStart());
            existingBooking.setEnd(booking.getEnd());
        if (booking.getStatus() != null) {
            existingBooking.setStatus(booking.getStatus());
        } else {
            existingBooking.setStatus(BookingStatus.WAITING);
        }
        return bookingRepository.save(existingBooking);
    }

    @Override
    public List<Booking> getBookingsByBooker(Long userId, BookingState state) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now,
                    sort);
            case PAST -> bookingRepository.findByBookerIdAndEndAfter(userId, LocalDateTime.now(), sort);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            default -> throw new ValidationException("Unknown state: " + state);
        };
    }

    @Override
    public List<Booking> getBookingsByOwner(Long ownerId, BookingState state) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case ALL -> bookingRepository.findByOwnerId(ownerId, sort);
            case CURRENT -> bookingRepository.findByOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now,
                    sort);
            case PAST -> bookingRepository.findByOwnerIdAndEndBefore(ownerId, now, sort);
            case FUTURE -> bookingRepository.findByOwnerIdAndStartAfter(ownerId, now,
                    sort);
            case WAITING -> bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING,
                    sort);
            case REJECTED -> bookingRepository.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED,
                    sort);
            default -> throw new ValidationException("Unknown state: " + state);
        };
    }

    @Override
    public Booking approveBooking(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NotFoundException("Booking not found"));

        if (!booking.getOwner().getId().equals(ownerId)) {
            throw new AccessErrorException("You are not the owner of this item");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalStateException("Booking has already been approved or rejected");
        }

        booking.setStatus(approved ? APPROVED : BookingStatus.REJECTED);

        return bookingRepository.save(booking);
    }



}

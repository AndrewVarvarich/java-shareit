package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking createBooking(Long bookerId, BookingDto booking);

    Booking getBookingById(Long bookingId, Long userId);

    void deleteBooking(Long bookingId);

    Booking updateBooking(Booking booking, Long bookingId);

    List<Booking> getBookingsByBooker(Long userId, BookingState state);

    List<Booking> getBookingsByOwner(Long ownerId, BookingState state);

    Booking approveBooking(Long bookingId, Long ownerId, Boolean approved);

    Booking changeStatus(Long bookingId, Long ownerId, Boolean approved);
}

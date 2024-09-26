package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Long bookerId, BookingDto booking);

    Booking getBookingById(Long bookingId);

    void deleteBooking(Long bookingId);

    Booking updateBooking(Booking booking, Long bookingId);


    List<Booking> getBookingsByBooker(Long userId, BookingState state);

    List<Booking> getBookingsByOwner(Long ownerId, BookingState state);

    List<Booking> getBookingsByUser(Long userId, Long bookingId, BookingState state);

    Booking approveBooking(Long bookingId, Long ownerId, Boolean approved);
}

package ru.practicum.shareit.booking.service;

public enum BookingState {
    ALL,       // Все бронирования
    CURRENT,   // Текущие бронирования
    PAST,      // Завершённые бронирования
    FUTURE,    // Будущие бронирования
    WAITING,   // Ожидающие подтверждения
    REJECTED   // Отклонённые бронирования
}

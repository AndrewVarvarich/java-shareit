package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {


    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(final Exception e) {
        log.error("Ошибка валидации данных: {}.", e.getMessage());

        // Создаем ответ в нужной структуре
        Map<String, Object> response = new HashMap<>();
        response.put("type", "about:blank");
        response.put("title", "Bad Request");
        response.put("status", 400);
        response.put("detail", "Check that data you sent is correct");
        response.put("instance", "/items");

        // Если это MethodArgumentNotValidException, то собираем ошибки полей
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validationException = (MethodArgumentNotValidException) e;
            Map<String, String> fieldErrors = validationException.getBindingResult().getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage()
                    ));
            response.put("error", fieldErrors);
        } else {
            // Для других исключений (например, ValidationException) добавляем общее сообщение
            response.put("error", Map.of("available", "не должно равняться null")); // Или другая логика
        }

        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleThrowable(final Throwable e) {
        log.error("Возникла ошибка: {}.", e.getMessage(), e);

        // Создаем ответ для внутренних ошибок
        return Map.of(
                "type", "about:blank",
                "title", "Internal Server Error",
                "status", 500,
                "detail", "An unexpected error occurred",
                "instance", "/items",
                "error", Map.of("message", e.getMessage())
        );
    }
}
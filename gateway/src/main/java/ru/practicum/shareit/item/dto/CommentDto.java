package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private Long id;
    @NotBlank
    private String text;
    @NotBlank
    private String authorName;
    @FutureOrPresent
    private LocalDateTime created;
}

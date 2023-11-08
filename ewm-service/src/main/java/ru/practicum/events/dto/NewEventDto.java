package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Location;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NewEventDto {
    Long id;
    @NotEmpty
    @Size(max = 2000)
    @Size(min = 20)
    String annotation;
    Long category;
    @NotEmpty
    @Size(max = 7000)
    @Size(min = 20)
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;          //      may be LocalDateTime
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    @NotEmpty
    @Size(max = 120, min = 3)
    String title;
}

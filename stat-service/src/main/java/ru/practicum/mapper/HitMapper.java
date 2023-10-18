package ru.practicum.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Hit;

public class HitMapper {
    public static Hit toHit(EndpointHitDto endpointHitDto) {
        return Hit.builder()
                .ip(endpointHitDto.getIp())
                .uri(endpointHitDto.getUri())
                .app(endpointHitDto.getApp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }

    public static ViewStatsDto toViewDto(Hit hit) {
        return ViewStatsDto.builder()
                .uri(hit.getUri())
                .app(hit.getApp())
                .build();
    }
}

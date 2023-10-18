package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;

public interface HitService {
    void addHit(EndpointHitDto endpointHitDto);
}

package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.HitRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class HitServiceImpl implements HitService {
    private  final HitRepository hitRepository;

    @Transactional
    public void addHit(EndpointHitDto endpointHitDto) {
        log.info("hit method call");
        Hit hit = HitMapper.toHit(endpointHitDto);
        hitRepository.save(hit);

    }
}

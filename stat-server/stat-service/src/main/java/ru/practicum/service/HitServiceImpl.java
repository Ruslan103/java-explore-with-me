package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Transactional
    public void addHit(EndpointHitDto endpointHitDto) {
        log.info("addHit method call");
        Hit hit = HitMapper.toHit(endpointHitDto);
        hitRepository.save(hit);
    }

    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {
        log.info("getStats method call");
        List<ViewStatsDto> s;
        if (unique) {
            if (uris.isEmpty()) {
                //   уникальные, но без списка uri
                s = hitRepository.getUniqueStats(start, end);
            } else {
                // уникальные и со списком uri
                s = hitRepository.getUniqueStatWithUris(start, end, uris);
            }
        } else {
            if (uris.isEmpty()) {
                //   не уникальные, но без списка uri
                s = hitRepository.getAllStats(start, end);

            } else {
                // неуникальные и со списком ури
                s = hitRepository.getStatWithUrisAndDate(start, end, uris);
            }
        }
        return s;
    }
}

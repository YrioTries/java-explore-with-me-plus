package ru.practicum.explorewithme;

import ru.practicum.EndpointHitDto;
import ru.practicum.StatResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final AppRepository appRepository;

    @Override
    public void addHit(EndpointHitDto endpointHitDto) {
        App app = appRepository.findByName(endpointHitDto.getApp())
                .orElseGet(() -> {
                    App newApp = new App();
                    newApp.setName(endpointHitDto.getApp());
                    return appRepository.save(newApp);
                });

        Stat stat = new Stat();
        stat.setApp(app);
        stat.setUri(endpointHitDto.getUri());
        stat.setIp(endpointHitDto.getIp());
        stat.setCreated(endpointHitDto.getTimestamp());

        statRepository.save(stat);
    }

    @Override
    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            if (uris == null || uris.isEmpty()) {
                return statRepository.findUniqueStats(start, end);
            } else {
                return statRepository.findUniqueStatsByUris(start, end, uris);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                return statRepository.findStats(start, end);
            } else {
                return statRepository.findStatsByUris(start, end, uris);
            }
        }
    }
}
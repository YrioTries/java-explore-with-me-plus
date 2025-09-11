package ru.practicum.explorewithme;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;
    private final AppRepository appRepository;

    @Override
    public void createStat(HttpServletRequest request) {
        String appName = "ewm-main-service";
        App app = appRepository.findByName(appName)
                .orElseGet(() -> {
                    App newApp = new App();
                    newApp.setName(appName);
                    return appRepository.save(newApp);
                });

        Stat newStat = new Stat();
        newStat.setApp(app);
        newStat.setUri(request.getRequestURI());
        newStat.setIp(request.getRemoteAddr());
        newStat.setCreated(LocalDateTime.now());

        statRepository.save(newStat);
    }

    @Override
    public List<StatResponseDto> getStats(LocalDateTime start, LocalDateTime end,
                                          List<String> uris, Boolean unique) {
        if (Boolean.TRUE.equals(unique)) {
            return statRepository.getUniqueStats(start, end, uris);
        } else {
            return statRepository.getStats(start, end, uris);
        }
    }
}
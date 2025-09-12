package ru.practicum.explorewithme;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StatServiceImpl implements StatService {

    public void createStat(HttpServletRequest request) {
        Stat newStat = new Stat();
        newStat.setUri(request.getRequestURI());
        newStat.setIp(request.getRemoteAddr());
        newStat.setCreated(LocalDateTime.now());
    }

}

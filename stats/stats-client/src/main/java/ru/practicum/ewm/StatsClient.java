package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.EndpointHitInputDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsClient {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://stats-server";

    public ResponseEntity<Object> addHit(EndpointHitInputDto hitDto) {
        log.info("POST {}/hit", BASE_URL);
        return restTemplate.postForEntity(BASE_URL + "/hit", hitDto, Object.class);
    }

    public ResponseEntity<Object> getStats(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           List<String> uris,
                                           Boolean unique) {
        StringBuilder uri = new StringBuilder(BASE_URL + "/stats?unique=" + unique);
        if (start != null) uri.append("&start=").append(start);
        if (end != null) uri.append("&end=").append(end);
        if (uris != null && !uris.isEmpty()) {
            uri.append("&uris=").append(String.join(",", uris));
        }

        log.info("GET {}", uri);
        return restTemplate.getForEntity(uri.toString(), Object.class);
    }
}

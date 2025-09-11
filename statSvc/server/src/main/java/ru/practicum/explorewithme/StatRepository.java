package ru.practicum.explorewithme;

import ru.practicum.StatResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("SELECT new ru.practicum.StatResponseDto(a.name, s.uri, COUNT(s)) " +
            "FROM Stat s JOIN s.app a " +
            "WHERE s.created BETWEEN :start AND :end " +
            "GROUP BY a.name, s.uri " +
            "ORDER BY COUNT(s) DESC")
    List<StatResponseDto> findStats(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.StatResponseDto(a.name, s.uri, COUNT(s)) " +
            "FROM Stat s JOIN s.app a " +
            "WHERE s.created BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "GROUP BY a.name, s.uri " +
            "ORDER BY COUNT(s) DESC")
    List<StatResponseDto> findStatsByUris(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.StatResponseDto(a.name, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat s JOIN s.app a " +
            "WHERE s.created BETWEEN :start AND :end " +
            "GROUP BY a.name, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<StatResponseDto> findUniqueStats(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.StatResponseDto(a.name, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat s JOIN s.app a " +
            "WHERE s.created BETWEEN :start AND :end " +
            "AND s.uri IN :uris " +
            "GROUP BY a.name, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<StatResponseDto> findUniqueStatsByUris(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end,
                                                @Param("uris") List<String> uris);
}
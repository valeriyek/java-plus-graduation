package ru.practicum.dto;

import lombok.Getter;
import lombok.Setter;
/**
 * DTO географической локации события.
 * <p>Используется в {@link EventFullDto} для указания координат проведения.</p>
 *
 * <ul>
 *   <li>{@code lat} — широта (−90.0 ... +90.0);</li>
 *   <li>{@code lon} — долгота (−180.0 ... +180.0).</li>
 * </ul>
 */
@Getter
@Setter
public class LocationDto {
    private Float lat;
    private Float lon;
}

package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * DTO заявки на участие в событии.
 * <p>Используется в публичном и административном API для отображения статуса запроса.</p>
 *
 * <ul>
 *   <li>{@code created} — дата и время создания запроса (формат {@link Constants#FORMAT_DATETIME});</li>
 *   <li>{@code event} — идентификатор события, к которому подана заявка;</li>
 *   <li>{@code id} — идентификатор заявки;</li>
 *   <li>{@code requester} — идентификатор пользователя, подавшего заявку;</li>
 *   <li>{@code status} — текущий статус (например, PENDING, CONFIRMED, REJECTED, CANCELED).</li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class ParticipationRequestDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private String status;
}
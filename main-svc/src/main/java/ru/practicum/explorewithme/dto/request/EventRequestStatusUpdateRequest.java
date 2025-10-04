package ru.practicum.explorewithme.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.enums.RequestStatus;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {
    private Set<Long> requestIds;
    private RequestStatus status;
}
package ua.ukrpost.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ua.ukrpost.entity.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ua.ukrpost.constraint.RegexPattern;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@ToString
public class ShipmentTrackingDetailDto {
    private long id;
    private UUID shipmentUuid;
    private long postOfficeId;
    private ShipmentStatus shipmentStatus;
    @JsonFormat(shape = STRING, pattern = RegexPattern.DATE_TIME_PATTERN)
    private LocalDateTime date;
}

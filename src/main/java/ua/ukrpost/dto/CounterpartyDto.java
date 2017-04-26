package ua.ukrpost.dto;

import java.util.UUID;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CounterpartyDto {
    private UUID uuid;
    @Size(max = 255)
    private String name;
    private UUID postcodePoolUuid;
    @Size(max = 255)
    private String description;
}

package ua.ukrpost.dto.postid;

import ua.ukrpost.entity.ClientType;
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
public class ClientTypeDto {
    private ClientType type;
}

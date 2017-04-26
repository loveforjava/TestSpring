package ua.ukrpost.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class CityDto {
    private long id;
    @Size(max = 25)
    private String name;
    @Size(max = 45)
    private String district;
    @Size(max = 25)
    private String region;
    @Size(max = 25)
    private String country;
}

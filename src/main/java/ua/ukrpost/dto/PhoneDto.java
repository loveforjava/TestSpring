package ua.ukrpost.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PhoneDto {
    private long id;
    private String phoneNumber;
}

package com.opinta.dto;

import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Diarsid on 20.03.2017.
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClientDto {
    
    private long id;
    
    @Size(max = 255)
    private String name;
    
    @Size(max = 255)
    private String uniqueRegistrationNumber;
    
}

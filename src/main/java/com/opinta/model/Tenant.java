package com.opinta.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


/**
 * Tenant is the group of clients with the same virtual post office
 */
@Entity
@Data
@NoArgsConstructor
public class Tenant {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @OneToOne
    private VirtualPostOffice activeVirtualPostOffice;
    private String description;
}

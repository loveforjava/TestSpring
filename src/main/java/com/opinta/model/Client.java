package com.opinta.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Client {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String usreou;
    private String tin;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Address address;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}

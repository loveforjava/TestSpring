package com.opinta.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class VirtualPostOffice {
    @Id
    @GeneratedValue
    private long id;
    // 5 digits
    private String virtualPostcode;
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
    private boolean blocked;
}

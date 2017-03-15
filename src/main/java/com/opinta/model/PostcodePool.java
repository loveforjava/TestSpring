package com.opinta.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class PostcodePool {
    @Id
    @GeneratedValue
    private long id;
    // 7 digits
    private String number;
    @ManyToOne
    @JoinColumn(name = "virtual_post_office_id")
    private VirtualPostOffice virtualPostOffice;
}

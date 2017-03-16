package com.opinta.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * VirtualPostOffice is the group of clients with the same postcode
 */
@Entity
@Data
@NoArgsConstructor
public class VirtualPostOffice {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @OneToOne
    @NotNull
    private PostcodePool activePostcodePool;
    // TODO add field private List<PostcodePool> closedPostcodePools (unidirectional)
    private String description;
    @OneToMany(mappedBy = "virtualPostOffice")
    private List<Client> clients;
}

package com.opinta.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Customer {
    @Id
    @GeneratedValue
	private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    @Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	public Customer(String firstName, String lastName, String email, String mobile) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobile = mobile;
		this.dateOfBirth = new Date();
	}
}
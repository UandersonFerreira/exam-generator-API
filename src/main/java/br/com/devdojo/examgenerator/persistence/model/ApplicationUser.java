package br.com.devdojo.examgenerator.persistence.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class ApplicationUser extends AbstractEntity{
    @NotEmpty(message = "The field username cannot be empty")
    @Column(unique = true)
    private String username;
    @NotEmpty(message = "This field password cannot be empty")
    private String password;
    @OneToOne
    private Professor professor;


}//class

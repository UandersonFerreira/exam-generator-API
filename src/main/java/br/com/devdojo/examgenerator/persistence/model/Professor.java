package br.com.devdojo.examgenerator.persistence.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity
public class Professor extends AbstractEntity {

    @NotEmpty(message = "The field name cannot be empty")
    private String name;
    @Email(message = "This email is not valid")
    @NotEmpty(message = "This field email cannot be empty")
    @Column(unique = true)
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}//class

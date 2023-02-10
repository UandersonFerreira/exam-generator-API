package br.com.devdojo.examgenerator.persistence.model;

<<<<<<< HEAD
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;

@Entity
public class ApplicationUser extends AbstractEntity {
=======
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Entity
public class ApplicationUser extends AbstractEntity{
>>>>>>> origin/master
    @NotEmpty(message = "The field username cannot be empty")
    @Column(unique = true)
    private String username;
    @NotEmpty(message = "This field password cannot be empty")
    private String password;
    @OneToOne
    private Professor professor;

<<<<<<< HEAD
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
=======

>>>>>>> origin/master
}//class

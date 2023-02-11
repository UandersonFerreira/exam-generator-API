package br.com.devdojo.examgenerator.endpoint.v1;


import br.com.devdojo.examgenerator.persistence.model.ApplicationUser;
import br.com.devdojo.examgenerator.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/professor")
public class ProfessorEndpoint {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @GetMapping
    public ResponseEntity<?> hello(){
        final ApplicationUser user = customUserDetailsService.loadApplicationUserByUsername("uanderson");
        System.out.println();
        System.out.println("Nome: "+user.getUsername());
        System.out.println("Professor: "+user.getProfessor().getEmail());
        System.out.println("Senha: "+  user.getPassword());

        return new ResponseEntity<>("Hello Worl", HttpStatus.OK);
    }

}

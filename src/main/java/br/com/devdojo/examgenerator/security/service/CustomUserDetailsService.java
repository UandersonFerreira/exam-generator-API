package br.com.devdojo.examgenerator.security.service;

import br.com.devdojo.examgenerator.persistence.model.ApplicationUser;
import br.com.devdojo.examgenerator.persistence.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public CustomUserDetailsService(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser applicationUser = loadApplicationUserByUsername(username);//buscando um ApplicationUser pelo nome no DB
        return new CustomUserDetails(applicationUser);//armazenando o ApplicationUser recuperado do DB customizado, já que
        // o UserDetails é uma classe do Spring é não do próprio java.
    }

    public ApplicationUser loadApplicationUserByUsername(String username){
        //Verificando se o ApplicationUser existe no db e retornando ele
        //caso não exista irá lança uma exceção UsernameNotFoundException.
        return Optional.ofNullable(applicationUserRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("ApplicationUser not found with name ["+username+"]"));

    }

    private final static class CustomUserDetails extends ApplicationUser implements UserDetails{
        /*
        Classe privada criada, somente para que seja possível contornnar o fato de
        que precisamos retornar um ApplicationUser ao invés de UserDetails,
        sem quebrar o contrato do method sobreescrito loadUserByUsername().

        Para isso criamos uma classe que extends ApplicationUser e implements UserDetails
        para poder passar no teste:  é um ?

        E então criamos um construtor passando um ApplicationUser, que irá
        chamar o próprio construtor da classe passada no argumento. Portanto
        sempre que se criar um novo objeto de CustomUserDetails(que é um ApplicationUser e UserDetails)
        estaremos retornando sempre um objeto ApplicationUser por de baixo dos panos.

         */
        private CustomUserDetails(ApplicationUser applicationUser) {
            super(applicationUser);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
           List<GrantedAuthority> authorityListProfesor = AuthorityUtils.createAuthorityList("ROLE_PROFESSOR");
           List<GrantedAuthority> authorityListStudent = AuthorityUtils.createAuthorityList("ROLE_STUDENT");
           return this.getProfessor() != null ? authorityListProfesor : authorityListStudent;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}//class

/*
UserDetails é uma class do spring que contém
informações padrões que todo sistema que utilizam
algum tipo de segurança possue.
Tais como:
    String getPassword();
    String getUsername();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();


 */

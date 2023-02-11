package br.com.devdojo.examgenerator.security.filter;

import br.com.devdojo.examgenerator.persistence.model.ApplicationUser;
import br.com.devdojo.examgenerator.security.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.com.devdojo.examgenerator.security.filter.Constants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final CustomUserDetailsService customUserDetailsService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager,
                                  CustomUserDetailsService customUserDetailsService) {
        super(authenticationManager);
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        //realiza a validação de acesso a um determinado endpoint
        String header = request.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith(TOKEN_PREFIX)){
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);//add para que seja possivel pegar as informações no endpoint
        chain.doFilter(request, response);
    }//method

    private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request){
        String token = request.getHeader(HEADER_STRING);
        if (token == null) return null;
        String username = Jwts.parser().setSigningKey(SECRET)//fazendo o parsse do token utilizando a key de acesso SECRET
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))//Removendo o TOKEN_PREFIX = "Bearer"; deixanod só o token
                .getBody()
                .getSubject();//pegando o nome do usuário autenticado, setado no method successfulAuthentication() ao gerar o token.

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        ApplicationUser applicationUser = customUserDetailsService.loadApplicationUserByUsername(username);
        return username != null ?  new UsernamePasswordAuthenticationToken(applicationUser, null, userDetails.getAuthorities()) : null;

       /*
           if (username != null){
                return new UsernamePasswordAuthenticationToken(applicationUser, null, userDetails.getAuthorities());
            }else {
                return null;
            }
        */

    }//method
}//class

/*
JWTAuthorizationFilter -> O que você pode fazer aqui dentro?

 doFilter(): Faz com que o próximo filtro da cadeia seja
 chamado ou, se o filtro de chamada for o último filtro da
  cadeia, faz com que o recurso no final da cadeia seja chamado.


 */

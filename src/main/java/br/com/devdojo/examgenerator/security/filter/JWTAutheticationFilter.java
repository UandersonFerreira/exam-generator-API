package br.com.devdojo.examgenerator.security.filter;

import br.com.devdojo.examgenerator.persistence.model.ApplicationUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static br.com.devdojo.examgenerator.security.filter.Constants.*;

public class JWTAutheticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;//Responsavel auntenticação

    public JWTAutheticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //Realização da tentativa de autenticação para que seja possível gerar o token
        try {
            ApplicationUser user = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
            return authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }//method

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //PROCESSO DE GERAÇÃO DE TOKEN - Para gerar o token é necessário está autenticado
        super.successfulAuthentication(request, response, chain, authResult);
        //Data de validade  do Token
        ZonedDateTime expTimeUTC = ZonedDateTime //Trabalha com os horarios neutros UTC
                .now(ZoneOffset.UTC) //pega o HORÁRIO ATUAL da Área neutra entre os horarios dos paises
                .plus(EXPIRATION_TIME, ChronoUnit.MILLIS);// ADICIONA mais 1day que o valor da constante que definimos em Milissegundos.

        String token = Jwts.builder()
                .setSubject(((ApplicationUser) authResult.getPrincipal()).getUsername())//setando o nome do usuário autenticado
                .setExpiration(Date.from(expTimeUTC.toInstant())) //setando uma instancia da data de expiração do token
                .signWith(SignatureAlgorithm.HS256, SECRET) // a chave de assinatura específica do algoritmo a ser usada para validar o JWT
                .compact(); // para mandar gerar o token

        //{"token":"Bearer token", "exp":"date"}
        //"{\"token\":" + addQuotes(TOKEN_PREFIX + token) + ", \"exp\": "+ addQuotes(expTimeUTC.toString())+"}";
        token = TOKEN_PREFIX + token;
        String tokenJson = "{\"token\":" + addQuotes(token) + ", \"exp\": " + addQuotes(expTimeUTC.toString()) + "}";

        response.getWriter().write(tokenJson); //adicionando/escrevendo o tokenJson em algum lugar da nossa resposta (HttpServletResponse)
        response.addHeader("Content Type", "application/json;charset=UTF-8");// Add o Content Type ao cabeçalho da resposta
        response.addHeader(HEADER_STRING, token);//Add HEADER_STRING(Authorization) e o token ao cabeçalho da resposta

    }//method

    private String addQuotes(String value) {
        return "\"" +
                value +
                "\"";
    }

}//class

/*
JWTAutheticationFilter -> Quem é você?

request.getInputStream() -> pode lancar um IOException

UsernamePasswordAuthenticationToken -> Objeto do proprio Spring

Jwts -> vem do pacote io.jsonwebtoken.Jwts


 */

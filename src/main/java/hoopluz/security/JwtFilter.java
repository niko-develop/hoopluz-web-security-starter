package hoopluz.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hoopluz.security.common.JwtToken;
import hoopluz.security.common.ResponseEntity;
import hoopluz.security.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final Jwt jwt;

  @Autowired
  public JwtFilter(Jwt jwt){
    this.jwt = jwt;
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain
  ) {

    try {
      String token = this.getToken(request);
      if (Objects.isNull(token)) {
        throw new UnauthorizedException();
      }

      JwtToken jwtToken = jwt.decode(token);
      UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(jwtToken, token, AuthorityUtils.NO_AUTHORITIES);

      authentication.setDetails(new WebAuthenticationDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception exception) {
      this.onException(exception, response);
    }
  }

  private String getToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (Objects.nonNull(token)) {
      return token;
    }
    return request.getParameter("Authorization");
  }

  private void onException(Exception exception, HttpServletResponse response) {
    ResponseEntity entity = ResponseEntity.fromException(exception);
    response.setStatus(entity.getCode());
    try {
      response.getWriter().write(convertObjectToJson(entity));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String convertObjectToJson(Object object) throws JsonProcessingException {
    if (object == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }

}

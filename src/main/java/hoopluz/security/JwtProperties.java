package hoopluz.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(
  prefix = "hoopluz.jwt",
  ignoreInvalidFields = true,
  ignoreUnknownFields = false
)
public class JwtProperties {

  private String signKey;

  private Long expires = 30 * 60 * 1000L;

  private Long invalid = 2 * 24 * 560 * 1000L;


}

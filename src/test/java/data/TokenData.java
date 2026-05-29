package data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import models.lombok.RequestTokenDTO;

@Data
@AllArgsConstructor
public class TokenData {

    @Getter
    private final String username = "admin";
    private final String password = "password123";

    public RequestTokenDTO generateToken() {

        return RequestTokenDTO.builder()
                .username(username)
                .password(password)
                .build();
    }
}

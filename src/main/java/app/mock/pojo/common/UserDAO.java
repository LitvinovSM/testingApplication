package app.mock.pojo.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDAO {

    @JsonProperty("userMail")
    private String userMail;

    @JsonProperty("id")
    private String id;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("userSurname")
    private String userSurname;

    @JsonProperty("isVip")
    private Boolean isVip;

    @JsonProperty("expirationDate")
    private LocalDate expirationDate;

}

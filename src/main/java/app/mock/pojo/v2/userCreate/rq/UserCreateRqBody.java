package app.mock.pojo.v2.userCreate.rq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRqBody{

	@JsonProperty("userMail")
	private String userMail;

	@JsonProperty("userName")
	private String userName;

	@JsonProperty("userSurname")
	private String userSurname;

	@JsonProperty("isVip")
	private Boolean isVip;

	@JsonProperty("expirationDate")
	private String expirationDate;

}
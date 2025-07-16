package app.mock.pojo.v1.userUpdate.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRsBody{

	@JsonProperty("userMail")
	private String userMail;

	@JsonProperty("id")
	private String id;

	@JsonProperty("userName")
	private String userName;

	@JsonProperty("userSurname")
	private String userSurname;
}
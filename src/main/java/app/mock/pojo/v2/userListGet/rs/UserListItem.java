package app.mock.pojo.v2.userListGet.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserListItem{

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
	private String expirationDate;

	@JsonProperty("isExpired")
	private Boolean isExpired;
}
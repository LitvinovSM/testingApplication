package app.mock.pojo.v2.userListGet.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserListGetRsBody{

	@JsonProperty("userList")
	private List<UserListItem> userList;
}
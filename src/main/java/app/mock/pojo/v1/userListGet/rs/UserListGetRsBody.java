package app.mock.pojo.v1.userListGet.rs;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserListGetRsBody{

	@JsonProperty("userList")
	private List<UserListItem> userList;
}
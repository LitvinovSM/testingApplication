package app.mock.pojo.v1.userDelete.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDeleteRsBody{

	@JsonProperty("statusMessage")
	private String statusMessage;
}
package app.mock.pojo.v2.userCreate.rsError;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRsErrorBody{

	//TODO баг № 7 - несоответствие контракту, поле должно называться просто error
	@JsonProperty("errorMessage")
	private String error;
}
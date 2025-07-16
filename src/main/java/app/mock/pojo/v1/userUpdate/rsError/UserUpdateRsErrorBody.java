package app.mock.pojo.v1.userUpdate.rsError;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRsErrorBody {

    @JsonProperty("error")
    private String error;

}

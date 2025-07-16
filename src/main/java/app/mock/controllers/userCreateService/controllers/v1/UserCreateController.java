package app.mock.controllers.userCreateService.controllers.v1;

import app.mock.controllers.userCreateService.service.UserCreateService;
import app.mock.pojo.v1.userCreate.rq.UserCreateRqBody;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.mock.controllers.userCreateService.service.UserCreateService.makeProcessingAndSuccessfulResponse;
import static app.mock.controllers.userCreateService.service.UserCreateService.makeResponseBasedOnProcessingResult;
import static app.mock.utils.CommonUtil.buildDefaultHttpHeaders;
import static app.mock.utils.JsonUtil.convertJsonToObject;

@RestController
@RequestMapping("/v1")
public class UserCreateController {

    @PostMapping("/userCreate")
    public ResponseEntity<String> userCreateRequest(@RequestHeader(required = false) String employeeId,
                                                    @RequestHeader(required = false) String employeeSystem,
                                                    @RequestBody String requestBody){
        return makeResponseBasedOnProcessingResult(employeeId,employeeSystem,requestBody);
    }


}

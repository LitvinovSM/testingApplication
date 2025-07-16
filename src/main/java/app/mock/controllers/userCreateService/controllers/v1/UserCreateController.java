package app.mock.controllers.userCreateService.controllers.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.mock.controllers.userCreateService.service.UserCreateService.makeResponseUserCreateServiceV1BasedOnProcessingResult;

@RestController
@RequestMapping("/v1")
public class UserCreateController {

    @PostMapping("/userCreate")
    public ResponseEntity<String> userCreateRequest(@RequestHeader(required = false) String employeeId,
                                                    @RequestHeader(required = false) String employeeSystem,
                                                    @RequestBody String requestBody){
        return makeResponseUserCreateServiceV1BasedOnProcessingResult(employeeId,employeeSystem,requestBody);
    }


}

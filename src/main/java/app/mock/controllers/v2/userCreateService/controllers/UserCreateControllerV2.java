package app.mock.controllers.v2.userCreateService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.mock.controllers.v2.userCreateService.service.UserCreateServiceV2.makeResponseUserCreateServiceV2BasedOnProcessingResult;

@RestController
@RequestMapping("/v2")
public class UserCreateControllerV2 {

    @PostMapping("/userCreate")
    public ResponseEntity<String> userCreateRequest(@RequestHeader(required = false) String employeeId,
                                                    @RequestHeader(required = false) String employeeSystem,
                                                    @RequestBody String requestBody){
        return makeResponseUserCreateServiceV2BasedOnProcessingResult(employeeId,employeeSystem,requestBody);
    }


}

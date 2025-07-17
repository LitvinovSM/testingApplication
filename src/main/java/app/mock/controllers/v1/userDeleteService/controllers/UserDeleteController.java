package app.mock.controllers.v1.userDeleteService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.mock.controllers.v1.userDeleteService.service.UserDeleteService.makeResponseUserDeleteServiceV1BasedOnProcessingResult;

@RestController
@RequestMapping("/v1")
public class UserDeleteController {

    @DeleteMapping("/userDelete")
    public ResponseEntity<String> userDeleteRequest(@RequestHeader(required = false) String employeeId,
                                                    @RequestHeader(required = false) String employeeSystem,
                                                    @RequestBody String requestBody) {
        return makeResponseUserDeleteServiceV1BasedOnProcessingResult(employeeId, employeeSystem, requestBody);
    }

}

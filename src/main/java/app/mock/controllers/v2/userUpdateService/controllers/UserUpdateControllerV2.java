package app.mock.controllers.v2.userUpdateService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.mock.controllers.v1.userUpdateService.service.UserUpdateService.makeResponseUserUpdateServiceV1BasedOnProcessingResult;

@RestController
@RequestMapping("/v2")
public class UserUpdateControllerV2 {

    @PutMapping("/userUpdate")
    public ResponseEntity<String> userUpdateRequest(@RequestHeader(required = false) String employeeId,
                                                    @RequestHeader(required = false) String employeeSystem,
                                                    @RequestBody String requestBody) {
        return makeResponseUserUpdateServiceV1BasedOnProcessingResult(employeeId, employeeSystem, requestBody);
    }

}

package app.mock.controllers.userDeleteService.controllers.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.mock.controllers.userDeleteService.service.UserDeleteService.makeResponseUserDeleteServiceV1BasedOnProcessingResult;

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

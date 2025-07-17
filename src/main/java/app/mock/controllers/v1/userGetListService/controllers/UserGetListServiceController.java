package app.mock.controllers.v1.userGetListService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.mock.controllers.v1.userGetListService.service.UserGetListService.makeResponseUserGetListServiceV1BasedOnProcessingResult;


@RestController
@RequestMapping("/v1")
public class UserGetListServiceController {


        @GetMapping("/userGetList")
        public ResponseEntity<String> userGetListRequest(@RequestHeader(required = false) String employeeId,
                                                        @RequestHeader(required = false) String employeeSystem) {
            return makeResponseUserGetListServiceV1BasedOnProcessingResult(employeeId, employeeSystem);
        }


}

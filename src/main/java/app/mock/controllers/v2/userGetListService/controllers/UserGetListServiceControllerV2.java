package app.mock.controllers.v2.userGetListService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static app.mock.controllers.v2.userGetListService.service.UserGetListServiceV2.makeResponseUserGetListServiceV2BasedOnProcessingResult;


@RestController
@RequestMapping("/v2")
public class UserGetListServiceControllerV2 {


        @GetMapping("/userGetList")
        public ResponseEntity<String> userGetListRequest(@RequestHeader(required = false) String employeeId,
                                                        @RequestHeader(required = false) String employeeSystem) {
            return makeResponseUserGetListServiceV2BasedOnProcessingResult(employeeId, employeeSystem);
        }


}

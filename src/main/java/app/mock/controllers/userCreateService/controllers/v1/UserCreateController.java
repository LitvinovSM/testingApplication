package app.mock.controllers.userCreateService.controllers.v1;

import app.mock.controllers.userCreateService.service.UserCreateService;
import app.mock.pojo.common.UserDAO;
import app.mock.pojo.v1.userCreate.rq.UserCreateRqBody;
import app.mock.pojo.v1.userCreate.rs.UserCreateRsBody;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static app.mock.controllers.userCreateService.service.UserCreateService.makeProcessingAndSuccessfulResponse;
import static app.mock.utils.CommonConstants.CONTENT_TYPE_HEADER_NAME;
import static app.mock.utils.CommonConstants.CONTENT_TYPE_HEADER_VALUE;
import static app.mock.utils.CommonUtil.buildDefaultHttpHeaders;
import static app.mock.utils.JsonUtil.convertJsonToObject;

@RestController
@RequestMapping("/v1")
public class UserCreateController {

    @PostMapping("/userCreate")
    public ResponseEntity<String> userCreateRequest(@RequestHeader(required = false) String employeeId,
                                                    @RequestHeader(required = false) String employeeSystem,
                                                    @RequestBody String requestBody){
        Pair<HttpStatus,String> validationResult = UserCreateService.makeValidation(employeeId,employeeSystem,requestBody);

        if (validationResult.getLeft().equals(HttpStatus.BAD_REQUEST)){
            return new ResponseEntity<>(
                    validationResult.getRight(), buildDefaultHttpHeaders(), HttpStatus.BAD_REQUEST);
        } else if (validationResult.getLeft().equals(HttpStatus.INTERNAL_SERVER_ERROR)){
            return new ResponseEntity<>(
                    validationResult.getRight(), buildDefaultHttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            //Если прошли все валидации - конвертируем запрос
            UserCreateRqBody userCreateRqBody = convertJsonToObject(requestBody,UserCreateRqBody.class);
            //Билдим тело ответа (да, костыльно, знаю, можно было в одну строку, но мне так понятнее)
            String responseBody = makeProcessingAndSuccessfulResponse(userCreateRqBody);
            return new ResponseEntity<>(
                    responseBody, buildDefaultHttpHeaders(), HttpStatus.OK);
        }
    }


}

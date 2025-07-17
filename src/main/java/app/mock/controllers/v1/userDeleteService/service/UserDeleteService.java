package app.mock.controllers.v1.userDeleteService.service;


import app.mock.pojo.v1.userDelete.rq.UserDeleteRqBody;
import app.mock.pojo.v1.userDelete.rs.UserDeleteRsBody;
import app.mock.pojo.v1.userDelete.rsError.UserDeleteRsErrorBody;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static app.mock.storage.UsersStorage.*;
import static app.mock.utils.CommonUtil.*;
import static app.mock.utils.JsonUtil.convertJsonToObject;
import static app.mock.utils.JsonUtil.convertToJson;

public class UserDeleteService {

    private static final String employeeIdHeaderName = "employeeId";
    private static final String employeeSystemHeaderName = "employeeSystem";
    private static final String userNameFieldName = "userName";
    private static final String userSurnameFieldName = "userSurname";
    private static final String userMailFieldName = "userMail";

    /**Валидация заголовков*/
    protected static Pair<HttpStatus,String> verifyRequiredHeaders(String employeeIdHeaderValue, String employeeSystemHeaderValue){
        String errorMessage = "";
        HttpStatus httpStatus = HttpStatus.OK;
        //Проводим проверки
        if (isNullOrEmpty(employeeIdHeaderValue) && isNullOrEmpty(employeeSystemHeaderValue)){
            errorMessage = String.format("Не переданы обязательные заголовки: %s и %s",employeeIdHeaderName,employeeSystemHeaderName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (isNullOrEmpty(employeeIdHeaderValue)){
            errorMessage = String.format("Не передан обязательный заголовок: %s",employeeIdHeaderName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (isNullOrEmpty(employeeSystemHeaderValue)){
            errorMessage = String.format("Не передан обязательный заголовок: %s",employeeSystemHeaderName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (employeeSystemHeaderValue.length()!=4){
            errorMessage = String.format("Длина значения заголовка: %s должна быть равна 4 символам",employeeSystemHeaderName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            try {
                Integer employeeIdValue = Integer.parseInt(employeeIdHeaderValue);
                if (employeeIdValue<1 || employeeIdValue>100){
                    errorMessage = String.format("Значение заголовка: %s должно быть в интервале от 1 до 100 включительно",employeeIdHeaderName);
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            } catch (Exception e) {
                errorMessage = String.format("В заголовок: %s переданы не целые числовые значения",employeeIdHeaderName);
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        }
        return Pair.of(httpStatus,errorMessage);
    }


    /**Валидация тела запроса*/
    protected static Pair<HttpStatus,String> verifyRequestBody(String requestBody){
        String errorMessage = "";
        HttpStatus httpStatus = HttpStatus.OK;
        // пытаемся сконвертить юзера
        UserDeleteRqBody user = new UserDeleteRqBody();
        try {
            user = convertJsonToObject(requestBody, UserDeleteRqBody.class);
        } catch (Exception e) {
            errorMessage = String.format("Не удалось конвертировать тело запроса в класс UserDeleteRqBody. Полный текст ошибки: %s",getStackTraceAsString(e));
            System.out.println(getStackTraceAsString(e));
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            return Pair.of(httpStatus,errorMessage);
        }
        //Пытаемся проверить является ли id в формате UUID
        try {
            UUID id = UUID.fromString(user.getId());
        } catch (Exception e) {
            errorMessage = String.format("Идентификатор пользователя: %s передан не в формате UUID",user.getId());
            System.out.println(getStackTraceAsString(e));
            httpStatus = HttpStatus.BAD_REQUEST;
            return Pair.of(httpStatus,errorMessage);
        }
        //Пытаемся получить существующего пользователя
        if (getUserByUuid(user.getId())==null){
            errorMessage = String.format("Пользователя с идентификатором: %s не существует",user.getId());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return Pair.of(httpStatus,errorMessage);
    }

    /**Общий метод проверки валидации*/
    public static Pair<HttpStatus,String> makeValidation(String employeeIdHeaderValue, String employeeSystemHeaderValue,String requestBody){
        String errorMessage = "";
        HttpStatus httpStatus = HttpStatus.OK;
        Pair<HttpStatus,String> resultPair = Pair.of(httpStatus,errorMessage);
        Pair<HttpStatus,String> headerVerificationResult = verifyRequiredHeaders(employeeIdHeaderValue,employeeSystemHeaderValue);
        if (headerVerificationResult.getLeft().equals(HttpStatus.BAD_REQUEST)){
            resultPair = headerVerificationResult;
        }
        Pair<HttpStatus,String> bodyVerificationResult = verifyRequestBody(requestBody);
        if (bodyVerificationResult.getLeft().equals(HttpStatus.BAD_REQUEST) || bodyVerificationResult.getLeft().equals(HttpStatus.INTERNAL_SERVER_ERROR)){
            resultPair = bodyVerificationResult;
        }
        return resultPair;
    }


    public static String makeProcessingAndSuccessfulResponse(UserDeleteRqBody userDeleteRqBody){
        //Удаляем из хранилища по идентификатору
        removeFromStorageById(userDeleteRqBody.getId());
        //Билдим ответ
        UserDeleteRsBody userUpdateRsBody = UserDeleteRsBody.builder()
                .statusMessage(String.format("Пользователь с идентификатором %s успешно удален",userDeleteRqBody.getId()))
                .build();
        return convertToJson(userUpdateRsBody);
    }

    public static ResponseEntity<String> makeResponseUserDeleteServiceV1BasedOnProcessingResult(String employeeIdHeaderValue, String employeeSystemHeaderValue, String requestBody){
        Pair<HttpStatus,String> validationResult = makeValidation(employeeIdHeaderValue,employeeSystemHeaderValue,requestBody);
        //Для 500 ошибки
        if (validationResult.getLeft().equals(HttpStatus.INTERNAL_SERVER_ERROR)){
            UserDeleteRsErrorBody userUpdateRsErrorBody = new UserDeleteRsErrorBody(validationResult.getRight());
            String resultBody = convertToJson(userUpdateRsErrorBody);
            return new ResponseEntity<>(
                    resultBody, buildDefaultHttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //Для 400 ошибки
        else if (validationResult.getLeft().equals(HttpStatus.BAD_REQUEST)){
            UserDeleteRsErrorBody userUpdateRsErrorBody = new UserDeleteRsErrorBody(validationResult.getRight());
            String resultBody = convertToJson(userUpdateRsErrorBody);
            return new ResponseEntity<>(
                    resultBody, buildDefaultHttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        else {
            //Если прошли все валидации - конвертируем запрос
            UserDeleteRqBody userDeleteRsBody = convertJsonToObject(requestBody,UserDeleteRqBody.class);
            //Билдим тело ответа (да, костыльно, знаю, можно было в одну строку, но мне так понятнее)
            String responseBody = makeProcessingAndSuccessfulResponse(userDeleteRsBody);
            return new ResponseEntity<>(
                    responseBody, buildDefaultHttpHeaders(), HttpStatus.OK);
        }
    }


}

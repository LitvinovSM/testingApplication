package app.mock.controllers.v2.userUpdateService.service;

import app.mock.pojo.common.UserDAO;
import app.mock.pojo.v2.userUpdate.rq.UserUpdateRqBody;
import app.mock.pojo.v2.userUpdate.rs.UserUpdateRsBody;
import app.mock.pojo.v2.userUpdate.rsError.UserUpdateRsErrorBody;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

import static app.mock.storage.UsersStorage.*;
import static app.mock.utils.CommonConstants.DATE_TIME_VALID_FORMAT;
import static app.mock.utils.CommonUtil.*;
import static app.mock.utils.JsonUtil.convertJsonToObject;
import static app.mock.utils.JsonUtil.convertToJson;


public class UserUpdateServiceV2 {


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
        UserUpdateRqBody user = new UserUpdateRqBody();
        try {
            user = convertJsonToObject(requestBody, UserUpdateRqBody.class);
        } catch (Exception e) {
            errorMessage = String.format("Не удалось конвертировать тело запроса в класс UserUpdateRqBody. Полный текст ошибки: %s",getStackTraceAsString(e));
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
        else if (isNullOrEmpty(user.getUserName())){
            errorMessage = String.format("Поле %s является обязательным для заполнения",userNameFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        else if (user.getUserName().length()<2 || user.getUserName().length()>50){
            errorMessage = String.format("Длина поля %s должна быть от 2 до 50 символов включительно",userNameFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        else if (isNullOrEmpty(user.getUserSurname())){
            errorMessage = String.format("Поле %s является обязательным для заполнения",userSurnameFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (isNullOrEmpty(user.getUserMail())){
            errorMessage = String.format("Поле %s является обязательным для заполнения",userMailFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        else if (user.getUserSurname().length()<2 || user.getUserSurname().length()>50){
            errorMessage = String.format("Длина поля %s должна быть от 2 до 50 символов включительно",userSurnameFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (user.getUserMail().length()<5){
            errorMessage = String.format("Длина поля %s должна быть больше 4 символов",userMailFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (!isValidEmail(user.getUserMail())){
            errorMessage = String.format("Поле %s имеет некорректный формат",userMailFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (!isEmailUniqueComparingToOtherUsers(user.getUserMail(),user.getId())){
            errorMessage = String.format("Значение e-mail: %s не является уникальным и принадлежит другому пользователю",user.getUserMail());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        else if (user.getExpirationDate()!=null){
            try{
                LocalDate expirationDate = convertStringToDateWithSomePattern(user.getExpirationDate(),DATE_TIME_VALID_FORMAT, LocalDate.class);
                if (!expirationDate.isAfter(LocalDate.now())){
                    //TODO: Баг №4 - якобы таймаут
                    Thread.sleep(10000);
                    errorMessage = String.format("На этом моменте вам надо включить воображение и представить, что если вы передаете некорректную дату (меньше или равную текущей): %s - что-то идет не так на стороне сервера или базы данных и запрос вываливается в таймаут",user.getExpirationDate());
                    httpStatus = HttpStatus.GATEWAY_TIMEOUT;
                }
            } catch (Exception e) {
                errorMessage = String.format("Значение expirationDate: %s передано в некорректном формате. Корректный формат yyyy-MM-dd",user.getExpirationDate());
                httpStatus = HttpStatus.BAD_REQUEST;
            }
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
        if (bodyVerificationResult.getLeft().equals(HttpStatus.BAD_REQUEST) || bodyVerificationResult.getLeft().equals(HttpStatus.INTERNAL_SERVER_ERROR) || bodyVerificationResult.getLeft().equals(HttpStatus.GATEWAY_TIMEOUT)){
            resultPair = bodyVerificationResult;
        }
        return resultPair;
    }


    public static String makeProcessingAndSuccessfulResponse(UserUpdateRqBody userUpdateRqBody){
        //Билдим DAO для хранилища
        UserDAO userDAO = UserDAO.builder()
                .id(userUpdateRqBody.getId())
                .userMail(userUpdateRqBody.getUserMail())
                .userName(userUpdateRqBody.getUserName())
                .userSurname(userUpdateRqBody.getUserSurname())
                .isVip(userUpdateRqBody.getIsVip())
                .build();
        if (userUpdateRqBody.getExpirationDate()!=null){
            //TODO Баг №5 - "разрабочик" почему то решил минусовать один день в ответ
            userDAO.setExpirationDate(convertStringToDateWithSomePattern(userUpdateRqBody.getExpirationDate(),DATE_TIME_VALID_FORMAT, LocalDate.class).minusDays(1));
        }
        //Помещаем его в хранилище
        USERS_STORAGE.put(userUpdateRqBody.getUserMail(),userDAO);
        //Билдим ответ
        UserUpdateRsBody userUpdateRsBody = UserUpdateRsBody.builder()
                .id(userUpdateRqBody.getId())
                .userMail(userUpdateRqBody.getUserMail())
                .userName(userUpdateRqBody.getUserName())
                .userSurname(userUpdateRqBody.getUserSurname())
                .isVip(userUpdateRqBody.getIsVip())

                .build();
        if (userUpdateRqBody.getExpirationDate()!=null) {
            //TODO продолжение бага №5
            userUpdateRsBody.setExpirationDate(userDAO.getExpirationDate().toString());
        }
        return convertToJson(userUpdateRsBody);
    }

    public static ResponseEntity<String> makeResponseUserUpdateServiceV2BasedOnProcessingResult(String employeeIdHeaderValue, String employeeSystemHeaderValue, String requestBody){
        Pair<HttpStatus,String> validationResult = makeValidation(employeeIdHeaderValue,employeeSystemHeaderValue,requestBody);
        //Для 500 ошибки
        if (validationResult.getLeft().equals(HttpStatus.INTERNAL_SERVER_ERROR)){
            UserUpdateRsErrorBody userUpdateRsErrorBody = new UserUpdateRsErrorBody(validationResult.getRight());
            String resultBody = convertToJson(userUpdateRsErrorBody);
            return new ResponseEntity<>(
                    resultBody, buildDefaultHttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //TODO продолжение бага №4
        if (validationResult.getLeft().equals(HttpStatus.GATEWAY_TIMEOUT)){
            UserUpdateRsErrorBody userUpdateRsErrorBody = new UserUpdateRsErrorBody(validationResult.getRight());
            String resultBody = convertToJson(userUpdateRsErrorBody);
            return new ResponseEntity<>(
                    resultBody, buildDefaultHttpHeaders(), HttpStatus.GATEWAY_TIMEOUT);
        }
        //Для 400 ошибки
        else if (validationResult.getLeft().equals(HttpStatus.BAD_REQUEST)){
            UserUpdateRsErrorBody userUpdateRsErrorBody = new UserUpdateRsErrorBody(validationResult.getRight());
            String resultBody = convertToJson(userUpdateRsErrorBody);
            return new ResponseEntity<>(
                    resultBody, buildDefaultHttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        else {
            //Если прошли все валидации - конвертируем запрос
            UserUpdateRqBody userUpdateRqBody = convertJsonToObject(requestBody,UserUpdateRqBody.class);
            //Билдим тело ответа (да, костыльно, знаю, можно было в одну строку, но мне так понятнее)
            String responseBody = makeProcessingAndSuccessfulResponse(userUpdateRqBody);
            return new ResponseEntity<>(
                    responseBody, buildDefaultHttpHeaders(), HttpStatus.OK);
        }
    }

}

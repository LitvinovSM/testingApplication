package app.mock.controllers.v2.userCreateService.service;



import app.mock.pojo.common.UserDAO;
import app.mock.pojo.v2.userCreate.rq.UserCreateRqBody;
import app.mock.pojo.v2.userCreate.rs.UserCreateRsBody;
import app.mock.pojo.v2.userCreate.rsError.UserCreateRsErrorBody;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

import static app.mock.storage.UsersStorage.USERS_STORAGE;
import static app.mock.storage.UsersStorage.isEmailUnique;
import static app.mock.utils.CommonConstants.DATE_TIME_VALID_FORMAT;
import static app.mock.utils.CommonUtil.*;
import static app.mock.utils.JsonUtil.convertJsonToObject;
import static app.mock.utils.JsonUtil.convertToJson;

public class UserCreateServiceV2 {

    private static final String employeeIdHeaderName = "employeeId";
    private static final String employeeSystemHeaderName = "employeeSystem";
    private static final String userNameFieldName = "userName";
    private static final String userSurnameFieldName = "userSurname";
    private static final String userMailFieldName = "userMail";

    /**Валидация заголовков*/
    protected static Pair<HttpStatus,String> verifyRequiredHeaders(String employeeIdHeaderValue, String employeeSystemHeaderValue){
        String errorMessage = "";
        HttpStatus httpStatus = HttpStatus.OK;

        if (isNullOrEmpty(employeeIdHeaderValue) && isNullOrEmpty(employeeSystemHeaderValue)){
            errorMessage = String.format("Не переданы обязательные заголовки: %s и %s",employeeIdHeaderName,employeeSystemHeaderName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (isNullOrEmpty(employeeIdHeaderValue)){
            errorMessage = String.format("Не передан обязательный заголовок: %s",employeeIdHeaderName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (isNullOrEmpty(employeeSystemHeaderValue)){
            errorMessage = String.format("Не передан обязательный заголовок: %s",employeeSystemHeaderName);
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        else if (employeeSystemHeaderValue.length()!=4){
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
        UserCreateRqBody user = new UserCreateRqBody();
        try {
            user = convertJsonToObject(requestBody, UserCreateRqBody.class);
        } catch (Exception e) {
            errorMessage = String.format("Не удалось конвертировать тело запроса в класс UserCreateRqBody. Полный текст ошибки: %s",getStackTraceAsString(e));
            System.out.println(getStackTraceAsString(e));
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            return Pair.of(httpStatus,errorMessage);
        }
        if (isNullOrEmpty(user.getUserName())){
            errorMessage = String.format("Поле %s является обязательным для заполнения",userNameFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (isNullOrEmpty(user.getUserSurname())){
            errorMessage = String.format("Поле %s является обязательным для заполнения",userSurnameFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (isNullOrEmpty(user.getUserMail())){
            errorMessage = String.format("Поле %s является обязательным для заполнения",userMailFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (user.getUserName().length()<2 || user.getUserName().length()>50){
            errorMessage = String.format("Длина поля %s должна быть от 2 до 50 символов включительно",userNameFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (user.getUserSurname().length()<2 || user.getUserSurname().length()>50){
            errorMessage = String.format("Длина поля %s должна быть от 2 до 50 символов включительно",userSurnameFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (user.getUserMail().length()<5){
            errorMessage = String.format("Длина поля %s должна быть больше 4 символов",userMailFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (!isValidEmail(user.getUserMail())){
            errorMessage = String.format("Поле %s имеет некорректный формат",userMailFieldName);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (!isEmailUnique(user.getUserMail())){
            errorMessage = String.format("Значение e-mail: %s не является уникальным",user.getUserMail());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        //TODO баг №3 - поле isVip является не обязательным, но мы проверяем его как обязательное
        else if (user.getIsVip()==null){
            errorMessage = String.format("Значение isVip: %s является обязательным",user.getIsVip());
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        else if (user.getExpirationDate()!=null){
            try{
                LocalDate expirationDate = convertStringToDateWithSomePattern(user.getExpirationDate(),DATE_TIME_VALID_FORMAT, LocalDate.class);
                if (!expirationDate.isAfter(LocalDate.now())){
                    errorMessage = String.format("Значение expirationDate: %s должно быть больше текущей даты: %s",user.getExpirationDate(),LocalDate.now());
                    httpStatus = HttpStatus.BAD_REQUEST;
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
        if (bodyVerificationResult.getLeft().equals(HttpStatus.BAD_REQUEST) || bodyVerificationResult.getLeft().equals(HttpStatus.INTERNAL_SERVER_ERROR)){
            resultPair = bodyVerificationResult;
        }
        return resultPair;
    }


    public static String makeProcessingAndSuccessfulResponse(UserCreateRqBody userCreateRqBody){
        UUID uuid = UUID.randomUUID();
        //Билдим DAO для хранилища
        UserDAO userDAO = UserDAO.builder()
                .id(uuid.toString())
                .userMail(userCreateRqBody.getUserMail())
                .userName(userCreateRqBody.getUserName())
                .userSurname(userCreateRqBody.getUserSurname())
                .isVip(userCreateRqBody.getIsVip())
                .build();
        if (userCreateRqBody.getExpirationDate()!=null){
            userDAO.setExpirationDate(convertStringToDateWithSomePattern(userCreateRqBody.getExpirationDate(),DATE_TIME_VALID_FORMAT, LocalDate.class));
        }
        //Помещаем его в хранилище
        USERS_STORAGE.put(userCreateRqBody.getUserMail(),userDAO);
        //Билдим ответ
        UserCreateRsBody userCreateRsBody = UserCreateRsBody.builder()
                .id(uuid.toString())
                .userMail(userCreateRqBody.getUserMail())
                .userName(userCreateRqBody.getUserName())
                .userSurname(userCreateRqBody.getUserSurname())
                .isVip(userCreateRqBody.getIsVip())
                .expirationDate(userCreateRqBody.getExpirationDate())
                .build();
        return convertToJson(userCreateRsBody);
    }

    public static ResponseEntity<String> makeResponseUserCreateServiceV2BasedOnProcessingResult(String employeeIdHeaderValue, String employeeSystemHeaderValue, String requestBody){
        Pair<HttpStatus,String> validationResult = makeValidation(employeeIdHeaderValue,employeeSystemHeaderValue,requestBody);
        //Для 500 ошибки
        if (validationResult.getLeft().equals(HttpStatus.INTERNAL_SERVER_ERROR)){
            UserCreateRsErrorBody userCreateRsErrorBody = new UserCreateRsErrorBody(validationResult.getRight());
            String resultBody = convertToJson(userCreateRsErrorBody);
            return new ResponseEntity<>(
                    resultBody, buildDefaultHttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //Для 400 ошибки
        else if (validationResult.getLeft().equals(HttpStatus.BAD_REQUEST)){
            UserCreateRsErrorBody userCreateRsErrorBody = new UserCreateRsErrorBody(validationResult.getRight());
            String resultBody = convertToJson(userCreateRsErrorBody);
            return new ResponseEntity<>(
                    resultBody, buildDefaultHttpHeaders(), HttpStatus.BAD_REQUEST);
        }
         else {
            //Если прошли все валидации - конвертируем запрос
            UserCreateRqBody userCreateRqBody = convertJsonToObject(requestBody,UserCreateRqBody.class);
            //Билдим тело ответа (да, костыльно, знаю, можно было в одну строку, но мне так понятнее)
            String responseBody = makeProcessingAndSuccessfulResponse(userCreateRqBody);
            return new ResponseEntity<>(
                    responseBody, buildDefaultHttpHeaders(), HttpStatus.OK);
        }
    }
}

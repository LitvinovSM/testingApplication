package app.mock.controllers.userCreateService.service;



import app.mock.pojo.common.UserDAO;
import app.mock.pojo.v1.userCreate.rq.UserCreateRqBody;
import app.mock.pojo.v1.userCreate.rs.UserCreateRsBody;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static app.mock.storage.UsersStorage.USERS_STORAGE;
import static app.mock.storage.UsersStorage.isEmailUnique;
import static app.mock.utils.CommonUtil.*;
import static app.mock.utils.JsonUtil.convertJsonToObject;
import static app.mock.utils.JsonUtil.convertToJson;

public class UserCreateService {

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
                httpStatus = HttpStatus.OK;
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
            errorMessage = String.format("Не удалось конвертировать тело запроса в класс UserCreateRqBody. Полный текст ошибки:\n\r%s",getStackTraceAsString(e));
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
                .build();
        //Помещаем его в хранилище
        USERS_STORAGE.put(userCreateRqBody.getUserMail(),userDAO);
        //Билдим ответ
        UserCreateRsBody userCreateRsBody = UserCreateRsBody.builder()
                .id(uuid.toString())
                .userMail(userCreateRqBody.getUserMail())
                .userName(userCreateRqBody.getUserName())
                .userSurname(userCreateRqBody.getUserSurname())
                .build();
        return convertToJson(userCreateRsBody);
    }
}

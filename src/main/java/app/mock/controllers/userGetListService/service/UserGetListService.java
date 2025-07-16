package app.mock.controllers.userGetListService.service;


import app.mock.pojo.common.UserDAO;
import app.mock.pojo.v1.userListGet.rs.UserListGetRsBody;
import app.mock.pojo.v1.userListGet.rs.UserListItem;
import app.mock.pojo.v1.userListGet.rsError.UserListGetRsErrorBody;
import app.mock.pojo.v1.userUpdate.rsError.UserUpdateRsErrorBody;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static app.mock.storage.UsersStorage.getUserDaoListInStorage;
import static app.mock.utils.CommonUtil.buildDefaultHttpHeaders;
import static app.mock.utils.CommonUtil.isNullOrEmpty;
import static app.mock.utils.JsonUtil.convertJsonToObject;
import static app.mock.utils.JsonUtil.convertToJson;

public class UserGetListService {

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


    /**Общий метод проверки валидации*/
    public static Pair<HttpStatus,String> makeValidation(String employeeIdHeaderValue, String employeeSystemHeaderValue){
        String errorMessage = "";
        HttpStatus httpStatus = HttpStatus.OK;
        Pair<HttpStatus,String> resultPair = Pair.of(httpStatus,errorMessage);
        Pair<HttpStatus,String> headerVerificationResult = verifyRequiredHeaders(employeeIdHeaderValue,employeeSystemHeaderValue);
        if (headerVerificationResult.getLeft().equals(HttpStatus.BAD_REQUEST)){
            resultPair = headerVerificationResult;
        }
        return resultPair;
    }


    public static String makeProcessingAndSuccessfulResponse(){
        //Получаем текущий список пользователей в хранилище
        List<UserDAO> userDAOList = getUserDaoListInStorage();
        //Создаем будущий список для ответа
        List<UserListItem> userListItems = new ArrayList<>();
        for (UserDAO userDAO : userDAOList){
            UserListItem userListItem = UserListItem.builder()
                    .id(userDAO.getId())
                    .userName(userDAO.getUserName())
                    .userSurname(userDAO.getUserSurname())
                    .userMail(userDAO.getUserMail())
                    .build();
            userListItems.add(userListItem);
        }
        //Билдим полный ответ
        UserListGetRsBody userListGetRsBody = new UserListGetRsBody(userListItems);
        //Билдим ответ
        return convertToJson(userListGetRsBody);
    }

    public static ResponseEntity<String> makeResponseUserGetListServiceV1BasedOnProcessingResult(String employeeIdHeaderValue, String employeeSystemHeaderValue){
        Pair<HttpStatus,String> validationResult = makeValidation(employeeIdHeaderValue,employeeSystemHeaderValue);
        //Для 500 ошибки
        if (validationResult.getLeft().equals(HttpStatus.INTERNAL_SERVER_ERROR)){
            UserListGetRsErrorBody userListGetRsErrorBody = new UserListGetRsErrorBody(validationResult.getRight());
            String resultBody = convertToJson(userListGetRsErrorBody);
            return new ResponseEntity<>(
                    resultBody, buildDefaultHttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //Для 400 ошибки
        else if (validationResult.getLeft().equals(HttpStatus.BAD_REQUEST)){
            UserUpdateRsErrorBody updateRsErrorBody = new UserUpdateRsErrorBody(validationResult.getRight());
            String resultBody = convertToJson(updateRsErrorBody);
            return new ResponseEntity<>(
                    resultBody, buildDefaultHttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        else {
            //Билдим тело ответа (да, костыльно, знаю, можно было в одну строку, но мне так понятнее)
            String responseBody = makeProcessingAndSuccessfulResponse();
            return new ResponseEntity<>(
                    responseBody, buildDefaultHttpHeaders(), HttpStatus.OK);
        }
    }

}

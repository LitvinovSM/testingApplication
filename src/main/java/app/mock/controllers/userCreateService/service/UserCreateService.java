package app.mock.controllers.userCreateService.service;



import org.apache.commons.lang3.tuple.Pair;

import static app.mock.utils.CommonUtil.isNullOrEmpty;

public class UserCreateService {

    public static Pair<Integer,String> verifyRequiredHeaders(String employeeIdHeaderValue, String employeeSystemHeaderValue){
        String errorMessage = "";
        Integer resultStatusCode = 200;
        String employeeIdHeaderName = "employeeId";
        String employeeSystemHeaderName = "employeeSystem";
        if (isNullOrEmpty(employeeIdHeaderValue) && isNullOrEmpty(employeeSystemHeaderValue)){
            errorMessage = String.format("Не переданы обязательные заголовки: %s и %s",employeeIdHeaderName,employeeSystemHeaderName);
            resultStatusCode = 400;
        } else if (isNullOrEmpty(employeeIdHeaderValue)){
            errorMessage = String.format("Не передан обязательный заголовок: %s",employeeIdHeaderName);
            resultStatusCode = 400;
        } else if (isNullOrEmpty(employeeSystemHeaderValue)){
            errorMessage = String.format("Не передан обязательный заголовок: %s",employeeSystemHeaderName);
            resultStatusCode = 400;
        } else if (employeeSystemHeaderValue.length()!=4){
            errorMessage = String.format("Длина значения заголовка: %s должна быть равна 4 символам",employeeSystemHeaderName);
            resultStatusCode = 400;
        } else if (employeeIdHeaderValue.isEmpty() || employeeIdHeaderValue.length()>100){
            errorMessage = String.format("Длина значения заголовка: %s должна быть равна в интервале [1;100]",employeeIdHeaderName);
            resultStatusCode = 400;
        } else {
            try {
                Integer.parseInt(employeeIdHeaderValue);
            } catch (Exception e) {
                errorMessage = String.format("В заголовок: %s переданы не целые числовые значения",employeeIdHeaderName);
                resultStatusCode = 200;
            }

        }
        return Pair.of(resultStatusCode,errorMessage);
    }


}

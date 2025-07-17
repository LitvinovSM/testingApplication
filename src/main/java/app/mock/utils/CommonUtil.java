package app.mock.utils;

import org.springframework.http.HttpHeaders;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static app.mock.utils.CommonConstants.CONTENT_TYPE_HEADER_NAME;
import static app.mock.utils.CommonConstants.CONTENT_TYPE_HEADER_VALUE;

public class CommonUtil {

    /**Метод проверки пустой строки*/
    public static boolean isNullOrEmpty(String value){
        return value==null || value.isEmpty();
    }

    /**Билд дефолтных заголовков ответа*/
    public static HttpHeaders buildDefaultHttpHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE);
        return headers;
    }

    /**Метод проверки строки на валидность e-mail*/
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Разделяем email на части по символу @
        String[] parts = email.split("@");
        // Должно быть ровно две части: до @ и после
        if (parts.length != 2) {
            return false;
        }
        String localPart = parts[0];  // часть до @
        String domainPart = parts[1]; // часть после @
        // Проверяем, что до @ есть хотя бы один символ
        if (localPart.length() < 1) {
            return false;
        }
        // Разделяем доменную часть по последней точке
        String[] domainParts = domainPart.split("\\.");
        // Должно быть хотя бы две части (домен и TLD)
        if (domainParts.length < 2) {
            return false;
        }
        // Проверяем, что после последней точки есть хотя бы один символ
        if (domainParts[domainParts.length - 1].length() < 1) {
            return false;
        }
        return true;
    }

    public static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);  // Записываем стек трейс в PrintWriter
        return sw.toString();   // Возвращаем в виде строки
    }


    public static <T> T convertStringToDateWithSomePattern(String inputDate, String inputDatePattern,Class<T> targetClass){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(inputDatePattern);
        T value = null;
        if (targetClass.equals(LocalDate.class)){
            value = (T) LocalDate.parse(inputDate,formatter);

        } else if (targetClass.equals(LocalDateTime.class)){
            value = (T) LocalDateTime.parse(inputDate,formatter);
        } else {
            throw new RuntimeException(String.format("Не удалось конвертировать строку: %s в формат: %s",inputDate,inputDatePattern));
        }
        return value;
    }
}

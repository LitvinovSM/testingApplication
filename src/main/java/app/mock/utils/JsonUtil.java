package app.mock.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonUtil {

    /**
     * Метод конвертации объекта в Json красивого вида
     * */
    public static <T> String convertToJson(T object){
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json;
        try {
            json = objectWriter.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.out.println(String.format("Ошибка парсинга объекта в json. Объект:\n\r%s",object));
            json=String.format("Ошибка парсинга объекта в json. Объект:\n\r%s",object);
        }
        return json;
    }



    /**
     * Безопасная конвертация JSON-строки в объект
     *
     * @param json JSON-строка
     * @param tClass класс целевого объекта
     * @return экземпляр целевого класса или null при ошибке
     */
    public static <T> T convertJsonToObject(String json, Class<T> tClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,true);
        try {
            return objectMapper.readValue(json, tClass);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге JSON: " + e.getMessage());
        }
    }
}

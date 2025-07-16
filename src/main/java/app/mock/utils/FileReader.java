package app.mock.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileReader {

    public static String readResourceFileAsString(String fileName) throws IOException {
        // Получаем ClassLoader для загрузки ресурсов
        ClassLoader classLoader = FileReader.class.getClassLoader();

        // Открываем поток для чтения файла из ресурсов
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Файл не найден: " + fileName);
            }

            // Читаем содержимое файла и преобразуем в строку
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}

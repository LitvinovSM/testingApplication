package app.mock.storage;

import app.mock.pojo.common.UserDAO;

import java.util.HashMap;

/**Класс хранилище для существующих пользователей*/
public class UsersStorage {

    public static final HashMap<String, UserDAO> USERS_STORAGE = new HashMap<>();

    /**
     * Проверяет, содержит ли HashMap указанный строковый ключ.
     *
     * @param key  Ключ, который нужно найти
     * @return true, если ключ найден, иначе false
     */
    public static boolean isEmailUnique(String key) {
        if (USERS_STORAGE.isEmpty() || key == null) {
            return true;
        }
        return !USERS_STORAGE.containsKey(key);
    }

}

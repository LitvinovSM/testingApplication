package app.mock.storage;

import app.mock.pojo.common.UserDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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


    /**
     * Проверяет, содержит ли HashMap указанный e-mail для пользователей с другим идентификатором.
     *
     * @return true, если ключ найден, иначе false
     */
    public static boolean isEmailUniqueComparingToOtherUsers(String email,String uuid) {
        for (String key : USERS_STORAGE.keySet()) {
            if (USERS_STORAGE.get(key).getUserMail().equalsIgnoreCase(email) && !USERS_STORAGE.get(key).getId().equalsIgnoreCase(uuid)){
                return false;
            }
        }
        return true;
    }


    /**Получение пользователя из хранилища по уникальному UUID
     * Если находим - возвращаем пользователя, если нет - null*/
    public static UserDAO getUserByUuid(String uuid){
        for (String key : USERS_STORAGE.keySet()) {
            if (USERS_STORAGE.get(key).getId().equalsIgnoreCase(uuid)){
                return USERS_STORAGE.get(key);
            }
        }
        return null;
    }


    /**
     * Проверяет, содержит ли HashMap указанный e-mail для пользователей с другим идентификатором.
     *
     * @return true, если ключ найден, иначе false
     */
    public static void removeFromStorageById(String uuid) {
        for (String key : USERS_STORAGE.keySet()) {
            if (USERS_STORAGE.get(key).getId().equalsIgnoreCase(uuid)){
                USERS_STORAGE.remove(key);
            }
        }
    }


    public static List<UserDAO> getUserDaoListInStorage(){
        List<UserDAO> userDAOList = new ArrayList<>();
        for (String key : USERS_STORAGE.keySet()) {
            UserDAO userDAO = USERS_STORAGE.get(key);
            userDAOList.add(userDAO);
        }
        return userDAOList;
    }
}

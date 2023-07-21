package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeptions.EmailDuplicateException;
import ru.practicum.shareit.exeptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> hashMap = new HashMap<>();

    private long idGenerator = 1;


    @Override
    public User create(User user) {
        checkEmailDuplicate(user.getEmail());
        user.setId(idGenerator);
        hashMap.put(idGenerator++, user);
        log.debug("Юзер с id {} успешно создан", user.getId());
        return user;
    }

    @Override
    public User update(User user, Long id) {
        if(!hashMap.containsKey(id)){
            throw new UserNotFoundException("Юзера с таким id {} нет");
        }
        User oldUser = hashMap.get(id);
        if(user.getName() != null){
            oldUser.setName(user.getName());
        }
        if(user.getEmail() != null && !user.getEmail().equals(oldUser.getEmail())){
            //if(user.getEmail().equals(oldUser.getEmail())){
                //throw new EmailDuplicateException("Нельзя обновить email на идентичный");
            //}
            checkEmailDuplicate(user.getEmail());
            oldUser.setEmail(user.getEmail());
        }
        return oldUser;
    }

    @Override
    public void delete(Long id) {
        if (hashMap.containsKey(id)) {
            hashMap.remove(id);
            log.debug("Юзер с id {} успешно удален", id);
        } else {
            throw new UserNotFoundException("Юзера с таким id не существует");
        }
    }

    @Override
    public User get(Long id) {
        if (hashMap.containsKey(id)){
            log.debug("Юзер с id {} был успешно получен", id);
            return hashMap.get(id);
        } else {
            throw new UserNotFoundException("Юзера с таким id не существует");
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(hashMap.values());
    }

    public void checkEmailDuplicate(String email) {
        for (User user : hashMap.values()) {
            if(email.equals(user.getEmail())){
                throw new EmailDuplicateException("Такой email уже есть");
            }
        }
    }

    public void userExist(Long userId){
        if(!hashMap.containsKey(userId)){
            throw new UserNotFoundException("Юзера с id " + userId + " не существует");
        }
    }
}

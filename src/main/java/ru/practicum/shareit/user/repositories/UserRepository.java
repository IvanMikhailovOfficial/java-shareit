package ru.practicum.shareit.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByIdIs(Long id);

    void deleteUserByIdIs(Long id);

    boolean existsUserByEmailIs(String email);
}
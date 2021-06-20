package com.example.abu.abu_zver.repository;

import com.example.abu.abu_zver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getByChatId(long chatId);
}

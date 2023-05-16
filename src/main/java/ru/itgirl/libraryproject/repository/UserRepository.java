package ru.itgirl.libraryproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itgirl.libraryproject.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername (String username);
}

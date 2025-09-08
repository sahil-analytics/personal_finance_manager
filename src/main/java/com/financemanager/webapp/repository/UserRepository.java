package com.financemanager.webapp.repository;

import com.financemanager.webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Optional annotation, JpaRepository implies it
public interface UserRepository extends JpaRepository<User, Long> { // Entity: User, Primary Key Type: Long

    /**
     * Finds a user by their email address.
     * Spring Data JPA automatically implements this based on the method name.
     *
     * @param email The email address to search for.
     * @return An Optional containing the User if found, otherwise empty.
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     * More efficient than findByEmail().isPresent() if you only need existence check.
     *
     * @param email The email address to check.
     * @return true if a user with this email exists, false otherwise.
     */
    boolean existsByEmail(String email);

}
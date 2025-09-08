package com.financemanager.webapp.repository;

import com.financemanager.webapp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> { // Entity: Category, Primary Key Type: Long

    /**
     * Finds all categories belonging to a specific user, ordered by name.
     * Spring Data JPA derives the query from the method name: findBy + User (associated entity) + Id (field in User) + OrderBy + Name + Asc (default).
     *
     * @param userId The ID of the user whose categories are to be retrieved.
     * @return A list of categories belonging to the user, ordered by name.
     */
    List<Category> findByUserIdOrderByNameAsc(Long userId);

    /**
     * Finds a specific category by its ID and the ID of its owner user.
     * Useful for ensuring a user is accessing/modifying their own category.
     *
     * @param id     The ID of the category.
     * @param userId The ID of the user who owns the category.
     * @return An Optional containing the Category if found and owned by the user, otherwise empty.
     */
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    /**
     * Checks if a category with the given name already exists for a specific user.
     * Useful for preventing duplicate category names per user.
     *
     * @param name   The name of the category to check.
     * @param userId The ID of the user.
     * @return true if a category with this name exists for the user, false otherwise.
     */
    boolean existsByNameAndUserId(String name, Long userId);

}
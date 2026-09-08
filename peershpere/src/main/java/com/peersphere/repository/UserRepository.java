package com.peersphere.repository;

import com.peersphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;


/**
 * @Repository — marks this as a data-access component.
 * JpaRepository<User, Long> gives us 20+ free methods instantly:
 *   - save(user)
 *   - findById(id)
 *   - findAll()
 *   - deleteById(id)
 *   - count()
 *   ... and more
 *
 * We don't write any SQL. Spring Data JPA generates it from the
 * method name. "findByEmail" → SELECT * FROM users WHERE email = ?
 *
 * Optional<User> means the result might be null (user not found),
 * which forces us to handle that case explicitly. Much safer than
 * returning null and getting NullPointerExceptions.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.department) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByNameOrDepartment(@Param("keyword") String keyword);

    /**
     * Find users who share the same department — useful for peer recommendations.
     * excludeId — we don't want to recommend the user to themselves.
     */
    @Query("SELECT u FROM User u WHERE u.department = :department AND u.id != :excludeId")
    List<User> findByDepartmentAndIdNot(
            @Param("department") String department,
            @Param("excludeId") Long excludeId
    );

    /**
     * Load all users except the current one.
     * Used by the recommendation engine to find potential peers.
     */
    @Query("SELECT u FROM User u WHERE u.id != :excludeId")
    List<User> findAllExcept(@Param("excludeId") Long excludeId);
}
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findByEmail(String username);

    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN u.roles r
        WHERE
            LOWER(COALESCE(u.firstName, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.lastName, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.tel, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.status, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.verifyEmail, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(CAST(r as string)) LIKE LOWER(CONCAT('%', :term, '%'))
            OR (:term = 'active' AND u.active = true)
            OR (:term = 'inactive' AND u.active = false)
        """)
    Page<User> searchUsers(@Param("term") String term, Pageable pageable);

    @Query("""
    SELECT DISTINCT u
    FROM User u
    WHERE
        (
            LOWER(COALESCE(u.firstName, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.lastName, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.tel, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.status, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            OR LOWER(COALESCE(u.verifyEmail, '')) LIKE LOWER(CONCAT('%', :term, '%'))
        )
        AND (
            (:activeFilter = 'ALL')
            OR (:activeFilter = 'ACTIVE' AND u.active = true)
            OR (:activeFilter = 'INACTIVE' AND u.active = false)
        )
""")
    Page<User> searchUsers(
            @Param("term") String term,
            @Param("activeFilter") String activeFilter,
            Pageable pageable
    );

    @Query("""
SELECT COUNT(u) FROM User u
WHERE (
    LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
    OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
    OR LOWER(u.tel) LIKE LOWER(CONCAT('%', :search, '%'))
)
AND (
    :filter = 'ALL'
    OR (:filter = 'ACTIVE' AND u.active = true)
    OR (:filter = 'INACTIVE' AND u.active = false)
)
""")
    long countUsers(
            @Param("search") String search,
            @Param("filter") String filter
    );

    /*    @Query("SELECT u FROM User u LEFT JOIN FETCH u.department WHERE u.email = :email")
    User findByEmailWithDepartments(@Param("email") String email);*/
}

package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

//    Optional<User> findByEmailAndBuyerIsNotEmpty(String email);

    @Query("select u from User u where u.email = ?1 and u.role ='BUYER'")
    Optional<User> findByEmailAndBuyer(String email);
}

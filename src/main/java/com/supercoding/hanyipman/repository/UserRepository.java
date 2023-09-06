package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Option;
import com.supercoding.hanyipman.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

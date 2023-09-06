package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long> {

    Buyer findByUser(User user);

    @Query("SELECT b from Buyer b JOIN fetch b.user u where u.id =:userId")
    Optional<Buyer> findBuyerByUserId(@Param("userId") Long userId);
    Boolean existsByUser(User user);

}

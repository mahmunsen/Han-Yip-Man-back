package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface SellerRepositoryTest extends JpaRepository<Seller, Long> {

    @Query("select (count(s) > 0) " +
            "from Seller s " +
            "join User u  " +
            "where  u.email = :email")
    Boolean existSellerEmail(String email);


}




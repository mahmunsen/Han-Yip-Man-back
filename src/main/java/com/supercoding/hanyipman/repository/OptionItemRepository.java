package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.OptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface OptionItemRepository extends JpaRepository<OptionItem, Long> {

    @Query("SELECT oi FROM OptionItem oi where oi.id in :options")
    List<OptionItem> findByOptionItemIds(@Param("options") List<Long> options);


}

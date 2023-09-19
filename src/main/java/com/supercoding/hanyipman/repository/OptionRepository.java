package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {

    Optional<Option> findByIdAndIsDeletedFalse(Long optionId);

}

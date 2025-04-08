package com.project.hstime.repository;

import com.project.hstime.domain.Hotel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface HotelRepository extends CrudRepository<Hotel, Long> {
    Set<Hotel> findAll();
    Optional<Hotel> findByIdHotel(long idHotel);
}

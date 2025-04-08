package com.project.hstime.service;

import com.project.hstime.domain.Hotel;

import java.util.Optional;
import java.util.Set;

public interface HotelService {
    Set<Hotel> findAll();
    Optional<Hotel> findByIdHotel(long idHotel);
}

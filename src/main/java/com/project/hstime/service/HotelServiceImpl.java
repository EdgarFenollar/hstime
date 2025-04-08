package com.project.hstime.service;

import com.project.hstime.domain.Hotel;
import com.project.hstime.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class HotelServiceImpl implements HotelService{
    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public Set<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    @Override
    public Optional<Hotel> findByIdHotel(long idHotel) {
        return hotelRepository.findByIdHotel(idHotel);
    }
}

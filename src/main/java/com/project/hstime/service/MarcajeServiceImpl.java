package com.project.hstime.service;

import com.project.hstime.domain.Marcaje;
import com.project.hstime.exception.MarcajeNotFoundException;
import com.project.hstime.repository.MarcajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MarcajeServiceImpl implements MarcajeService{

    @Autowired
    private MarcajeRepository marcajeRepository;

    @Override
    public Set<Marcaje> findAll() {
        return marcajeRepository.findAll();
    }

    @Override
    public Optional<Marcaje> findByIdMarcaje(long idMarcaje) {
        return marcajeRepository.findByIdMarcaje(idMarcaje);
    }

    @Override
    public Set<Marcaje> findByIdHotelAndIdTrabajador(int idHotel, int idTrabajador) {
        return marcajeRepository.findByIdHotelAndIdTrabajador(idHotel, idTrabajador);
    }

    @Override
    public Set<Marcaje> findByFechaHora(Date fechaHora) {
        return marcajeRepository.findByFechaHora(fechaHora);
    }

    @Override
    public Marcaje addMarcaje (Marcaje marcaje) {
        return marcajeRepository.save(marcaje);
    }

    @Override
    public Marcaje modifyMarcaje(long idMarcaje, Marcaje newMarcaje) {
        Marcaje marcaje = marcajeRepository.findByIdMarcaje(idMarcaje)
                .orElseThrow(() -> new MarcajeNotFoundException(idMarcaje));

        marcaje.setIdHotel(newMarcaje.getIdHotel());
        marcaje.setIdTrabajador(newMarcaje.getIdTrabajador());
        marcaje.setLocalizacion(newMarcaje.getLocalizacion());
        marcaje.setFechaHora(newMarcaje.getFechaHora());
        marcaje.setAccion(newMarcaje.getAccion());

        return marcajeRepository.save(marcaje);
    }

    @Override
    public Marcaje descargarMarcaje(long idMarcaje, Marcaje newMarcaje) {
        Marcaje marcaje = marcajeRepository.findByIdMarcaje(idMarcaje)
                .orElseThrow(() -> new MarcajeNotFoundException(idMarcaje));

        marcaje.setDescargado(newMarcaje.getDescargado());

        return marcajeRepository.save(marcaje);
    }

    @Override
    public void deleteMarcaje(long idMarcaje) {
        marcajeRepository.findByIdMarcaje(idMarcaje)
                .orElseThrow(() -> new MarcajeNotFoundException(idMarcaje));
        marcajeRepository.deleteById(idMarcaje);
    }
}
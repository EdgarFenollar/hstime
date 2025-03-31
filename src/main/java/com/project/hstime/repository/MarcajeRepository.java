package com.project.hstime.repository;

import com.project.hstime.domain.Marcaje;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MarcajeRepository extends CrudRepository<Marcaje, Long> {

    Set<Marcaje> findAll();
    Optional<Marcaje> findByIdMarcaje(long idMarcaje);
    Set<Marcaje> findByIdHotelAndIdTrabajador(int idHotel, int idTrabajador);
    Set<Marcaje> findByIdHotelAndIdTrabajadorAndFechaHora(int idHotel, int idTrabajador, Date fecha);
    Set<Marcaje> findByFechaHora(Date fechaHora);
    Set<Marcaje> findByFechaHoraBetween(Date fechaInicio, Date fechaFin);
    Set<Marcaje> findByIdHotelAndIdTrabajadorAndFechaHoraBetween(int idHotel, int idTrabajador, Date startDate, Date endDate);
}


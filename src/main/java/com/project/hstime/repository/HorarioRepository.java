package com.project.hstime.repository;

import com.project.hstime.domain.Horario;
import com.project.hstime.domain.Marcaje;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Repository
public interface HorarioRepository extends CrudRepository<Horario, Long> {
    Set<Horario> findAll();
    Optional<Horario> findByIdHorario(long idHorario);
    Set<Horario> findByIdHotelAndIdTrabajador(int idHotel, int idTrabajador);
    Set<Horario> findByIdHotelAndIdTrabajadorAndFechaBetween(int idHotel, int idTrabajador, Date fechaInicio, Date fechaFin);
    Set<Horario> findByIdHotelAndIdTrabajadorAndFecha(int idHotel, int idTrabajador, Date fecha);
}
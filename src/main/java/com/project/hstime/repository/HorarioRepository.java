package com.project.hstime.repository;

import com.project.hstime.domain.Horario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface HorarioRepository extends CrudRepository<Horario, Long> {
    Set<Horario> findAll();
    Optional<Horario> findByIdHorario(long idHorario);
    Set<Horario> findByIdHotelAndIdTrabajador(int idHotel, int idTrabajador);
}
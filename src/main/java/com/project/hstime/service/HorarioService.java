package com.project.hstime.service;

import com.project.hstime.domain.Horario;
import java.util.Optional;
import java.util.Set;

public interface HorarioService {
    //Metodos de busqueda
    Set<Horario> findAll();
    Optional<Horario> findByIdHorario(long idMarcaje);
    Set<Horario> findByIdHotelAndIdTrabajador(int idHotel, int idTrabajador);
    //Metodo de añadir
    Horario addHorario(Horario horario);
    //Metodo modificar
    Horario modifyHorario(long idHorario, Horario newHorario);
    //Metodo eliminar
    void deleteHorario(long idHorario);
}

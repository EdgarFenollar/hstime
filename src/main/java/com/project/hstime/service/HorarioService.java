package com.project.hstime.service;

import com.project.hstime.domain.Horario;
import com.project.hstime.domain.Marcaje;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface HorarioService {
    //Metodos de busqueda
    Set<Horario> findAll();
    Optional<Horario> findByIdHorario(long idHorario);
    Set<Horario> findByIdHotelAndIdTrabajador(int idHotel, int idTrabajador);
    Set<Horario> findByIdHotelAndIdTrabajadorAndFechaHoraBetween(int idHotel, int idTrabajador, Date fechaInicio, Date fechaFin);
    Set<Horario> findByRangoFechas(int idHotel, int idTrabajador, Date fechaInicio, Date fechaFin);
    //Metodo de añadir
    Horario addHorario(Horario horario);
    //Metodo modificar
    Horario modifyHorario(long idHorario, Horario newHorario);
    //Metodo eliminar
    void deleteHorario(long idHorario);
}

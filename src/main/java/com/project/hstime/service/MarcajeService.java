package com.project.hstime.service;

import com.project.hstime.domain.Marcaje;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MarcajeService {
    //Metodos de busqueda
    Set<Marcaje> findAll();
    Optional<Marcaje> findByIdMarcaje(long idMarcaje);
    Set<Marcaje> findByIdHotelAndIdTrabajador(int idHotel, int idTrabajador);
    Set<Marcaje> findByFechaHora(Date fechaHora);
    Set<Marcaje> findByRangoFechas(Date fechaInicio, Date fechaFin);
    //Metodo de añadir
    Marcaje addMarcaje(Marcaje marcaje);
    //Metodo modificar
    Marcaje modifyMarcaje(long idMarcaje, Marcaje newMarcaje);
    //Metodo descargar
    Marcaje descargarMarcaje(long idMarcaje);
    //Metodo eliminar
    void deleteMarcaje(long idMarcaje);
}

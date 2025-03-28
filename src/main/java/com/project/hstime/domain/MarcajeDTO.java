package com.project.hstime.domain;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.Date;

public class MarcajeDTO {
    private int idHotel;
    private int idTrabajador;
    private double latitud;
    private double longitud;
    private char accion;

    // Getters y Setters
    public int getIdHotel() { return idHotel; }
    public void setIdHotel(int idHotel) { this.idHotel = idHotel; }

    public int getIdTrabajador() { return idTrabajador; }
    public void setIdTrabajador(int idTrabajador) { this.idTrabajador = idTrabajador; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public char getAccion() { return accion; }
    public void setAccion(char accion) { this.accion = accion; }

    // Método para convertir DTO a Entity
    public Marcaje toEntity() {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(longitud, latitud)); // Importante: (longitud, latitud)

        return new Marcaje(idHotel, idTrabajador, new Date(), point, accion);
    }

    // Método estático para convertir Entity a DTO
    public static MarcajeDTO fromEntity(Marcaje marcaje) {
        MarcajeDTO dto = new MarcajeDTO();
        dto.setIdHotel(marcaje.getIdHotel());
        dto.setIdTrabajador(marcaje.getIdTrabajador());

        if (marcaje.getLocalizacion() != null) {
            dto.setLatitud(marcaje.getLocalizacion().getY()); // Y es latitud
            dto.setLongitud(marcaje.getLocalizacion().getX()); // X es longitud
        }

        dto.setAccion(marcaje.getAccion());
        return dto;
    }
}

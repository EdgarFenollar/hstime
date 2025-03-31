package com.project.hstime.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.project.hstime.domain.deserializer.CustomDateDeserializer;

import java.util.Date;

public class MarcajeUpdateDTO {
    private Long idHotel;
    private Long idTrabajador;
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date fechaHora;
    private Double latitud;
    private Double longitud;
    private Character accion;
    private String observaciones;

    public Long getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(Long idHotel) {
        this.idHotel = idHotel;
    }

    public Long getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(Long idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Character getAccion() {
        return accion;
    }

    public void setAccion(Character accion) {
        this.accion = accion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
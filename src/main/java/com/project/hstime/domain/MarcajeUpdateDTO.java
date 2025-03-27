package com.project.hstime.domain;

import java.util.Date;

public class MarcajeUpdateDTO {
    private Long idHotel;
    private Long idTrabajador;
    private Date fechaHora;
    private Double latitud;
    private Double longitud;
    private Character accion;
    private Character descargado;

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

    public Character getDescargado() {
        return descargado;
    }

    public void setDescargado(Character descargado) {
        this.descargado = descargado;
    }
}
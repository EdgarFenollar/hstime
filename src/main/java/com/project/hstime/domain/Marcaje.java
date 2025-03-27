package com.project.hstime.domain;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.util.Date;

@Entity
@Table(name = "marcajes")
public class Marcaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMarcaje;

    @Column
    private int idHotel;

    @Column
    private int idTrabajador;

    @Column
    private Date fechaHora;

    @Column(columnDefinition = "POINT")
    private Point localizacion;

    @Column(columnDefinition = "CHAR(1) DEFAULT 'N'")
    private char descargado;

    @Column
    private char accion;

    public Marcaje() {
        this.descargado = 'N';
    }

    public Marcaje(int idHotel, int idTrabajador, Date fechaHora, Point localizacion, char descargado, char accion) {
        this.idHotel = idHotel;
        this.idTrabajador = idTrabajador;
        this.fechaHora = fechaHora;
        this.localizacion = localizacion;
        this.descargado = 'N';
        this.accion = accion;
    }

    public Long getIdMarcaje() {
        return idMarcaje;
    }

    public void setIdMarcaje(Long idMarcaje) {
        this.idMarcaje = idMarcaje;
    }

    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Point getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Point localizacion) {
        this.localizacion = localizacion;
    }

    public char getDescargado() {
        return descargado;
    }

    public void setDescargado(char descargado) {
        this.descargado = descargado;
    }

    public char getAccion() {
        return accion;
    }

    public void setAccion(char accion) {
        this.accion = accion;
    }
}
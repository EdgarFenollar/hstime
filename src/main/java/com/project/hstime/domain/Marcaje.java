package com.project.hstime.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.hstime.domain.deserializer.PointSerializer;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.util.Date;

@Entity
@Table(name = "marcajes")
public class Marcaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMarcaje;

    @Column(nullable = false)
    private int idHotel;

    @Column(nullable = false)
    private int idTrabajador;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaHora;

    @Column(columnDefinition = "POINT", nullable = true)
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @JsonSerialize(using = PointSerializer.class)
    private Point localizacion;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private Character descargado;

    @Column(nullable = false)
    private Character accion;

    @Column
    private String observaciones;

    public Marcaje() {
        this.descargado = 'N';
    }

    public Marcaje(int idHotel, int idTrabajador, Date fechaHora, Point localizacion, Character accion, String observaciones) {
        this.idHotel = idHotel;
        this.idTrabajador = idTrabajador;
        this.fechaHora = fechaHora;
        this.localizacion = localizacion;
        this.descargado = 'N';
        this.accion = accion;
        this.observaciones = observaciones;
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

    public Character getDescargado() {
        return descargado;
    }

    public void setDescargado(Character descargado) {
        this.descargado = descargado;
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

package com.project.hstime.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.hstime.domain.deserializer.PointSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.util.Date;

@Entity
@Table(name = "horario")
public class    Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorario;

    @Column(nullable = false)
    private int idHotel;

    @Column(nullable = false)
    private int idTrabajador;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fecha;

    @Column
    @Size(max = 50)
    private String departamento;

    @Column(nullable = true)
    @Size(max = 50)
    private String concepto;

    @Column(nullable = true)
    @Size(max = 100)
    private String horario;

    public Horario() {
        this.concepto = null;
        this.horario = null;
    }

    public Horario(Long idHorario, int idHotel, int idTrabajador, Date fecha, String departamento) {
        this.idHorario = idHorario;
        this.idHotel = idHotel;
        this.idTrabajador = idTrabajador;
        this.fecha = fecha;
        this.departamento = departamento;

    }

    public Long getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Long idHorario) {
        this.idHorario = idHorario;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }
}

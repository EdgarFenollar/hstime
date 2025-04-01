package com.project.hstime.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.project.hstime.domain.deserializer.CustomDateDeserializer;

import java.util.Date;

public class HorarioUpdateDTO {
    private Long idHotel;
    private Long idTrabajador;
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date fecha;
    private String departamento;
    private String concepto;
    private String horario;

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
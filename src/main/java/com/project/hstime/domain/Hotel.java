package com.project.hstime.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.hstime.domain.deserializer.PointSerializer;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.util.Date;

@Entity
@Table(name = "hoteles")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHotel;

    @Column
    private String hotel;

    @Column
    private String poblacion;

    @Column(columnDefinition = "POINT", nullable = true)
    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @JsonSerialize(using = PointSerializer.class)
    private Point localizacion;

    public Long getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(Long idHotel) {
        this.idHotel = idHotel;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public Point getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Point localizacion) {
        this.localizacion = localizacion;
    }
}

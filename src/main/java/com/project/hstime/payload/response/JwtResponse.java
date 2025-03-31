package com.project.hstime.payload.response;

import java.util.List;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String correo;
  private String nombre;
  private int idHotel;
  private int idTrabajador;
  private String DNI;
  private List<String> roles;

  public JwtResponse(String accessToken, Long id, String correo, String nombre, int idHotel, int idTrabajador, String DNI, List<String> roles) {
    this.token = accessToken;
    this.id = id;
    this.correo = correo;
    this.nombre = nombre;
    this.idHotel = idHotel;
    this.idTrabajador = idTrabajador;
    this.DNI = DNI;
    this.roles = roles;
  }

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokenType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCorreo() {
    return correo;
  }

  public void setCorreo(String email) {
    this.correo = email;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
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

  public String getDNI() {
    return DNI;
  }

  public void setDNI(String dni) {
    this.DNI = dni;
  }

  public List<String> getRoles() {
    return roles;
  }
}

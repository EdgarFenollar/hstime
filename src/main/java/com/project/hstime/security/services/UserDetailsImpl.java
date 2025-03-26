package com.project.hstime.security.services;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.hstime.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private Long id;

  private String username;

  private String correo;

  private int idHotel;

  private String DNI;

  @JsonIgnore
  private String contrasenya;

  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(Long id, String correo, String contrasenya, int idHotel, String DNI,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.correo = correo;
    this.contrasenya = contrasenya;
    this.idHotel = idHotel;
    this.DNI = DNI;
    this.authorities = authorities;
  }

  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName().name()))
        .collect(Collectors.toList());

    return new UserDetailsImpl(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        user.getIdHotel(),
        user.getDNI(),
        authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public Long getId() {
    return id;
  }

  public String getCorreo() {
    return correo;
  }

  public int getIdHotel() {
    return idHotel;
  }

  public String getDNI() {
    return DNI;
  }

  @Override
  public String getPassword() {
    return contrasenya;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}

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
  private String email;
  private int idHotel;
  private int idTrabajador;
  private String DNI;

  @JsonIgnore
  private String password;

  private Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(Long id, String email, String password, int idHotel, int idTrabajador, String DNI,
                         Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.idHotel = idHotel;
    this.idTrabajador = idTrabajador;
    this.DNI = DNI;
    this.authorities = authorities;
  }

  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .collect(Collectors.toList());

    return new UserDetailsImpl(
            user.getId(),
            user.getEmail(),    // Asegúrate que User.getEmail() existe
            user.getPassword(), // Asegúrate que User.getPassword() existe
            user.getIdHotel(),
            user.getIdTrabajador(),
            user.getDNI(),
            authorities);
  }

  // Métodos de UserDetails
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  public Long getId() {
    return id;
  }

  public int getIdHotel() {
    return idHotel;
  }

  public int getIdTrabajador() {
    return idTrabajador;
  }

  public String getDNI() {
    return DNI;
  }

  // Métodos de estado de cuenta (siempre true si no necesitas esta funcionalidad)
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
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
package com.project.hstime.repository;


import com.project.hstime.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  List<User> findByEmailContainingIgnoreCase(String email); // Nuevo método


  Boolean existsByEmail(String email);

  boolean existsByIdHotelAndIdTrabajador(int idHotel, int idTrabajador);

  Boolean existsByDNI(String dni);

  //Para que usuario pueda editar sus atributos
  Optional<User> findById(Long id);

  Optional<User> findByIdHotelAndIdTrabajador(int idHotel, int idTrabajador);

  @Transactional
  void deleteUserById(Long id);
}

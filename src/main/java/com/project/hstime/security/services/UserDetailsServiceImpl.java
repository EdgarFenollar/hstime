package com.project.hstime.security.services;

import com.project.hstime.models.User;
import com.project.hstime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  // Constructor injection es mejor práctica que @Autowired en campo
  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
    // Validación robusta del email
    if (!StringUtils.hasText(email)) {
      throw new UsernameNotFoundException("El email no puede estar vacío");
    }

    // Validación de formato básico de email (opcional)
    if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
      throw new UsernameNotFoundException("Formato de email inválido");
    }

    // Buscar usuario por email
    User user = userRepository.findByEmail(email.toLowerCase().trim())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

    // Construir UserDetails
    return UserDetailsImpl.build(user);
  }
}
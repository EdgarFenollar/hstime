package com.project.hstime.controller;

import com.project.hstime.domain.Hotel;
import com.project.hstime.domain.Marcaje;
import com.project.hstime.payload.response.MessageResponse;
import com.project.hstime.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("hstime/hotel")
@Tag(name = "Hoteles", description = "API para gestión de hoteles")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Operation(summary = "Obtener todos los hoteles", description = """
          Devuelve un conjunto de todos los hoteles registrados.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hoteles obtenidos exitosamente.",
                    content = @Content(schema = @Schema(implementation = Hotel.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getAllHoteles() {
        try {
            Set<Hotel> hoteles = hotelService.findAll();
            return ResponseEntity.ok(hoteles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener hoteles: " + e.getMessage()));
        }
    }

    @Operation(summary = "Obtener hotel por id", description = """
          Devuelve los hoteles por una id determinada.
          **Permisos requeridos**: Solamente Administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel obtenido exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("search/{idHotel}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getHotelById(
            @PathVariable int idHotel) {
        try {
            Optional<Hotel> hoteles = hotelService.findByIdHotel(idHotel);
            return ResponseEntity.ok(hoteles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener hotel: " + e.getMessage()));
        }
    }
}

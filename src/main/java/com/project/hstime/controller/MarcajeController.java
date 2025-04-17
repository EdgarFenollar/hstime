package com.project.hstime.controller;

import com.project.hstime.domain.Marcaje;
import com.project.hstime.domain.MarcajeDTO;
import com.project.hstime.domain.MarcajeUpdateDTO;
import com.project.hstime.exception.MarcajeNotFoundException;
import com.project.hstime.payload.response.ErrorResponseUpdate;
import com.project.hstime.payload.response.MessageResponse;
import com.project.hstime.service.MarcajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("hstime/marcaje")
@Tag(name = "Marcajes", description = "API para gestión de marcajes de trabajadores")
public class MarcajeController {

    @Autowired
    private MarcajeService marcajeService;

    @Operation(summary = "Obtener todos los marcajes", description = """
          Devuelve un conjunto de todos los marcajes registrados.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcajes obtenidos exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getAllMarcajes() {
        try {
            Set<Marcaje> marcajes = marcajeService.findAll();
            return ResponseEntity.ok(marcajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes: " + e.getMessage()));
        }
    }

    @Operation(summary = "Obtener marcajes por hotel y trabajador", description = """
          Devuelve los marcajes de un trabajador específico en un hotel determinado.
          **Permisos requeridos**: Solamente Administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcajes obtenidos exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("search/{idHotel}/{idTrabajador}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getMarcajesByHotelAndTrabajador(
            @PathVariable int idHotel,
            @PathVariable int idTrabajador) {
        try {
            Set<Marcaje> marcajes = marcajeService.findByIdHotelAndIdTrabajador(idHotel, idTrabajador);
            return ResponseEntity.ok(marcajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes: " + e.getMessage()));
        }
    }

    @Operation(summary = "Obtener marcajes por hotel, trabajador y fecha", description = """
          Devuelve los marcajes de un trabajador específico en un hotel determinado.
          **Permisos requeridos**: Solamente Administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcajes obtenidos exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("search/day/{idHotel}/{idTrabajador}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getMarcajesByHotelAndTrabajadorAndDay(
            @PathVariable int idHotel,
            @PathVariable int idTrabajador,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date fecha) {
        try {
            // Crear rango de fechas para el día completo
            Calendar cal = Calendar.getInstance();
            cal.setTime(fecha);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date startDate = cal.getTime();

            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Date endDate = cal.getTime();

            Set<Marcaje> marcajes = marcajeService.findByIdHotelAndIdTrabajadorAndFechaHoraBetween(
                    idHotel, idTrabajador, startDate, endDate);
            return ResponseEntity.ok(marcajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes: " + e.getMessage()));
        }
    }

    @Operation(summary = "Obtener marcajes por rango de fechas", description = """
      Devuelve los marcajes realizados entre dos fechas específicas.
      **Permisos requeridos**: Solo para administradores.
      """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcajes obtenidos exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de fecha inválidos.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para acceder a esta información.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/search/fechas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getMarcajesByRangoFechas(
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") Date fechaFin) {
        try {
            if (fechaInicio.after(fechaFin)) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("La fecha de inicio debe ser anterior a la fecha fin"));
            }

            Set<Marcaje> marcajes = marcajeService.findByRangoFechas(fechaInicio, fechaFin);
            return ResponseEntity.ok(marcajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes por rango de fechas: " + e.getMessage()));
        }
    }

    @Operation(summary = "Marcar inicio de jornada", description = """
        Registra un nuevo marcaje de inicio en el sistema.
        **Permisos requeridos**: Clientes o administradores.
        """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Marcaje de inicio creado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "400", description = "Datos del marcaje inválidos.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/inicio")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> marcarInicio(@Valid @RequestBody MarcajeDTO marcajeDTO) {
        try {
            Marcaje marcaje = convertToEntity(marcajeDTO, 'E');
            Marcaje nuevoMarcaje = marcajeService.addMarcaje(marcaje);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMarcaje);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseUpdate("Error de integridad de datos: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseUpdate("Error al crear marcaje de inicio: " + e.getMessage()));
        }
    }

    @Operation(summary = "Marcar fin de jornada", description = """
        Registra un nuevo marcaje de fin en el sistema.
        **Permisos requeridos**: Clientes o administradores.
        """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Marcaje de fin creado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "400", description = "Datos del marcaje inválidos.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/fin")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> marcarFin(@Valid @RequestBody MarcajeDTO marcajeDTO) {
        try {
            Marcaje marcaje = convertToEntity(marcajeDTO, 'S');
            Marcaje nuevoMarcaje = marcajeService.addMarcaje(marcaje);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMarcaje);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponseUpdate("Error de integridad de datos: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponseUpdate("Error al crear marcaje de fin: " + e.getMessage()));
        }
    }

    private Marcaje convertToEntity(MarcajeDTO dto, char accion) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitud(), dto.getLatitud()));

        Marcaje marcaje = new Marcaje();
        marcaje.setIdHotel(dto.getIdHotel());
        marcaje.setIdTrabajador(dto.getIdTrabajador());
        marcaje.setFechaHora(new Date());
        marcaje.setLocalizacion(point);
        marcaje.setAccion(accion);
        marcaje.setDescargado('N');
        marcaje.setObservaciones(dto.getObservaciones());

        return marcaje;
    }

    @Operation(summary = "Modificar un marcaje", description = """
      Actualiza la información de un marcaje existente.
      Formato de fecha  10/02/2025-10:54:02.
      **Permisos requeridos**: Solo para administradores.
      """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcaje modificado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "404", description = "Marcaje no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/update/{idMarcaje}") //Formato fecha  10/02/2025-10:54:02
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> updateMarcaje(
            @PathVariable long idMarcaje,
            @Valid @RequestBody MarcajeUpdateDTO marcajeUpdateDTO) {
        try {
            Marcaje marcaje = convertUpdateDTOToEntity(marcajeUpdateDTO);
            Marcaje marcajeActualizado = marcajeService.modifyMarcaje(idMarcaje, marcaje);
            return ResponseEntity.ok(marcajeActualizado);
        } catch (MarcajeNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al actualizar marcaje: " + e.getMessage()));
        }
    }

    private Marcaje convertUpdateDTOToEntity(MarcajeUpdateDTO dto) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitud(), dto.getLatitud()));

        // Convert Date to Instant then to LocalDateTime
        Instant instant = dto.getFechaHora().toInstant();
        LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        Marcaje marcaje = new Marcaje();
        marcaje.setIdHotel(Math.toIntExact(dto.getIdHotel()));
        marcaje.setIdTrabajador(Math.toIntExact(dto.getIdTrabajador()));
        marcaje.setFechaHora(dto.getFechaHora());
        marcaje.setLocalizacion(point);
        marcaje.setAccion(dto.getAccion());
        marcaje.setObservaciones(dto.getObservaciones());

        return marcaje;
    }

    @Operation(summary = "Marcar marcaje como descargado", description = """
          Actualiza el estado de un marcaje a 'descargado'.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Marcaje actualizado exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "404", description = "Marcaje no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/download/{idMarcaje}")  // Cambiado a POST con ruta más descriptiva
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> markAsDownloaded(@PathVariable long idMarcaje) {
        try {
            Marcaje marcajeActualizado = marcajeService.descargarMarcaje(idMarcaje);
            return ResponseEntity.ok(marcajeActualizado);
        } catch (MarcajeNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al actualizar estado de marcaje: " + e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar un marcaje", description = """
          Elimina un marcaje del sistema.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Marcaje eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Marcaje no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete/{idMarcaje}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> deleteMarcaje(@PathVariable long idMarcaje) {
        try {
            marcajeService.deleteMarcaje(idMarcaje);
            return ResponseEntity.noContent().build();
        } catch (MarcajeNotFoundException e) {
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al eliminar marcaje: " + e.getMessage()));
        }
    }

    // Metodos para la descarga de marcajes:

    // Obtener marcajes no descargados por hotel
    @Operation(summary = "Obtener marcajes no descargados por hotel", description = """
          Devuelve una lista de marcajes no descargados para un hotel específico.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de marcajes no descargados obtenida exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/no-descargados/{idHotel}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getMarcajesNoDescargados(@PathVariable int idHotel) {
        try {
            Set<Marcaje> marcajes = marcajeService.findByIdHotel(idHotel);

            Set<Marcaje> noDescargados = marcajes.stream()
                    .filter(m -> m.getDescargado() == 'N')
                    .collect(Collectors.toSet());

            // Marcar como descargados
            noDescargados.stream()
                    .forEach(m -> marcajeService.descargarMarcaje(m.getIdMarcaje()));

            return ResponseEntity.ok(noDescargados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes no descargados: " + e.getMessage()));
        }
    }

    // Obtener marcajes por hotel, rango de fechas y trabajador
    @Operation(summary = "Obtener marcajes por hotel, fechas y trabajador", description = """
          Devuelve todos los marcajes para un hotel, rango de fechas y trabajador específico,
          y marca los no descargados como descargados.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de marcajes obtenida exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/hotel/{idHotel}/fechas/{fechaInicio}/{fechaFin}/trabajador/{idTrabajador}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getMarcajesByHotelFechasAndTrabajador(
            @PathVariable int idHotel,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin,
            @PathVariable int idTrabajador) {
        try {
            // Ajustar el rango de fechas
            Calendar calendar = Calendar.getInstance();

            // Establecer fechaInicio a las 00:00:00.000
            calendar.setTime(fechaInicio);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startDate = calendar.getTime();

            // Establecer fechaFin a las 23:59:59.999
            calendar.setTime(fechaFin);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date endDate = calendar.getTime();

            Set<Marcaje> marcajes = marcajeService.findByIdHotelAndIdTrabajadorAndFechaHoraBetween(
                    idHotel, idTrabajador, startDate, endDate);

            // Marcar no descargados como descargados
            marcajes.stream()
                    .filter(m -> m.getDescargado() == 'N')
                    .forEach(m -> marcajeService.descargarMarcaje(m.getIdMarcaje()));

            return ResponseEntity.ok(marcajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes: " + e.getMessage()));
        }
    }

    // Obtener marcajes por hotel y rango de fechas
    @Operation(summary = "Obtener marcajes por hotel y fechas", description = """
          Devuelve todos los marcajes para un hotel y rango de fechas específico,
          y marca los no descargados como descargados.
          **Permisos requeridos**: Solo para administradores.
          """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de marcajes obtenida exitosamente.",
                    content = @Content(schema = @Schema(implementation = Marcaje.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permisos para realizar esta acción.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/hotel/{idHotel}/fechas/{fechaInicio}/{fechaFin}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> getMarcajesByHotelAndFechas(
            @PathVariable int idHotel,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
        try {
            // Ajustar el rango de fechas
            Calendar calendar = Calendar.getInstance();

            // Establecer fechaInicio a las 00:00:00.000
            calendar.setTime(fechaInicio);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startDate = calendar.getTime();

            // Establecer fechaFin a las 23:59:59.999
            calendar.setTime(fechaFin);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date endDate = calendar.getTime();

            // Obtener todos los marcajes en el rango de fechas
            Set<Marcaje> todosMarcajes = marcajeService.findByRangoFechas(startDate, endDate);

            // Filtrar por hotel
            Set<Marcaje> marcajes = todosMarcajes.stream()
                    .filter(m -> m.getIdHotel() == idHotel)
                    .collect(Collectors.toSet());

            // Marcar no descargados como descargados
            marcajes.stream()
                    .filter(m -> m.getDescargado() == 'N')
                    .forEach(m -> marcajeService.descargarMarcaje(m.getIdMarcaje()));

            return ResponseEntity.ok(marcajes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al obtener marcajes: " + e.getMessage()));
        }
    }
}
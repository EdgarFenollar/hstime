package com.project.hstime.exception;

public class HorarioNotFoundException extends RuntimeException{
    public HorarioNotFoundException() {
        super();
    }
    public HorarioNotFoundException(String message) {
        super(message);
    }
    public HorarioNotFoundException(long id_horario) {
        super("Horario no encontrado: " + id_horario);
    }
}

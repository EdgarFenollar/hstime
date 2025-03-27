package com.project.hstime.exception;

public class MarcajeNotFoundException extends RuntimeException{
    public MarcajeNotFoundException() {
        super();
    }
    public MarcajeNotFoundException(String message) {
        super(message);
    }
    public MarcajeNotFoundException(long id_marcaje) {
        super("Marcaje no encontrado: " + id_marcaje);
    }
}

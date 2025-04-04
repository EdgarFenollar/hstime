package com.project.hstime.service;

import com.project.hstime.domain.Horario;
import com.project.hstime.domain.Marcaje;
import com.project.hstime.exception.HorarioNotFoundException;
import com.project.hstime.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class HorarioServiceImpl implements HorarioService{
    @Autowired
    private HorarioRepository horarioRepository;

    @Override
    public Set<Horario> findAll() {
        return horarioRepository.findAll();
    }

    @Override
    public Optional<Horario> findByIdHorario(long idHorario) {
        return horarioRepository.findByIdHorario(idHorario);
    }

    @Override
    public Set<Horario> findByIdHotelAndIdTrabajador(int idHotel, int idTrabajador) {
        return horarioRepository.findByIdHotelAndIdTrabajador(idHotel, idTrabajador);
    }

    @Override
    public Set<Horario> findByRangoFechas(int idHotel, int idTrabajador, Date fechaInicio, Date fechaFin) {
        Date inicio = setStartOfDay(fechaInicio);
        Date fin = setEndOfDay(fechaFin.equals(fechaInicio) ? fechaInicio : fechaFin);

        return horarioRepository.findByIdHotelAndIdTrabajadorAndFechaBetween(idHotel, idTrabajador, inicio, fin);
    }

    private Date setStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date setEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    @Override
    public Set<Horario> findByIdHotelAndIdTrabajadorAndFechaHoraBetween(int idHotel, int idTrabajador, Date fechaInicio, Date fechaFin) {
        return horarioRepository.findByIdHotelAndIdTrabajadorAndFechaBetween(idHotel, idTrabajador, fechaInicio, fechaFin);
    }

    @Override
    public Horario addHorario(Horario horario) {
        return horarioRepository.save(horario);
    }

    @Override
    public Horario modifyHorario(long idHorario, Horario newHorario) {
        Horario horario = horarioRepository.findByIdHorario(idHorario)
                .orElseThrow(() -> new HorarioNotFoundException(idHorario));
        horario.setIdHotel(newHorario.getIdHotel());
        horario.setIdTrabajador(newHorario.getIdTrabajador());
        horario.setFecha(newHorario.getFecha());
        horario.setDepartamento(newHorario.getDepartamento());
        horario.setConcepto(newHorario.getConcepto());
        horario.setHorario(newHorario.getHorario());

        return horarioRepository.save(horario);
    }

    @Override
    public void deleteHorario(long idHorario) {
        horarioRepository.findByIdHorario(idHorario)
                .orElseThrow(() -> new HorarioNotFoundException(idHorario));
        horarioRepository.deleteById(idHorario);
    }
}

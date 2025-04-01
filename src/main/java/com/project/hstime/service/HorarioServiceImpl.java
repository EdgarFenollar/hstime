package com.project.hstime.service;

import com.project.hstime.domain.Horario;
import com.project.hstime.domain.Marcaje;
import com.project.hstime.exception.HorarioNotFoundException;
import com.project.hstime.exception.MarcajeNotFoundException;
import com.project.hstime.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

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

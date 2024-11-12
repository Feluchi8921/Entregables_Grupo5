package com.example.services;

import com.example.dto.CuentaDto;
import com.example.entities.Cuenta;
import com.example.mappers.CuentaMapper;
import com.example.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaService {

    @Autowired
    CuentaRepository cuentaRepository;
    @Autowired
    CuentaMapper cuentaMapper;

    @Transactional(readOnly = true)
    public List<CuentaDto> findAll() {
        return this.cuentaRepository.findAll()
                .stream().map(CuentaDto::new).toList();
    }

    @Transactional(readOnly = true)
    public CuentaDto findById(Long id) {
        return this.cuentaRepository.findById(id)
                .map(CuentaDto::new)
                .orElseThrow(() -> new RuntimeException("Cuenta con ID " + id + " no encontrada."));
    }

    @Transactional
    public CuentaDto save(CuentaDto cuentaDto) {
        if (cuentaDto.getTarifa() == null || cuentaDto.getFechaAlta() == null || cuentaDto.getSaldo() == null) {
            throw new RuntimeException("Son requeridos todos los campos para crear una cuenta.");
        }

        // Convertir el DTO a entidad Admin
        Cuenta nuevaCuenta = cuentaMapper.toCuenta(cuentaDto);

        // Si pasa las validaciones, guardar el estudiante en base de datos
        Cuenta guardada = cuentaRepository.save(nuevaCuenta);

        return cuentaMapper.toCuentaDto(guardada);
    }

    public CuentaDto update(Long id, CuentaDto cuentaDto) {
        if (cuentaDto.getTarifa() == null || cuentaDto.getFechaAlta() == null || cuentaDto.getSaldo() == null) {
            throw new RuntimeException("Son requeridos todos los campos para crear una cuenta.");
        }

        // Buscar el admin por ID
        Cuenta adminExistente = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta con ID " + id + " no encontrada."));

        // Actualizar los campos del admin con los datos del DTO
        adminExistente.setTarifa(cuentaDto.getTarifa());
        adminExistente.setFechaAlta(cuentaDto.getFechaAlta());
        adminExistente.setSaldo(cuentaDto.getSaldo());
        adminExistente.setHabilitada(cuentaDto.isHabilitada());

        // Guardar los cambios
        cuentaRepository.save(adminExistente);

        return cuentaMapper.toCuentaDto(adminExistente);
    }

    public CuentaDto delete(Long id) {
        Cuenta cuentaEliminada = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta con ID " + id + " no encontrada."));

        // Eliminar el estudiante
        cuentaRepository.delete(cuentaEliminada);

        // Devolver el admin eliminado
        return cuentaMapper.toCuentaDto(cuentaEliminada);
    }

    public void cambiarEstadoCuenta(Long id, boolean estado) {
        Cuenta adminExistente = cuentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta con ID " + id + " no encontrada."));

        adminExistente.setHabilitada(estado);
        cuentaRepository.save(adminExistente);
    }

}

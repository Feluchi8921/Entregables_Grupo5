package com.example.services;

import com.example.dto.AdminDto;
import com.example.dto.CantidadMonopatinesEstadoDto;
import com.example.dto.TarifaRequestDto;
import com.example.entities.Admin;
import com.example.feignClients.*;
import com.example.mappers.AdminMapper;
import com.example.model.Monopatin;
import com.example.model.Parada;
import com.example.repository.AdminRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    @Autowired
    private final AdminRepository adminRepository;
    @Autowired
    private final AdminMapper adminMapper;
    @Autowired
    private final CuentaFeign cuentaFeign;
    @Autowired
    private final MonopatinFeign monopatinFeign;
    @Autowired
    private final ParadaFeign paradaFeign;
    @Autowired
    private final ViajeFeign viajeFeign;

    @Transactional(readOnly = true)
    public List<AdminDto> findAll() {
        return this.adminRepository.findAll()
                .stream().map(AdminDto::new).toList();
    }

    @Transactional(readOnly = true)
    public AdminDto findById(Long id) {
        return this.adminRepository.findById(id)
                .map(AdminDto::new)
                .orElseThrow(() -> new RuntimeException("Administrador con ID " + id + " no encontrado."));
    }

    @Transactional
    public AdminDto save(AdminDto adminDto) {
        if (adminDto.getNombre().isEmpty() || adminDto.getApellido().isEmpty() ||
                adminDto.getDni().isEmpty() || adminDto.getContrasenia().isEmpty()) {
            throw new RuntimeException("Todos los campos son requeridos para crear un administrador.");
        }

        // Convertir el DTO a entidad Admin
        Admin nuevoAdmin = adminMapper.toAdmin(adminDto);

        // Si pasa las validaciones, guardar el estudiante en base de datos
        Admin guardado = adminRepository.save(nuevoAdmin);

        return adminMapper.toAdminDto(guardado);
    }

    public AdminDto update(Long id, AdminDto adminDto) {
        if (adminDto.getNombre().isEmpty() || adminDto.getApellido().isEmpty() ||
                adminDto.getDni().isEmpty() || adminDto.getContrasenia().isEmpty()) {
            throw new RuntimeException("Todos los campos son requeridos para modificar un administrador.");
        }

        // Buscar el admin por ID
        Admin adminExistente = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador con ID " + id + " no encontrado."));

        // Actualizar los campos del admin con los datos del DTO
        adminExistente.setNombre(adminDto.getNombre());
        adminExistente.setApellido(adminDto.getApellido());
        adminExistente.setDni(adminDto.getDni());
        adminExistente.setContrasenia(adminDto.getContrasenia());

        // Guardar los cambios
        adminRepository.save(adminExistente);

        return adminMapper.toAdminDto(adminExistente);
    }

    public AdminDto delete(Long id) {
        Admin adminExistente = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador con ID " + id + " no encontrado."));

        // Eliminar el estudiante
        adminRepository.delete(adminExistente);

        // Devolver el admin eliminado
        return adminMapper.toAdminDto(adminExistente);
    }



    // ------------------------------------------------ Servicios ------------------------------------------------

    // 3. b) Anula cuentas para inhabilitar el uso momentáneo de la misma.
    @Transactional
    public void cambiarEstadoCuenta(Long id, boolean estado) {
        try {
            cuentaFeign.cambiarEstadoCuenta(id, estado);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Monopatin crearMonopatin(Monopatin monopatin) {
        try {
            return monopatinFeign.createMonopatin(monopatin);
        } catch (Exception e) {
            throw new RuntimeException("El monopatín no pudo ser creado.");
        }
    }

    @Transactional
    public Monopatin modificarMonopatin(Monopatin monopatin, Long id) {
        try {
            return monopatinFeign.updateMonopatin(monopatin, id);
        } catch (Exception e) {
            throw new RuntimeException("El monopatín no pudo ser modificado.");
        }
    }

    @Transactional
    public Monopatin eliminarMonopatin(Long id) {
        try {
            return monopatinFeign.deleteMonopatin(id);
        } catch (Exception e) {
            throw new RuntimeException("El monopatín no pudo ser eliminado.");
        }
    }

    @Transactional
    public Parada crearParada(Parada parada) {
        try {
            return paradaFeign.createParada(parada);
        } catch (Exception e) {
            throw new RuntimeException("La parada no pudo ser creada.");
        }
    }

    @Transactional
    public Parada modificarParada(Parada parada, Long id) {
        try {
            return paradaFeign.updateParada(parada, id);
        } catch (Exception e) {
            throw new RuntimeException("La parada no pudo ser modificada.");
        }
    }

    @Transactional
    public Parada eliminarParada(Long id) {
        try {
            return paradaFeign.deleteParada(id);
        } catch (Exception e) {
            throw new RuntimeException("La parada no pudo ser eliminada.");
        }
    }

    // ------------------------------------- Servicios -------------------------------------

    // 3. e) Cantidad de monopatines según su estado
    public CantidadMonopatinesEstadoDto obtenerCantidadMonopatines() {
        int cantidadEnOperacion = monopatinFeign.getCantidadEnOperacion();
        int cantidadEnMantenimiento = monopatinFeign.getCantidadEnMantenimiento();
        return new CantidadMonopatinesEstadoDto(cantidadEnOperacion, cantidadEnMantenimiento);
    }

    // 3. f) Modifica la tarifa normal de los viajes.
    @Transactional
    public void modificarTarifaNormal(TarifaRequestDto tarifaDto) {
        try {
            viajeFeign.modificarTarifaNormal(tarifaDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar la tarifa normal.");
        }
    }

    @Transactional
    public void modificarTarifaExtra(TarifaRequestDto tarifaDto) {
        try {
            viajeFeign.modificarTarifaExtra(tarifaDto);
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar la tarifa extra.");
        }
    }

}
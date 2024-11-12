package com.example.feignClients;

import com.example.dto.ReporteMonopatinDto;
import com.example.model.Monopatin;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "microservicio-monopatin")
public interface MonopatinFeign {

    // Agrega un monopatín
    @PostMapping("api/monopatines/")
    Monopatin createMonopatin(@RequestBody Monopatin monopatin);

    // Modifica un monopatín
    @PutMapping("api/monopatines/id/{id}")
    Monopatin updateMonopatin(@RequestBody Monopatin monopatin, @PathVariable Long id);

    // Elimina un monopatín
    @DeleteMapping("api/monopatines/id/{id}")
    Monopatin deleteMonopatin(@PathVariable("id") Long id);

    // 3. e) Cantidad de monopatines según su estado.
    @GetMapping("api/monopatines/cantidad/operacion")
    int getCantidadEnOperacion();

    @GetMapping("api/monopatines/cantidad/mantenimiento")
    int getCantidadEnMantenimiento();

}
package com.segfault.recuperaciones.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.List;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.segfault.recuperaciones.model.BedList;
import com.segfault.recuperaciones.repository.BedListRepository;

@RestController
@RequestMapping("/bed")
@Api(value="Sala de recuperaciones", description="Descripción del comportamiento de la API REST")
public class BedListController {

    @Autowired
    BedListRepository bedRepository;


    @ApiOperation(value = "Lista de las camas en existencia", response = List.class)

    @GetMapping(value = "/list")
    public ResponseEntity index() {
        return ResponseEntity.ok(bedRepository.findAll());
    }


    @ApiOperation(value = "Buscar cama mediante un id", response = BedList.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message="Encontrado con éxito"),
        @ApiResponse(code = 404, message="El recurso no se ha podido encontrar")
    })

    @GetMapping(value = "/show", params = "id")
    public ResponseEntity getBed(@RequestParam(value = "id") Long id) {
        Optional<BedList> foundBedList = bedRepository.findById(id);
        if(foundBedList.isPresent()){
            return ResponseEntity.ok(foundBedList.get());
        } else {
            return ResponseEntity.badRequest().body("No existe una cama con el id: " + id);
        }
    }


    @ApiOperation(value = "Buscar camas según su condicion", response = BedList.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Recursos encontrados con exito"),
        @ApiResponse(code = 404, message = "No existen camas en esa condicion")
    })

    @GetMapping(value = "/cond", params = "condition")
    public ResponseEntity getBed(@RequestParam(value="condition") String condition){
        List<BedList> foundBedListArr = bedRepository.findByCondition(condition);
    	if(!foundBedListArr.isEmpty()){
            return ResponseEntity.ok(foundBedListArr);
    	}
    	else {
    	    return ResponseEntity.badRequest().body("No existen Camas " + condition + "s");
    	}
    }   
    

    @ApiOperation(value = "Añadir una cama con una condición y paciente", response = BedList.class)
    @PostMapping(value = "/add")
    public ResponseEntity addToBedList(@RequestParam(value = "condition") String condition, @RequestParam(value = "paciente") Long paciente) {
        return ResponseEntity.ok(bedRepository.save(new BedList(paciente, condition)));
    }

    /*
    @ApiOperation(value = "Añadir una cama con la condición de esta", response = BedList.class)

    @PostMapping(value = "/add")
    public ResponseEntity addToBedList(@RequestParam(value = "condition") String condition) {
        return ResponseEntity.ok(bedRepository.save(new BedList(-1, condition)));
    }
    */

    @ApiOperation(value = "Añadir una cama vacía", response = BedList.class)
    @PostMapping(value = "/vacia")
    public ResponseEntity addToBedList() {
        return ResponseEntity.ok(bedRepository.save(new BedList(-1, "Vacía")));
    }


    @ApiOperation(value = "Actualizar los valores de una cama", response = BedList.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message="Encontrado con éxito"),
        @ApiResponse(code = 404, message="El recurso no existe")
    })
    @PutMapping(value = "/")
    public ResponseEntity updateBedList(@RequestParam(value = "condition") String condition, @RequestParam(value = "id") Long id, @RequestParam(value = "paciente") Long paciente){
        Optional<BedList> optionalBedList = bedRepository.findById(id);
        if (!optionalBedList.isPresent()) {
            return ResponseEntity.badRequest().body("No existe una cama con el id: " + id);
        }
        BedList foundBedList = optionalBedList.get();
        foundBedList.setPaciente(paciente);
        foundBedList.setCondition(condition);
        return ResponseEntity.ok(bedRepository.save(foundBedList));
    }


    @ApiOperation(value = "Eliminar una cama", response = BedList.class)
    @DeleteMapping(value = "/")
    public ResponseEntity removeBedList(@RequestParam(value = "id") Long id) {
        bedRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

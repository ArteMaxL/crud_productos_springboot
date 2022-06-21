package com.crud.controller;

import com.crud.dto.Mensaje;
import com.crud.dto.ProductoDto;
import com.crud.entity.Producto;
import com.crud.service.ProductoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producto")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    @Autowired
    ProductoService productoService;

    @GetMapping("/lista")
    public ResponseEntity<List<Producto>> list(){
        List<Producto> list = productoService.list();
        return new ResponseEntity<List<Producto>>(list, HttpStatus.OK);
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<Producto> getById(@PathVariable("id") int id){
        if (!productoService.existsById(id)){
            return new ResponseEntity(new Mensaje("No existe el producto con el id: " + id), HttpStatus.NOT_FOUND);
        }
        Producto producto = productoService.getOne(id).get();
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    @GetMapping("/detallenombre/{nombre}")
    public ResponseEntity<Producto> getByNombre(@PathVariable("nombre") String nombre){
        if (!productoService.existsByNombre(nombre)){
            return new ResponseEntity(new Mensaje("No existe el producto con el nombre: " + nombre), HttpStatus.NOT_FOUND);
        }
        Producto producto = productoService.getByNombre(nombre).get();
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }
@PostMapping("/crear")
    public ResponseEntity<?> create(@RequestBody ProductoDto productoDto){
        if(StringUtils.isBlank(productoDto.getNombre())){
            return new ResponseEntity<>(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }
        if(productoDto.getPrecio() == null || productoDto.getPrecio() < 0.1){
            return new ResponseEntity<>(new Mensaje("El precio debe ser mayor a 0"), HttpStatus.BAD_REQUEST);
        }
        if(productoService.existsByNombre(productoDto.getNombre())){
            return new ResponseEntity<>(new Mensaje("El nombre ya existe"), HttpStatus.BAD_REQUEST);
        }
        Producto producto = new Producto(productoDto.getNombre(), productoDto.getPrecio());
        productoService.save(producto);

        return new ResponseEntity<>(new Mensaje("Producto creado"), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody ProductoDto productoDto){
        if (!productoService.existsById(id)){
            return new ResponseEntity(new Mensaje("No existe el producto con el id: " + id), HttpStatus.NOT_FOUND);
        }
        if(productoService.existsByNombre(productoDto.getNombre()) && productoService.getByNombre(productoDto.getNombre()).get().getId() != id){
            return new ResponseEntity<>(new Mensaje("El nombre ya existe"), HttpStatus.BAD_REQUEST);
        }
        if(StringUtils.isBlank(productoDto.getNombre())){
            return new ResponseEntity<>(new Mensaje("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        }
        if(productoDto.getPrecio() < 0.1){
            return new ResponseEntity<>(new Mensaje("El precio debe ser mayor a 0"), HttpStatus.BAD_REQUEST);
        }

        Producto producto = productoService.getOne(id).get();
        producto.setNombre(productoDto.getNombre());
        producto.setPrecio(productoDto.getPrecio());
        productoService.save(producto);

        return new ResponseEntity<>(new Mensaje("Producto actualizado"), HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id){
        if (!productoService.existsById(id)){
            return new ResponseEntity(new Mensaje("No existe el producto con el id: " + id), HttpStatus.NOT_FOUND);
        }
        productoService.delete(id);

        return new ResponseEntity<>(new Mensaje("Producto eliminado"), HttpStatus.OK);
    }
}

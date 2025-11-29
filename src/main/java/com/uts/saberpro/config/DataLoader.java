package com.uts.saberpro.config;

import com.uts.saberpro.model.Usuario;
import com.uts.saberpro.model.Rol; // Importación específica
import com.uts.saberpro.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// @Component habilitado para cargar datos de prueba
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario admin si no existe
        if (usuarioService.findByEmail("admin@saberpro.edu.co").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("Sistema");
            admin.setEmail("admin@saberpro.edu.co");
            admin.setPassword("admin123");
            admin.setRol(Rol.ADMIN);
            admin.setActivo(true);
            usuarioService.save(admin);
            System.out.println("✅ Usuario admin creado: admin@saberpro.edu.co / admin123");
        }
        
        // Crear usuario coordinador si no existe
        if (usuarioService.findByEmail("coordinador@saberpro.edu.co").isEmpty()) {
            Usuario coordinador = new Usuario();
            coordinador.setNombre("Coordinador");
            coordinador.setApellido("Sistema");
            coordinador.setEmail("coordinador@saberpro.edu.co");
            coordinador.setPassword("coord123");
            coordinador.setRol(Rol.COORDINACION);
            coordinador.setActivo(true);
            usuarioService.save(coordinador);
            System.out.println("✅ Usuario coordinador creado: coordinador@saberpro.edu.co / coord123");
        }
    }
}
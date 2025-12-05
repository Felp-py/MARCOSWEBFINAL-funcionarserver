package com.example.demo.config;

import com.example.demo.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> {
            System.out.println("=== INTENTO DE LOGIN ===");
            System.out.println("Usuario ingresado: '" + username + "'");

            var todos = usuarioRepository.findAll();
            System.out.println("Total usuarios en BD: " + todos.size());

            Optional<com.example.demo.model.Usuario> usuarioOpt = usuarioRepository.findByCorreo(username);
            
            if (usuarioOpt.isEmpty()) {
                usuarioOpt = usuarioRepository.findByNombre(username);
                if (usuarioOpt.isPresent()) {
                    System.out.println("Encontrado por nombre: " + usuarioOpt.get().getNombre());
                }
            } else {
                System.out.println("Encontrado por correo: " + usuarioOpt.get().getCorreo());
            }

            if (usuarioOpt.isEmpty()) {
                System.out.println("NO ENCONTRADO. Usuarios disponibles:");
                todos.forEach(u -> {
                    System.out.println("   - Nombre: '" + u.getNombre() + 
                                     "', Correo: '" + u.getCorreo() + "'");
                });
                throw new UsernameNotFoundException("Usuario '" + username + "' no encontrado");
            }
            
            var usuario = usuarioOpt.get();
            String rol = (usuario.getIdRol() != null && usuario.getIdRol() == 1) ? "ADMIN" : "USER";
            
            System.out.println("🔑 Autenticando: " + usuario.getNombre());
            System.out.println("   Correo: " + usuario.getCorreo());
            System.out.println("   Rol asignado: " + rol);
            System.out.println("   Password (hash): " + usuario.getContrasena());
 
            if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
                throw new UsernameNotFoundException("Contraseña no configurada para usuario: " + usuario.getNombre());
            }
            
            return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getContrasena())
                .roles(rol)
                .build();
        };
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            System.out.println("Acceso denegado para: " + request.getRequestURI());
            response.sendRedirect("/acceso-denegado");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/inicio", "/catalogo", "/nosotros",
                    "/login", "/registro/**", "/error",
                    "/css/**", "/js/**", "/img/**", "/webjars/**",
                    "/registro/api/**", "/api/consulta/**", "/dni/**", "/api/**",
                    "/acceso-denegado", "/carrito/**", "/checkout/**"
                ).permitAll()

                .requestMatchers(
                    "/libros/**", 
                    "/admin/**",
                    "/compras/estadisticas",  
                    "/compras/estadisticas/**" 
                ).hasRole("ADMIN")

                .requestMatchers(
                    "/perfil/**",
                    "/compras/**", 
                    "/checkout",
                    "/pago/**"
                ).hasAnyRole("USER", "ADMIN")
    
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/inicio", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/acceso-denegado") 
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/registro/api/**",
                    "/api/consulta/**",
                    "/api/**"
                )
            )
            .build();
    }
}
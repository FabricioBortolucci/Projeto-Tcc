package com.produto.oficina;

import com.produto.oficina.model.enums.Role;
import com.produto.oficina.service.UsuarioService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class OficinaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OficinaApplication.class, args);
//        ApplicationContext context = SpringApplication.run(OficinaApplication.class, args);

        // Criar usuário ADMIN manualmente na inicialização
      /*  UsuarioService cadastroUsuarioService = context.getBean(UsuarioService.class);
        cadastroUsuarioService.cadastrarUsuario("Fabricio", "1234", Role.ADMIN);

        System.out.println("Usuário ADMIN cadastrado!");*/


    }

}

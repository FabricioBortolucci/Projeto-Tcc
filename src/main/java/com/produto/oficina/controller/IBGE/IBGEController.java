package com.produto.oficina.controller.IBGE;

import com.produto.oficina.service.IBGE.IBGEDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ibge")
public class IBGEController {

    private final IBGEDataService ibgeDataService;

    public IBGEController(IBGEDataService ibgeDataService) {
        this.ibgeDataService = ibgeDataService;
    }

    @GetMapping("/importar")
    public String importar() {
        ibgeDataService.inserirdados();
        return "importação concluida";
    }

}

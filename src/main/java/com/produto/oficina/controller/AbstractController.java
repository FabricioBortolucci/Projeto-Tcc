package com.produto.oficina.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractController {

    protected ResponseEntity<Void> htmxRedirect(final String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("HX-Redirect", url);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}

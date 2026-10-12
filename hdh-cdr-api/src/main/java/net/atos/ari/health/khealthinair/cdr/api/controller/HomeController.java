package net.atos.ari.health.khealthinair.cdr.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden
public class HomeController {

    @GetMapping("/")
    public RedirectView redirectToSwagger() {
        return new RedirectView("swagger-ui/index.html");
    }
}

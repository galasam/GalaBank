package com.gala.sam.ordercapture.entrypoint;

import io.swagger.annotations.SwaggerDefinition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SwaggerDefinition
public class WebController {

  @GetMapping("/")
  public String index() {
    return "index.html";
  }

}

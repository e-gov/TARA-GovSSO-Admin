package ee.ria.tara.controllers;

import ee.ria.tara.configuration.HomeController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class CustomHomeController extends HomeController {

    @Override
    @RequestMapping("/")
    public String index() {
        return "redirect:/index.html";
    }

}

package iuh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class HomeController {

    public HomeController() {
        super();
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String HomePage(Model model) {
        LocalDate date = LocalDate.now();
        String mess ="Welcome Thymeleaf";
        model.addAttribute("message", mess);
        model.addAttribute("date", date.toString());
        return "home";
    }

}

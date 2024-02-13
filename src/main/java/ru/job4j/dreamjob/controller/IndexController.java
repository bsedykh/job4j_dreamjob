package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@ThreadSafe
public class IndexController {
    @GetMapping({"/", "/index"})
    public String getIndex(Model model, HttpSession session) {
        ControllerUtils.setUser(model, session);
        return "index";
    }
}

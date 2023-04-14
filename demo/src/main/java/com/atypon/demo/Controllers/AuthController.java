package com.atypon.demo.Controllers;

import com.atypon.demo.Services.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String Authentication(){
        return "authentication";
    }
    @GetMapping("/signIn")
    public String signIn(){
        return "signIn";
    }
    @GetMapping("/signUp")
    public String signUp(){
        return "signUp";
    }
    @PostMapping("/connect")
    public String connect(@RequestParam("Id") String id,
                          @RequestParam("password") String password,
                          @RequestParam(value = "is-manager", required = false) boolean isManager,
                          Model model) {
        try {
            JsonNode rootNode = authService.connectUser(id,password,isManager);

            String Token = "Sign Up Failed";
            String workerURL = "Sign Up Failed";
            if( rootNode != null && rootNode.get("Token") != null) {
                Token = rootNode.get("Token").asText();
                workerURL = rootNode.get("URL").asText();
            }
            model.addAttribute("Token",Token);
            model.addAttribute("URL",workerURL);

            return "showCredential";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/authenticate")
    public String signIn(@RequestParam("Token") String Token ,@RequestParam("Password") String Password,
                         @RequestParam(value = "is-manager", required = false) boolean isManager,
                         HttpServletRequest request){

        if(!authService.checkCredentials(Token,Password,isManager)){
            return "invalidCr";
        }

        HttpSession session = request.getSession();
        session.setAttribute("Token", Token);
        session.setAttribute("Password", Password);
        String url = authService.getURL(Token,isManager);
        session.setAttribute("URL",url);
        return "redirect:/Bank/Home";


    }



}

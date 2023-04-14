package com.atypon.demo.Controllers;

import com.atypon.demo.Services.DeleteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("Bank/Delete")
public class DeletingController {
    private final DeleteService deleteService;
    @Autowired
    public DeletingController(DeleteService deleteService) {
        this.deleteService = deleteService;
    }

    @PostMapping("/DB")
    public String DeleteBranch(@RequestParam("Database") String DB, HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        if(session.getAttribute("Token") == null) {
            model.addAttribute("result","Authentication Failed");
            model.addAttribute("link","/");
            return "statusPage";
        }
        String Token = session.getAttribute("Token").toString();
        String Password = session.getAttribute("Password").toString();
        String url = session.getAttribute("URL").toString();
        String result = deleteService.DeleteBranch(DB,Token,Password,url);
        model.addAttribute("result",result);
        model.addAttribute("link","/Bank/Home");
        return "statusPage";
    }

    @PostMapping("/ById")
    public String DeleteById(@RequestParam("Database") String DB,
                             @RequestParam("Collection") String Collection ,
                             @RequestParam("Id") String id,
                             HttpServletRequest request,
                             Model model){

        HttpSession session = request.getSession();
        if(session.getAttribute("Token") == null) {
            model.addAttribute("result","Authentication Failed");
            model.addAttribute("link","/");
            return "statusPage";
        }

        String Token = session.getAttribute("Token").toString();
        String Password = session.getAttribute("Password").toString();
        String url = session.getAttribute("URL").toString();

        String result = deleteService.DeleteRecord(DB,Collection,id,Token,Password,url);
        model.addAttribute("result",result);
        model.addAttribute("link","/Bank/Home");
        return "statusPage";
    }
}

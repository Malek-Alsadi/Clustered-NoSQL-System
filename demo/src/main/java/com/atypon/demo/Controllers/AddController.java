package com.atypon.demo.Controllers;

import com.atypon.demo.Objects.Customers;
import com.atypon.demo.Objects.Employee;
import com.atypon.demo.Services.AddingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/Bank/Add")
public class AddController {
    private final AddingService addingService;
    @Autowired
    public AddController(AddingService addingService) {
        this.addingService = addingService;
    }

    @PostMapping("/Branch")
    public String addBranch(@RequestParam("name") String name, HttpServletRequest request,Model model){
        HttpSession session = request.getSession();
        if(session.getAttribute("Token") == null) {
            model.addAttribute("result","Authentication Failed");
            model.addAttribute("link","/");
            return "statusPage";
        }

        String Token = session.getAttribute("Token").toString();
        String Password = session.getAttribute("Password").toString();
        String url = session.getAttribute("URL").toString();
        String result = addingService.addingBranchs(name,Token,Password,url);
        result += addingService.addingCollection(name,Token,Password,url);
        model.addAttribute("result",result);
        model.addAttribute("link","/Bank/Home");
        return "statusPage";
    }

    @PostMapping("/Costumer")
    public String addCostumer(@RequestParam("Branch") String db,
                              @RequestParam("Id") String Id,
                              @RequestParam("Name") String name,
                              @RequestParam("Balance") double balance,
                              HttpServletRequest request,
                              Model model){

        Customers customers = new Customers(Id, name, balance);
        HttpSession session = request.getSession();
        if(session.getAttribute("Token") == null) {
            model.addAttribute("result","Authentication Failed");
            model.addAttribute("link","/");
            return "statusPage";
        }

        String Token = session.getAttribute("Token").toString();
        String Password = session.getAttribute("Password").toString();
        String url = session.getAttribute("URL").toString();
        String result = addingService.addingRecordCustomer(db,customers,Token,Password,url);
        model.addAttribute("result",result);
        model.addAttribute("link","/Bank/Home");
        return "statusPage";
    }
    @PostMapping("/Employee")
    public String addEmployee(@RequestParam("Branch") String db,
                              @RequestParam("Id") String id,
                              @RequestParam("Name") String name,
                              @RequestParam("Role") String Role,
                              @RequestParam("Salary") double Salary,
                              HttpServletRequest request,
                              Model model){

        Employee employee = new Employee(id, name, Role,Salary);
        HttpSession session = request.getSession();
        if(session.getAttribute("Token") == null) {
            model.addAttribute("result","Authentication Failed");
            model.addAttribute("link","/");
            return "statusPage";
        }

        String Token = session.getAttribute("Token").toString();
        String Password = session.getAttribute("Password").toString();
        String url = session.getAttribute("URL").toString();
        String result = addingService.addingRecordEmployee(db,employee,Token,Password,url);
        model.addAttribute("result",result);
        model.addAttribute("link","/Bank/Home");
        return "statusPage";
    }

}

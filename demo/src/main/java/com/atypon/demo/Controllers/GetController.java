package com.atypon.demo.Controllers;

import com.atypon.demo.Objects.Customers;
import com.atypon.demo.Objects.Employee;
import com.atypon.demo.Services.GetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.util.List;

@Controller
@RequestMapping("/Bank/Getting")
public class GetController {
    private final GetService getService;
    @Autowired
    public GetController(GetService getService) {
        this.getService = getService;
    }

    @PostMapping("/AllEmployees")
    public String displayEmployees(@RequestParam("Database") String Database,
                                   HttpServletRequest request,
                                   Model model) throws MalformedURLException, JsonProcessingException {

        HttpSession session = request.getSession();
        if(session.getAttribute("Token") == null) {
            model.addAttribute("result","Authentication Failed");
            model.addAttribute("link","/");
            return "statusPage";
        }

        String Token = session.getAttribute("Token").toString();
        String Password = session.getAttribute("Password").toString();
        String url = session.getAttribute("URL").toString();
        List<Employee> list = getService.getAll(Database,Token,Password,url);
        model.addAttribute("employees",list);

        return "showAllEmployees";
    }
    @PostMapping("/CostumerInfo")
    public String displayCostumerInfo(@RequestParam("Database") String Database,
                                      @RequestParam("Id") String Id,
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
        Customers customer = getService.getCostumerById(Database,Id,Token,Password,url);
        if(customer == null){
            model.addAttribute("result","Id not exist");
            return "statusPage";
        }
        model.addAttribute("customer",customer);
        return "showCostumer";

    }
    @PostMapping("/EmployeeInfo")
    public String displayEmployeeInfo(@RequestParam("Database") String Database,
                                      @RequestParam("Id") String Id,
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
        Employee employee = getService.getEmployeeById(Database,Id,Token,Password,url);
        if(employee == null){
            model.addAttribute("result","Id not exist");
        }
        model.addAttribute("employee",employee);

        return "showEmployee";

    }
    @PostMapping("/CostumersByName")
    public String AllCostumersCalled(@RequestParam("Database") String Database,@RequestParam("Name") String name,
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
        List<Customers> list = getService.allWithName(Database,name,Token,Password,url);
        model.addAttribute("list",list);
        return "showCostumers";
    }

    @PostMapping("/UpdateBalance")
    public String Update(@RequestParam("Database") String Database,@RequestParam("Id") String Id,@RequestParam("Amount") double Amount,
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

        getService.Update(Database,Id,Amount,Token,Password,url);

        return displayCostumerInfo(Database,Id,request,model);
    }


}

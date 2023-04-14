package com.atypon.demo.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Bank")
public class ApiController {

    @GetMapping("/Home")
    public String home(){
        return "BankHome";
    }
    @GetMapping("/Adding")
    public String addingPage(){
        return "adding";
    }
    @GetMapping("/branchAdding")
    public String addingBranch(){
        return "CreateBranch";
    }
    @GetMapping("/CostumerAdding")
    public String CostumerAdding(){
        return "costumerForm";
    }
    @GetMapping("/EmployeeAdding")
    public String EmployeeAdding(){
        return "EmployeeForm";
    }

    @GetMapping("/Deleting")
    public String Deleting(){
        return "deleting";
    }

    @GetMapping("/BranchDeleting")
    public String DeleteBranch(String DB){
        return "DeleteBranch";
    }

    @GetMapping("/DeleteById")
    public String Delete(){
        return "DeleteForm";
    }
    @GetMapping("/Getting")
    public String Update(){
        return "GetList";
    }
    @GetMapping("/Getting/costumerID")
    public String GetCostumerById(){
        return "ChooseDBGet";
    }

    @GetMapping("/Getting/employeeID")
    public String GetEmployeeById(){
        return "ChooseDBGet2";
    }
    @GetMapping("/Getting/AllEmployee")
    public String GetAllEmployee(){
        return "ChooseDBGet3";
    }

    @GetMapping("/Getting/CostumerWithName")
    public String GetCostumersWithName(){
        return "ChooseDBGet_Name";
    }

    @GetMapping("/Updating")
    public String withDraw(){
        return "ChooseIdAmount";
    }

}

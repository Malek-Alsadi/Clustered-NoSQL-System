package com.atypon.demo.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee extends Persons{
    private String Role;
    private double Salary;

    public Employee() {
    }

    public Employee(String Id, String name, String role, double salary) {
        super.setId(Id);
        super.setName(name);
        Role = role;
        Salary = salary;
    }

    @JsonProperty("Role")
    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    @JsonProperty("Salary")
    public double getSalary() {
        return Salary;
    }

    public void setSalary(double salary) {
        Salary = salary;
    }
}

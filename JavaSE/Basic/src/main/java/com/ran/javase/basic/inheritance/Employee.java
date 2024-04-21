package com.ran.javase.basic.inheritance;

/**
 * Employee
 *
 * @author rwei
 * @since 2023/11/7 14:00
 */
public class Employee {
    private String name;

    private int salary;

    public Employee(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}

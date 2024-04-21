package com.ran.javase.basic.inheritance;

/**
 * Main
 * 测试继承的特性，根据引用类型决定是调用父类还是子类方法
 * @author rwei
 * @since 2023/11/7 14:06
 */
public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager("manager", 100, 100);
        Employee employee1 = new Employee("employee1", 70);
        Employee employee2 = new Employee("employee2", 90);
        Employee[] employees = new Employee[3];
        employees[0] = manager;
        employees[1] = employee1;
        employees[2] = employee2;
        System.out.println(employees[0].getName());
        System.out.println(employees[0].getSalary());
        System.out.println(employees[1].getSalary());
        System.out.println(employees[2].getSalary());
    }
}

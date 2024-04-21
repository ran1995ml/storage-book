package com.ran.javase.basic.inheritance;

/**
 * Manager
 *
 * @author rwei
 * @since 2023/11/7 14:02
 */
public class Manager extends Employee {
    private int bonus;

    public Manager(String name, int salary, int bonus) {
        super(name, salary);
        this.bonus = bonus;
    }

    public int getSalary() {
        return super.getSalary() + this.bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}

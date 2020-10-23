package com.app.fueleco;

/**
 * <h1>Information collection of all car properties of the User.</h1>
 * Objects generation saving all the car properties.
 *
 * @author  Sotirios Nikas
 * @version 1.0
 * @since   2020-09-20
 */

public class UserCar {

    private int id;
    private String car;
    private double mass_num;
    private double c_num;
    private double a_num;
    private double efficiency_num;

    public UserCar() {
    }
    public UserCar(int id, String car, double mass_num, double c_num, double a_num, double efficiency_num) {
        this.id = id;
        this.car = car;
        this.mass_num = mass_num;
        this.c_num = c_num;
        this.a_num = a_num;
        this.efficiency_num = efficiency_num;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCar() {
        return car;
    }
    public void setCar(String car) {
        this.car = car;
    }

    public double getMass() {
        return mass_num;
    }
    public void setMass(double mass_num) {
        this.mass_num = mass_num;
    }

    public double getC() {
        return c_num;
    }
    public void setC(double c_num) {
        this.c_num = c_num;
    }

    public double getA() {
        return a_num;
    }
    public void setA(double a_num) {
        this.a_num = a_num;
    }

    public double get_efficiency() {
        return efficiency_num;
    }
    public void set_Efficiency(double efficiency_num) {
        this.efficiency_num = efficiency_num;
    }

    //Function for returning the object in string format (car name) inside the spinner
    public String toString(){
        return car;
    }
}

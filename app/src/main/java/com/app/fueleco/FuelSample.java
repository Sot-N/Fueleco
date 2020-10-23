package com.app.fueleco;

/**
 * <h1>Information collection related to the released energy from each type of fuel when it is burnt.</h1>
 * Objects generation saving the density of air for each temperature read in the csv file.
 *
 * @author  Sotirios Nikas
 * @version 1.0
 * @since   2020-09-20
 */

public class FuelSample {

    private String name;
    private double energy;

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public double getFuel() { return energy;}
    public void setFuel(double energy) {
        this.energy = energy;
    }
}
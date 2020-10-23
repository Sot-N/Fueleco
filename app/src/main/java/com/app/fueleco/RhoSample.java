package com.app.fueleco;

/**
 * <h1>Information collection related to air density.</h1>
 * Objects generation saving the density of air for each temperature read in the csv file.
 *
 * @author  Sotirios Nikas
 * @version 1.0
 * @since   2020-09-20
 */

public class RhoSample {

    private String name;
    private double rho;

    public String getName() { return name; }
    public void setName(String name){
        this.name = name;
    }

    public double getRho() {
        return rho;
    }
    public void setRho(double rho) {
        this.rho = rho;
    }
}

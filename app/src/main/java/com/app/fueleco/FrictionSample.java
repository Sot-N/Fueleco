package com.app.fueleco;

/**
 * <h1>Information collection related to road friction.</h1>
 * Objects generation saving the road friction for each type of road as read in the csv file.
 *
 * @author  Sotirios Nikas
 * @version 1.0
 * @since   2020-09-20
 */

public class FrictionSample {

    private String name;
    private double friction;

    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public double getFriction() {
        return friction;
    }
    public void setFriction(double friction) {
        this.friction = friction;
    }
}

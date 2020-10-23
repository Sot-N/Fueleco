package com.app.fueleco;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    //Unit test with following parameters:
    //mass = 1251kg
    //mu = 0.013 (asphalt)
    //Cd = 0.38
    //A = 1.77
    //d = 1000km
    //h = 1000m
    //petrol, 34200000, 2.31 CO2 per kg
    //rho = 1.2041(temperature = 20C)
    //load = 0kg
    //speed = 100km/h
    //price = 1.2cy


    private DisplayActivity DisplayTest;

    @Before
    public void setup() {
        DisplayTest = new DisplayActivity();
        System.out.println("Ready for testing!");
    }

    @After
    public void cleanup() {
        System.out.println("Done with unit test!");
    }

    @BeforeClass
    public static void testClassSetup() {
        System.out.println("Getting test class ready");
    }

    @AfterClass
    public static void testClassCleanup() {
        System.out.println("Done with tests");
    }

    @Test
    public void testWeight() {
        DisplayTest = new DisplayActivity();
        double weight = DisplayTest.weightCalc(9.8,1251);
        assertEquals("Weight is not calculated correctly", 12259.800000000001, weight, 0.1);
    }

    @Test
    public void testRollingForce() {
        DisplayTest = new DisplayActivity();
        double rolling_force = DisplayTest.forceRollCalc(12259.8,0.013);
        assertEquals("Rolling force is not calculated correctly", 159.3774, rolling_force, 0.1);
    }

    @Test
    public void testAirForce() {
        DisplayTest = new DisplayActivity();
        double air_force = DisplayTest.forceAirCalc(0.38, 1.2041, 1.77,27.77777777777778);
        assertEquals("Air force is not calculated correctly",312.45280092592594 , air_force, 0.1);
    }

    @Test
    public void testConversion() {
        DisplayTest = new DisplayActivity();
        double conversion = DisplayTest.convert(1000,27.77777777777778);
        assertEquals("The traveling time is not calculated correctly",   36000.0   , conversion, 0.1);
    }


    @Test
    public void testTotalPower() {
        DisplayTest = new DisplayActivity();
        double total_power = DisplayTest.powerCalc(312.45280092592594,159.3774, 27.77777777777778);
        assertEquals("The sum power of air and rolling forces are not calculated correctly",   13106.3944702   , total_power, 0.1);
    }

    @Test
    public void testLiftForce() {
        DisplayTest = new DisplayActivity();
        double lift_force = DisplayTest.energyLiftCalc(12259.800000000001, 1000, 1000, 27.77777777777778, 36000.0);
        assertEquals("The lift energy is not calculated correctly",1.2259800000000002E7
                , lift_force, 0.1);
    }


    @Test
    public void testTotalEnergy() {
        DisplayTest = new DisplayActivity();
        double total_energy = DisplayTest.energyCalc(13106.39447016461, 36000.0, 1.2259800000000002E7 );
        assertEquals("The total energy is not calculated correctly",4.8409000092592597E8

                , total_energy, 0.1);
    }

    @Test
    public void testEnergyPerLitre() {
        DisplayTest = new DisplayActivity();
        double energy_per_litre = DisplayTest.energyPerLitre(34200000, 0.15);
        assertEquals("The kinetic energy from one litre of fuel is not calculated correctly",5130000.0

                , energy_per_litre, 0.1);
    }

    @Test
    public void testFuel() {
        DisplayTest = new DisplayActivity();
        double fuel = DisplayTest.fuel(4.840900009272E8, 5130000.0);
        assertEquals("The fuel in litres is not calculated correctly",94.36452259789473
                , fuel, 0.1);
    }

    @Test
    public void testCost() {
        DisplayTest = new DisplayActivity();
        double cost = DisplayTest.Cost(94.36452259789473, 1.2);
        assertEquals("The cost in current currency is not calculated correctly",113.23742711747367
                , cost, 0.1);
    }

    @Test
    public void testCO2() {
        DisplayTest = new DisplayActivity();
        double co2 = DisplayTest.CO2(94.36452259789473, 2.31);
        assertEquals("The CO2 emission is not calculated correctly",217.98204720113685
                , co2, 0.1);
    }

}
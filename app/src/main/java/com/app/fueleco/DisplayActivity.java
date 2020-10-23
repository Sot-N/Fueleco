package com.app.fueleco;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.DialogInterface;
import android.widget.Toast;
import android.text.Html;
import android.graphics.Color;
import com.google.android.material.textfield.TextInputLayout;

/**
 * <h1>Display of result activity.</h1>
 * The car properties from previous activity are collected and used in this activity with
 * travel properties to calculate the final result (fuel).
 * @author  Sotirios Nikas
 * @version 1.0
 * @since   2020-09-20
 */

public class DisplayActivity extends AppCompatActivity {

    //Information coming from the main activity
    /*** Mass as given from Car properties activity. */
    double massNum;

    /*** Drag coefficient as given from Car properties activity. */
    double cNum;

    /*** Reference area as given from Car properties activity. */
    double aNum;

    /*** Fuel efficiency as given from Car properties activity. */
    double efficiencyNum;

    /*** Acceleration of gravity. */
    double gravity = 9.8;

    /*** Weight = mass * acceleration of gravity. */
    double weight;

    /*** Position of fuel spinner instead of using csv file. */
    int fuelposition = 0;

    /*** Rolling friction used for different road type. */
    double frictionNum;

    /*** Rho is the air density used for different temperatures. */
    double rhoNum;

    /*** The additional car load from passengers, and transferred material. */
    double carLoadNum;

    /*** The altitude difference from destination to departure of trip in m. */
    double heightNum;

    /*** The average speed in km/h. */
    double speedNum;

    /*** The covered distance in km. */
    double distanceNum;

    /*** The travelled time needed to cover this distance with the given average speed. */
    double travelTime;

    /*** The price per litre. */
    double priceNum;

    /*** Total force due to the rolling friction. */
    double forceRollNum;

    /*** Total force due to the air friction. */
    double forceAirNum;

    /*** The consumed power. */
    double powerNum;

    /*** The lifting energy. */
    double energyLiftingNum;

    /*** The total consumed energy. */
    double energyNum;

    /*** Energy converted to pure kinetic energy for the car from one litre of fuel. */
    double kineticEfficiencyNum;

    /*** The fuel energy used for different fuel type. */
    double fuelEnergyNum;

    /*** Total fuel of the trip. */
    double fuelNum;

    /*** Total cost of the trip. */
    double totalCostNum;

    /*** CO2 kg per litre. */
    double co2Num;

    /*** Total C02 kg of the trip. */
    double totalCO2Num;

    //Those are given in the textEntry
    TextInputLayout carload, height, speed, distance, price;

    //Button for calculating the fuel consumption
    Button Fuel;

    /*** Spinner for the fuel type selection. */
    Spinner FuelSpinner;

    /*** Spinner for the road type selection. */
    Spinner FrictionSpinner;

    /*** Spinner for the temperature selection. */
    Spinner RhoSpinner;

    //Printed Information from the csv files to the spinner
    /*** String array with size depending on the number of inputs saving the fuel type. */
    String[] mOptionsFuel = new String[3];

    /*** String array with size depending on the number of inputs saving the road type. */
    String[] mOptionsFriction = new String[2];

    /*** String array with size depending on the number of inputs saving the temperature. */
    String[] mOptionsRho = new String[14];

    //Corresponding parameters from the csv file are selected based on the spinner selection
    /*** Double array with size depending on the number of inputs saving the energy. */
    Double[] fuels = new Double[3];

    /*** Double array with size depending on the number of inputs saving the frictions. */
    Double[] frictions = new Double[2];

    /*** Double array with size depending on the number of inputs saving the density of air. */
    Double[] rhos = new Double[14];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Collect the information from the previous activity
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        massNum = extras.getDouble("mass",0.00);
        cNum = extras.getDouble("c",0.00);
        aNum = extras.getDouble("a",0.00);
        efficiencyNum = extras.getDouble("efficiency", 0.00);

        //Create a list of objects for saving the entries from CSV file
        List<FuelSample> fuelObjects;
        List<FrictionSample> frictionObjects;
        List<RhoSample> rhoObjects;

        //Read all the data
        fuelObjects = readFuelData();
        frictionObjects = readFrictionData();
        rhoObjects = readRhoData();

        int counterFuel = 0;
        int counterFriction = 0;
        int counterRho = 0;

        //Get the name and corresponding values in the list of rho objects
        for (FuelSample fuel : fuelObjects){
            mOptionsFuel[counterFuel] = fuel.getName();
            fuels[counterFuel] = fuel.getFuel();
            counterFuel++;
        }

        //Get the air density based on the temperature
        FuelSpinner = findViewById(R.id.fuel);

        //Creating the ArrayAdapter instance having the list of options
        final ArrayAdapter fuelAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mOptionsFuel);
        fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting the ArrayAdapter data on the Spinner
        FuelSpinner.setAdapter(fuelAdapter);

        //Select the air density based on the position corresponding temperature
        FuelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fuelEnergyNum = fuels[position];
                //Used for selecting the corresponding CO2 emissions
                fuelposition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Get the name and corresponding values in the list of friction objects
        for (FrictionSample friction : frictionObjects){
            mOptionsFriction[counterFriction] =  friction.getName();
            frictions[counterFriction] = friction.getFriction();
            counterFriction++;
        }

        //Get the friction based on the road type
        FrictionSpinner = findViewById(R.id.friction);

        //Creating the ArrayAdapter instance having the list of options
        ArrayAdapter frictionAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mOptionsFriction);
        frictionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting the ArrayAdapter data on the Spinner
        FrictionSpinner.setAdapter(frictionAdapter);

        //Select the rolling friction based on the position corresponding road type
        FrictionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                frictionNum = frictions[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Get the name and corresponding values in the list of rho objects
        for (RhoSample rho : rhoObjects){
            mOptionsRho[counterRho] = rho.getName();
            rhos[counterRho] = rho.getRho();
            counterRho++;
        }

        //Get the air density based on the temperature
        RhoSpinner = findViewById(R.id.rho);

        //Creating the ArrayAdapter instance having the list of options
        ArrayAdapter rhoAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, mOptionsRho);
        rhoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting the ArrayAdapter data on the Spinner
        RhoSpinner.setAdapter(rhoAdapter);

        //Select the air density based on the position corresponding temperature
        RhoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rhoNum = rhos[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Assigning the TextView from layout file to TextView variable
        carload = findViewById(R.id.carload);
        height = findViewById(R.id.height);
        speed = findViewById(R.id.speed);
        distance = findViewById(R.id.distance);
        price = findViewById(R.id.price);
        Fuel = findViewById(R.id.Fuel);

        //Button for calculating the fuel consumption
        Fuel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                //Check if the insertion of the data is successful
                if(TextUtils.isEmpty(Objects.requireNonNull(carload.getEditText()).getText().toString())) {
                    carload.setError("Please give the car load in kg coming from passengers and luggages.");
                    return;
                }else{
                    carload.setError(null);
                    carload.setErrorEnabled(false);
                }
                if(TextUtils.isEmpty(Objects.requireNonNull(height.getEditText()).getText().toString())) {
                    height.setError("Please give the altitude difference between departure and destination in meters.");
                    return;
                }else{
                    height.setError(null);
                    height.setErrorEnabled(false);
                }
                if(TextUtils.isEmpty(Objects.requireNonNull(speed.getEditText()).getText().toString())) {
                    speed.setError("Please give the average speed in km/h.");
                    return;
                }else{
                    speed.setError(null);
                    speed.setErrorEnabled(false);
                }
                if(TextUtils.isEmpty(Objects.requireNonNull(distance.getEditText()).getText().toString())) {
                    distance.setError("Please give the total distance in km of your trip.");
                    return;
                }else{
                    distance.setError(null);
                    distance.setErrorEnabled(false);
                }
                if(TextUtils.isEmpty(Objects.requireNonNull(price.getEditText()).getText().toString())) {
                    price.setError("Please give the price per litre.");
                    return;
                }else{
                    price.setError(null);
                    price.setErrorEnabled(false);
                }

                //Convert the necessary car related information (textview) from texts into numeric or string
                carLoadNum = Double.parseDouble(carload.getEditText().getText().toString());
                heightNum = Double.parseDouble(height.getEditText().getText().toString());
                speedNum = Double.parseDouble(speed.getEditText().getText().toString());
                distanceNum = Double.parseDouble(distance.getEditText().getText().toString());

                //Check if the distance is longer than height
                if(distanceNum < (heightNum/1000) ) {
                    distance.setError("Note, the altitude difference could not be longer than the covered distance.");
                    return;
                }else{
                    distance.setError(null);
                    distance.setErrorEnabled(false);
                }

                //Total Weight = gravity (9.8 m/s^2) * total mass (kg)
                double totalMassNum = massNum + carLoadNum;
                weight = weightCalc(gravity, totalMassNum);

                //Convert the speed from km/h to m/s for using it in calculations
                speedNum = speedNum/3.6;

                //Rolling resistance
                forceRollNum = forceRollCalc(weight, frictionNum);

                //Air resistance
                forceAirNum = forceAirCalc(cNum, rhoNum, aNum, speedNum);

                //Total power including Rolling and Air resistance
                powerNum = powerCalc(forceAirNum, forceRollNum, speedNum);

                //Traveling time, multiply by 1000 since the distance is given in km. The speed is m/s, therefore the travel time is in seconds
                travelTime = convert(distanceNum, speedNum);

                //Lifting energy (distance is given in km, so that I have to multiply by 1000 to convert it to m)
                energyLiftingNum = energyLiftCalc(weight, heightNum, distanceNum, speedNum, travelTime);

                //Total energy is the sum of rolling, air and lifting energy
                energyNum = energyCalc(powerNum, travelTime, energyLiftingNum);

                //Energy per litre converted to kinetic energy. 3.42 * 10^7 J (1 L of benzin) * efficiency (percentage goes to kinetic energy)
                kineticEfficiencyNum = energyPerLitre(efficiencyNum, fuelEnergyNum);

                //Divide the total energy by the energy released in one litre giving you the total amount of litres
                fuelNum = fuel(energyNum, kineticEfficiencyNum);

                //Petrol
                if (fuelposition == 0){
                    co2Num = 2.31;
                }
                //Diesel
                if (fuelposition == 1){
                    co2Num = 2.68;
                }
                //LPG
                if (fuelposition == 2){
                    co2Num = 1.51;
                }

                priceNum = Double.parseDouble(price.getEditText().getText().toString());

                ////totalCostNum = fuelNum * priceNum;
                totalCostNum = Cost(fuelNum, priceNum);

                //totalCO2Num = fuelNum * co2Num;
                totalCO2Num = CO2(fuelNum, co2Num);

                showConfirmationDialog(fuelNum, totalCostNum, totalCO2Num);
            }
        });
    }


    //Use functions instead for applying unit tests accordingly
    /**
     * <pre>weight = mass  * gravity</pre>
     */
    public double weightCalc(double g, double m){
        return g * m;
    }


    /**
     * <pre>Rolling force = weight * rolling friction(depends on road type)</pre>
     */
    public double forceRollCalc(double w, double mu){
        return w * mu;
    }


    /**
     ** <pre>Air resistance force = 1/2 * drag coefficient * density of air (depends on temperature) * A (frontal area) * speed^2</pre>
     */
    public double forceAirCalc(double Cd, double rho, double A, double u){
        return  0.5 * Cd * rho * A * Math.pow(u, 2);
    }


    /**
     * <pre>Power_total = (Air resistance force  + Rolling force) * speed</pre>
     */
    public double powerCalc(double Fair, double Froll, double u){
        return  (Fair + Froll) * u;
    }


    /**
     ** <pre>Travel_time = distance / speedNum</pre>
     */
    public double convert(double d, double u){
        return d * 1000/u;
    }


    /**
     * <pre>Lifting_energy = weight * (altitude/distance) * speed * Travel_time</pre>
     */
    public double energyLiftCalc(double w, double h, double d, double u, double t){
        return w * (h/(d * 1000)) * u * t;
    }


    /**
     * <pre>Total_energy = (Power_total * Travel_time) + Lifting_energy</pre>
     */
    public double energyCalc(double TotalPower, double t, double Elif){
        return (TotalPower * t) + Elif;

    }


    /**
     * <pre>Released_energy_per_litre = 3.5 * 10^7 J (1 L of benzin)</pre>
     * <pre>Kinetic_energy_per_litre = Released_energy_per_litre * efficiency</pre>
     */
    public double energyPerLitre(double f, double Efuel){
        return f * Efuel;
    }


    /**
     * <pre>Fuel_in_litres = Total_energy / Kinetic_energy_per_litre</pre>
     */
    public double fuel(double E, double Elitre){
        return E/Elitre;
    }


    /**
     * <pre>Cost = Fuel_in_litres * cost_per_litre</pre>
     */
    public double Cost(double fuel, double price){
        return fuel * price;
    }


    /**
     * <pre>CO2 emmissions = Fuel_in_litres * C02_emmissions_per_litre</pre>
     */
    public double CO2(double fuel, double CO2Litre){
        return fuel * CO2Litre;
    }


    /**
     * <pre>Use common menu for share and help options</pre>
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.commonmenu, menu);
        return true;
    }


    /**
     * <pre>Depending on selected option, choose share or help option</pre>
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HelpActivity.class));
                return true;

            case R.id.share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                // Share provider of the link in google store
                ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
                //create the sharing intent
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                //then set the sharingIntent
                mShareActionProvider.setShareIntent(sharingIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * <pre>Create alert dialog for showing the final output with fuel, cost and CO2 emissions</pre>
     */
    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showConfirmationDialog(double fuelNum, double totalCostNum, double totalCO2Num) {

        //Create the string printed in the Items form. The result is printed in bold and big letters. For CO2 a sub is used
        //Keep only two decimals for each printed output without rounding
        @SuppressLint("DefaultLocale") String fuel_lt = "<br/>" + "Fuel: <b>" + String.format("%.2f", fuelNum) + "</b> litres.<br/><br/>";
        @SuppressLint("DefaultLocale") String cost_cy = "Cost: <b>" + String.format("%.2f", totalCostNum) + "</b> cy.<br/><br/>";
        @SuppressLint("DefaultLocale") String co2_kg = "CO<sub><small>2</small></sub>: <b>" + String.format("%.2f", totalCO2Num) + "</b> kg. <br/><br/>";
        String items = "<big>" + fuel_lt + cost_cy + co2_kg + "<big>";

        TextView textView = new TextView(DisplayActivity.this);
        //Place the icon in the bottom so that be in the center
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.mipmap.app_result);
        textView.setPadding(20, 5, 20, 5);
        textView.setBackgroundColor(getResources().getColor(R.color.dialogTitle));
        textView.setTextColor(Color.WHITE);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DisplayActivity.this);
                builder.setCustomTitle(textView);
                builder.setTitle("Result");
                builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog, null));
                builder.setItems(new Spanned[]{Html.fromHtml(items)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(DisplayActivity.this, "Thanks", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
    }


    /**
     * <pre>Create the list including the fuel information (name and value) from csv</pre>
     */
    private List<FuelSample> readFuelData(){
        //Here it is the csv file (fuel)
        InputStream in = getResources().openRawResource(R.raw.fuel);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)
        );

        List<FuelSample> fuelSamples = new ArrayList<>();


        String line = "";
        try {
            reader.readLine();
            while ( (line = reader.readLine()) != null) {
                Log.d("My activity", "Line: " + line);
                String[] tokens = line.split(",");

                FuelSample sample = new FuelSample();
                sample.setName(tokens[0]);

                if (tokens[1].length() > 0){
                    sample.setFuel(Double.parseDouble(tokens[1]));
                } else{
                    sample.setFuel(0);
                }

                fuelSamples.add(sample);
                Log.d("My activity", "Just created: " + sample);

            }
        } catch (IOException e){
            Log.v("My activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
        return fuelSamples;
    }


    /**
     * <pre>Create the list including the rolling friction information (name and value) from csv</pre>
     */
    private List<FrictionSample> readFrictionData(){
        InputStream in = getResources().openRawResource(R.raw.friction);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)
        );

        List<FrictionSample> frictionSamples = new ArrayList<>();


        String line = "";
        try {
            reader.readLine();
            while ( (line = reader.readLine()) != null) {
                Log.d("My activity", "Line: " + line);
                String[] tokens = line.split(",");

                FrictionSample sample = new FrictionSample();
                sample.setName(tokens[0]);

                if (tokens[1].length() > 0){
                    sample.setFriction(Double.parseDouble(tokens[1]));
                } else{
                    sample.setFriction(0);
                }

                frictionSamples.add(sample);
                Log.d("My activity", "Just created: " + sample);

            }
        } catch (IOException e){
            Log.v("My activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
        return frictionSamples;
    }


    /**
     * <pre>Create the list including the air density information (name and value) from csv</pre>
     */
    private List<RhoSample> readRhoData(){
        InputStream in = getResources().openRawResource(R.raw.rho);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8)
        );

        List<RhoSample> rhoSamples = new ArrayList<>();


        String line = "";
        try {
            reader.readLine();
            while ( (line = reader.readLine()) != null) {
                Log.d("My activity", "Line: " + line);
                String[] tokens = line.split(",");

                RhoSample sample = new RhoSample();
                sample.setName(tokens[0]);

                if (tokens[1].length() > 0){
                    sample.setRho(Double.parseDouble(tokens[1]));
                } else{
                    sample.setRho(0);
                }

                rhoSamples.add(sample);
                Log.d("My activity", "Just created: " + sample);

            }
        } catch (IOException e){
            Log.v("My activity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
        return rhoSamples;
    }
}
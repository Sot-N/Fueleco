package com.app.fueleco;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.text.Html;
import android.text.TextUtils;
import java.lang.*;
import java.util.Objects;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.database.Cursor;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputLayout;

/**
 * <h1>Car properties activity.</h1>
 * In this activity, the user inserts the information related to its car.
 * Information such as the car name, fuel efficiency, car weight, drug coefficient and frontal area.
 * After filling and adding the car properties, then it can be selected from the selection list for further processing.
 *
 * @author  Sotirios Nikas
 * @version 1.0
 * @since   2020-09-20
 */

public class MainActivity extends AppCompatActivity {

    /*** Definition of sqlite database object in global scope. */
    private DB userCarStorage;

    /*** Button for adding a new car of the User into the database after all the necessary information have been added. */
    Button btnAddUserCar;

    /*** Button for viewing the information of a new car of the User. */
    Button btwViewUserCar;

    /*** Button for deleting the information of the selected car of the User. */
    Button btwDeleteUserCar;

    //Those are given in the textEntry
    TextInputLayout carName, mass, Cd, A, fuelEfficiency;

    /*** ID used in the creation of each object inside the database.*/
    int etID;

    /*** Array to keep the ids. */
    Integer IDarray[] = new Integer[3];

    //Two different strings are needed. The carNameStr serves for adding a car into database
    /*** The carNameStr serves for adding a car into database. */
    String carNameStr;

    /*** Mass of car in kg. */
    double massNum;

    /*** Drag coefficient of the car (dimensionless quantity). */
    double cNum;

    /*** Reference area (cross-sectional area) (m^2). */
    double aNum;

    /*** Fuel efficiency [%]. */
    double effNum;

    /*** The spinner for the selection of the car for either further processing or deletion. */
    Spinner carSpinner;

    /*** List of cars (objects), this is changed afterwards depending on the selection. */
    List<UserCar> cars = new ArrayList<>();

    /*** Adapter converts an ArrayList of objects into View items. */
    ArrayAdapter<UserCar> adapter;

    /***
     * It is defined globally so that can be changed from the spinner internally and selected either for deletion or for further processing.
     */
    UserCar finalUserCar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning the information from the text entry
        carName = findViewById(R.id.car);
        mass = findViewById(R.id.weight);
        fuelEfficiency = findViewById(R.id.efficiency);
        Cd = findViewById(R.id.drug);
        A = findViewById(R.id.area);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Assigning the button functionality (add,view,delete)
        btnAddUserCar = findViewById(R.id.btnAddUser);
        btwViewUserCar = findViewById(R.id.btwViewUser);
        btwDeleteUserCar = findViewById(R.id.btwDeleteUser);

        //Spinner used for car selection
        carSpinner = findViewById(R.id.carSpinner);

        //Create a database to store the cars
        userCarStorage = new DB(this);

        //Function for adding the new car
        AddData();

        //Function for viewing the datta
        ViewData();

        //Function for deleting the selected car
        DeleteData();

        //Function for the spinner selection
        prepareData();

        //Continue to the next activity, after typing continue
        //Button for the continuing to the next activity, after you have filled all necessary info.
        Button nextAct;
        nextAct = findViewById(R.id.button);
        nextAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });
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
     * Spinner selection of the car object
     */
    public void prepareData()
    {
        //It is listing all the car objects from the sqlite database
        cars=userCarStorage.allCars();

        //It takes all the cars objects into the adapter
        adapter=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,android.R.id.text1,cars);

        //Specify the layout (simple_spinner_dropdown_item) to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Sets the data behind this ListView/Spinner
        carSpinner.setAdapter(adapter);

        //Register a callback to be invoked when an item in this AdapterView has been selected.
        carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Define the finalUser by selecting it inside the spinner
                finalUserCar = (UserCar) parent.getSelectedItem();
                //displayUserData(finalUserCar);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /**
     * Function for displaying the selected data (possibly remove afterwards)
     */
    private void displayUserData(UserCar user) {
        String name = user.getCar();
        double disp_eff = user.get_efficiency();
        double disp_weight = user.getMass();
        double disp_drag = user.getC();
        double disp_frontArea = user.getA();
        String userData = "Name: " + name + "\nEfficiency: " + disp_eff + "\nWeight: " + disp_weight + "\nFrontal area: " + disp_frontArea + "\nDrag coefficient: " + disp_drag ;
        Toast.makeText(this, userData, Toast.LENGTH_LONG).show();
    }


    /**
     * Function for adding the selected car object into database
     */
    public void AddData() {
        btnAddUserCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //This checks if no name for the car is given and when user inserts empty spaces (problematic)
                if(carName.getEditText().getText().toString() == null || TextUtils.isEmpty(carName.getEditText().getText().toString().trim())){
                    carName.setError("Please give the car name.");
                    return;
                }else{
                    carName.setError(null);
                    carName.setErrorEnabled(false);
                }
                if(TextUtils.isEmpty(Objects.requireNonNull(fuelEfficiency.getEditText()).getText().toString())) {
                    fuelEfficiency.setError("Please give the fuel efficiency. Typical values are about 15 and 30%.");
                    return;
                }else{
                    fuelEfficiency.setError(null);
                    fuelEfficiency.setErrorEnabled(false);
                }
                if(TextUtils.isEmpty(Objects.requireNonNull(mass.getEditText()).getText().toString())) {
                    mass.setError("Please give the car weight in kg.");
                    return;
                }else{
                    mass.setError(null);
                    mass.setErrorEnabled(false);
                }
                if(TextUtils.isEmpty(Objects.requireNonNull(Cd.getEditText()).getText().toString())) {
                    Cd.setError("Please give the drag coefficient. Typical values are about 0.3 and 0.8");
                    return;
                }else{
                    Cd.setError(null);
                    Cd.setErrorEnabled(false);
                }
                if(TextUtils.isEmpty(Objects.requireNonNull(A.getEditText()).getText().toString())) {
                    A.setError(Html.fromHtml("Please give the car frontal area in m<sup><small>2</small></sup>."));
                    return;
                }else{
                    A.setError(null);
                    A.setErrorEnabled(false);
                }

                //Convert the necessary car related information (textview) from texts into numeric or string
                carNameStr = carName.getEditText().getText().toString();
                massNum = Double.parseDouble(mass.getEditText().getText().toString());
                cNum = Double.parseDouble(Cd.getEditText().getText().toString());
                aNum = Double.parseDouble(A.getEditText().getText().toString());
                effNum = Double.parseDouble(fuelEfficiency.getEditText().getText().toString());

                //Collect the buffer of the database for checking the number of objects
                Cursor data = userCarStorage.showData();

                //Up to 3 objects is allowed per user
                if (data.getCount() > 2) {
                    display("Error", "No more cars can be added");
                }else {
                    //Use available ID (0,1,2). Alternative solution is also autoincrement in database
                    etID = getAvailID();
                    //Create a new object having the information as given by the user.
                    UserCar test = new UserCar(etID, carNameStr, massNum, cNum, aNum, effNum);
                    //Add this user into the database
                    boolean insertData = userCarStorage.addUser(test);
                    //Add this car into the spinner
                    adapter.add(test);
                    //Notifies the attached observers that the underlying data has been changed and any View representing the data set should refresh itself
                    adapter.notifyDataSetChanged();

                    //Check if the insertion of the data into database is successful
                    if (insertData) {
                        Toast.makeText(MainActivity.this, "Data successfully inserted.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    /**
     * Function for returning an ID value from the IDarray which is not used, alternatively autoincrement in database can be used
     */
    public int getAvailID(){
        int id = 0;
        for (int i = 0; i < 3; i++){
            if(IDarray[i] == null){
                ///Fill the returned value
                id = i;
                //Fill the id into the array so that can be deleted afterwards
                IDarray[i] = i;
                break;
            }
        }
        return id;
    }


    /**
     * Function for enabling the view button for allowing user to see the inserted data
     */
    public void ViewData() {
        btwViewUserCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor data = userCarStorage.showData();

                if (data.getCount() == 0) {
                    display("Error", "No data found, please fill the car properties first.");
                    return;
                }

                StringBuilder buffer = new StringBuilder();
                while (data.moveToNext()) {
                    //buffer.append("ID: ").append(data.getString(0)).append("\n");
                    buffer.append("Car name: ").append(data.getString(1)).append("\n");
                    buffer.append("Weight: ").append(data.getString(2)).append(" kg \n");
                    buffer.append("Drug coefficient: ").append(data.getString(3)).append("\n");
                    buffer.append("Frontal area: ").append(data.getString(4)).append(" m^2 \n");
                    buffer.append("Fuel efficiency: ").append(data.getString(5)).append(" % \n\n");
                    display("Stored Cars:", buffer.toString());
                }
            }
        });
    }



    /**
     * Function to display the errors (title, message)
     */
    public void display (String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    /**
     * Function for deleting the selected car object into database
     */
    public void DeleteData(){
        btwDeleteUserCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cursor data = userCarStorage.showData();

                if (data.getCount() == 0) {
                    display("Error", "No data found to delete.");
                    return;
                }

                //Delete id from array allowing to be available for the next object
                IDarray[finalUserCar.getId()] = null;

                //This is the length of the car name
                int temp = finalUserCar.getCar().length();
                //Checks if the length of the car name is non zero
                if(temp > 0){
                    //Delete a row from the database, given the car name and its id
                    Integer deleteRow = userCarStorage.deleteData(finalUserCar.getCar(), finalUserCar.getId());
                    //Remove the selected user object from the spinner
                    adapter.remove(finalUserCar);
                    //Notifies the attached observers that the underlying data has been changed and any View representing the data set should refresh
                    adapter.notifyDataSetChanged();
                    //displayUserData(finalUserCar);
                    //This is necessary for allowing to change the finalUserCar to the next car
                    prepareData();
                    if(deleteRow > 0){
                        Toast.makeText(MainActivity.this, "Data successfully deleted.", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "You must enter an ID to delete", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * Function for connecting this activity with the next one
     */
    public void openActivity2() {

        Cursor data = userCarStorage.showData();

        if (data.getCount() == 0) {
            display("Error", "No data found, fill the car properties first and then click the button 'Continue' to fill the trip properties to the next page.");
            return;
        }
        //It is the message that is passed between components such as activities, content etc
        Intent intent = new Intent(this, DisplayActivity.class);
        //Add extended data to the intent. Information to be used for the next activity
        intent.putExtra("mass", finalUserCar.getMass());
        intent.putExtra("c", finalUserCar.getC());
        intent.putExtra("a", finalUserCar.getA());
        intent.putExtra("efficiency", finalUserCar.get_efficiency()/100);

        //Launch a new activity
        startActivity(intent);
    }
}


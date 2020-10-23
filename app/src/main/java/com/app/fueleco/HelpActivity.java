package com.app.fueleco;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView text;
        text = findViewById(R.id.helpline);

        //Create the string printed in the Items form. The result is printed in bold, big and subscript letters.
        String help = "<b>Fuel efficiency</b> is the percentage of the released energy from the fuel converted into kinetic energy. Typical values are about 15%-30%." +
                "<br/><br/><b>Car weight</b> is considered the curb weight." +
                "<br/><br/>The <b>Drag coefficient</b> is associated with the car aerodynamic design. Typical values are about 0.3-0.8." +
                "<br/><br/>The <b>Frontal area</b> is the frontal projection of the car's area." +
                "<br/><br/>The <b>type of fuel</b> determines the <b>energy per litre</b> and <b>CO<sub><small>2</small></sub> emissions</b> are used in the calculations." +
                "<br/><br/>The <b>type of road</b> determines the rolling friction coefficient is used in the calculations." +
                "<br/><br/>The <b>temperature</b> determines the air density is used in the calculations." +
                "<br/><br/>The <b>altitude</b> difference between departure and destination is inserted. Only positive values are considered." +
                "<br/><br/>The <b>average speed</b> is inserted into the calculations. No accelerations are considered." +
                "<br/><br/>The <b>cy</b> is used to represent different currencies." +
                "<br/><br/>CO<sub><small>2</small></sub> emissions per litre for <b>diesel, petrol and lpg</b> are 2.68, 2.31 and 1.51 kg.";

        text.setText(Html.fromHtml(help));
        text.setTextColor(Color.BLACK);
    }
}
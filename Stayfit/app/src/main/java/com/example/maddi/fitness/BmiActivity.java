package com.example.maddi.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BmiActivity extends AppCompatActivity {
    EditText number1;
    EditText number2;
    Button Add_button;
    TextView result;

    int ans=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        Intent intent = getIntent();

        // by ID we can use each component which id is assign in xml file
        number1=(EditText) findViewById(R.id.editText);
        number2=(EditText) findViewById(R.id.editText2);
        Add_button=(Button) findViewById(R.id.button4);
        result = (TextView) findViewById(R.id.textView10);

        // Add_button add clicklistener
        Add_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // num1 or num2 double type
                // get data which is in edittext, convert it to string
                // using parse Double convert it to Double type
                double num1 = Double.parseDouble(number1.getText().toString());
                double num2 = Double.parseDouble(number2.getText().toString());
                // add both number and store it to sum
                double bmi = num1 / num2;
                // set it ot result textview
                result.setText(Double.toString(bmi));
            }
        });
    }





}

package com.example.foodcounter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView angka;
    private Button btnFries, btnSoup, btnBeef, btnNdl, reset;

    private int totalPrice, cfries, csoup, cbeef, cndl = 0;

    private int fries = 20000;
    private int soup = 12000;
    private int beef = 50000;
    private int noodle= 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        angka = findViewById(R.id.angka);

        btnFries = findViewById(R.id.btnFries);
        btnFries.setOnClickListener(this);

        btnSoup = findViewById(R.id.btnSoup);
        btnSoup.setOnClickListener(this);

        btnBeef = findViewById(R.id.btnBeef);
        btnBeef.setOnClickListener(this);

        btnNdl = findViewById(R.id.btnNdl);
        btnNdl.setOnClickListener(this);

        reset = findViewById(R.id.reset);
        reset.setOnClickListener(this);




    }

    @Override
    public void onClick(View view) {
        if (view == btnFries) {
            cfries++;
            btnFries.setText("Fries (" + cfries + ")");

            totalPrice += fries;
            angka.setText(String.valueOf(totalPrice));
        }  else if (view == btnSoup) {
            csoup++;
            btnSoup.setText("Soup (" + csoup + ")");

            totalPrice += soup;
            angka.setText(String.valueOf(totalPrice));
        } else if (view == btnBeef) {
            cbeef++;
            btnBeef.setText("Beef (" + cbeef + ")");

            totalPrice += beef;
            angka.setText(String.valueOf(totalPrice));
        } else if (view == btnNdl) {
            cndl++;
            btnNdl.setText("Noodle (" + cndl + ")");

            totalPrice += noodle;
            angka.setText(String.valueOf(totalPrice));
        } else if (view == reset) {
            cfries = 0;
            csoup = 0;
            cbeef = 0;
            cndl = 0;
            totalPrice = 0;
            angka.setText("Total (" + totalPrice + ")");
            btnFries.setText("Fries (" + cfries + ")");
            btnSoup.setText("Soup (" + csoup + ")");
            btnBeef.setText("Beef (" + cbeef + ")");
            btnNdl.setText("Noodle (" + cndl + ")");
        }
    }
}
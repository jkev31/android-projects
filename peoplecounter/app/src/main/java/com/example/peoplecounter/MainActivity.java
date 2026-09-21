package com.example.peoplecounter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView txtTotal;
    private PeopleControl peopleCtrl;
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

        Button buttonL=findViewById(R.id.btnL);
        Button buttonP=findViewById(R.id.btnP);
        Button buttonR=findViewById(R.id.btnReset);
        peopleCtrl=new PeopleControl();
        buttonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peopleCtrl.add(true);
                updateView();
            }
        });

        buttonP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peopleCtrl.add(true);
                updateView();
            }
        });

        buttonR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peopleCtrl.add(true);
                updateView();
            }
        });
    }

    private void updateView(){
        txtTotal.setText(String.format("%d" , peopleCtrl.getTotal()));
    }


}
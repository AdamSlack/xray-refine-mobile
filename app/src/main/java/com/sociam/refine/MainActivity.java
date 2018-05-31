package com.sociam.refine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button viewAppBtn = (Button) findViewById(R.id.viewAppsBtn);

        viewAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findOwnApps = new Intent(getApplicationContext(), ListApplications.class);
                startActivity(findOwnApps);
            }
        });
    }

}

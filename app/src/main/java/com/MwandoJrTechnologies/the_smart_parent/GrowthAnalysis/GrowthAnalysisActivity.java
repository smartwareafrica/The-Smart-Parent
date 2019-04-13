package com.MwandoJrTechnologies.the_smart_parent.GrowthAnalysis;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.MwandoJrTechnologies.the_smart_parent.NewsFeed.MainActivity;
import com.MwandoJrTechnologies.the_smart_parent.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class GrowthAnalysisActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private FloatingActionButton fabDate;
    private TextView dateOfBirth;
    private TextView childAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_analysis);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);  //for the back button
        getSupportActionBar().setTitle("Monitor growth");

        fabDate = findViewById(R.id.g_analysis_fab);
        dateOfBirth = findViewById(R.id.g_analysis_birth_date);
        childAge = findViewById(R.id.g_analysis_age_display);


        fabDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int calendarYear = calendar.get(Calendar.YEAR);
            int calendarMonth = calendar.get(Calendar.MONTH);
            int calendarDay = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), datePickerListener, calendarYear, calendarMonth, calendarDay);
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        });

    }

    private DatePickerDialog.OnDateSetListener datePickerListener = (view, year, month, dayOfMonth) -> {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String format = new SimpleDateFormat("EEE, MMM d, ''yyyy").format(calendar.getTime());
        dateOfBirth.setText(format);
        childAge.setText(Integer.toString(calculateAge(calendar.getTimeInMillis())));
    };

    private int calculateAge(long timeInMillis) {

        Calendar dob = Calendar.getInstance();
        dob.setTimeInMillis(timeInMillis);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_MONTH)<dob.get(Calendar.DAY_OF_MONTH)){
            age--;
        }
        return age;
    }


    //activate back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    //open main activity
    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(GrowthAnalysisActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}

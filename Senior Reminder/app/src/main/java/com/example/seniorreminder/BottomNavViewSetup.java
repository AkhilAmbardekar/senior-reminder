package com.example.seniorreminder;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Class for creating bottom navigation view in activities
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.2
 */
public class BottomNavViewSetup {

    /**
     * default no-parameter constructor
     */
    public BottomNavViewSetup() { }

    /**
     * Sets up bottom navigation view given bottom navigation view
     *
     * @param bottomNavigationView bottomNavigationView xml file
     * @param context application context (activity)
     */
    public void setupBottomNavViewSetup(BottomNavigationView bottomNavigationView,
                                        final Context context){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            /**
             * navigates to activity depending on what is selected in navigation view
             */
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeMenu:
                        Toast.makeText(context, "Home", Toast.LENGTH_SHORT).show();
                        Intent startIntent = new Intent(context, HomeActivity.class);
                        context.startActivity(startIntent);
                        return true;
                    case R.id.medicationTrackingMenu:
                        Toast.makeText(context, "Medication Tracking", Toast.LENGTH_SHORT).show();
                        startIntent = new Intent(context, MedTrackingActivity.class);
                        context.startActivity(startIntent);
                        return true;
                    case R.id.tasksMenu:
                        Toast.makeText(context, "Tasks", Toast.LENGTH_SHORT).show();
                        startIntent = new Intent(context, TasksActivity.class);
                        context.startActivity(startIntent);
                        return true;
                    case R.id.fluShotMenu:
                        Toast.makeText(context, "Flu Shot", Toast.LENGTH_SHORT).show();
                        startIntent = new Intent(context, FluShotActivity.class);
                        context.startActivity(startIntent);
                        return true;
                }
                return false;
            }
        });

    }
}

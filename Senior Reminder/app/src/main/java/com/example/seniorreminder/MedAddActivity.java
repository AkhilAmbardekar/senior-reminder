package com.example.seniorreminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

/**
 * Login activity for senior reminder app
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 3.8
 */
public class MedAddActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "MedAddActivity";

    private EditText medNameET;
    private EditText medBrandET;
    private EditText extra1ET;
    private EditText extra2ET;
    private EditText medDosageET;
    private EditText medCountET;

    private TextView medTypeTV;
    private TextView dosageUnitTV;

    private Button submitBTN;
    private Button timeBTN;
    private Button startDateBTN;

    private NotificationManagerCompat notificationManager;

    // used to save year, month, day, hour, minute from datepicker and timepicker dialogs

    public int mHour;
    public int mMin;

    public int mYear;
    public int mMonth;
    public int mDay;


    /**
     * generates activity layout and buttons
     * allows user to enter information and have it saved into
     * firebase database (as a Medication object)
     * that is later retrieved in the MedTrackingListActivity.java
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupMedAddActivity();

        dosageUnitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * creates popup menu when user clicks on dosageunit textview
             */
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MedAddActivity.this, v);
                popupMenu.setOnMenuItemClickListener(MedAddActivity.this);
                popupMenu.inflate(R.menu.dosageunit_menu);
                popupMenu.show();
            }
        });

        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * if all fields are filled, initializes FirebaseAuth and FirebaseFirestore
             * to create document in users collection with users UID, and then adds
             * Medication object to the user's specific document
             */
            public void onClick(View v) {
                // need to update notEmpty method notEmpty() ==
                if (notEmpty() == Boolean.TRUE){

                    String name = medNameET.getText().toString();

                    String brand = medBrandET.getText().toString();
                    String dosageUnit = dosageUnitTV.getText().toString();
                    String type = medTypeTV.getText().toString();

                    Double countDub = Double.parseDouble(medCountET.getText().toString());

                    int count = (int) Math.round(countDub);

                    float countCurrent = count;
                    float dosage = Float.parseFloat(medDosageET.getText().toString());

                    if (type.equals(getString(R.string.antiobioticTitle))) {
                        String ingestionType = extra1ET.getText().toString();
                        int expiry = Integer.parseInt(extra2ET.getText().toString());

                        Antibiotics newAntibioticMed = new Antibiotics(name, brand, type, dosage, dosageUnit, count,
                                countCurrent, mYear, mMonth, mDay, mHour, mMin, ingestionType,
                        expiry, 0, true);
                        newAntibioticMed.addMedDatabase(getApplicationContext());
                    } else if(type.equals(getString(R.string.diabeticTitle))){
                        int critNum = Integer.parseInt(extra1ET.getText().toString());
                        DiabeticMed newDiabeticMed = new DiabeticMed(name, brand, type, dosage, dosageUnit, count,
                                countCurrent, mYear, mMonth, mDay, mHour, mMin, critNum, true);
                        newDiabeticMed.addMedDatabase(getApplicationContext());
                    } else if(type.equals(getString(R.string.heartTitle))){
                        int critNum = Integer.parseInt(extra1ET.getText().toString());
                        String importance = extra2ET.getText().toString();
                        HeartMed newHeartMed = new HeartMed(name, brand, type, dosage, dosageUnit, count,
                                countCurrent, mYear, mMonth, mDay, mHour, mMin, critNum, importance, true);
                        newHeartMed.addMedDatabase(getApplicationContext());
                    } else if (type.equals(getString(R.string.otherMedTitle))){

                        OtherMed newOtherMed = new OtherMed(name, brand, type, dosage, dosageUnit, count,
                                countCurrent, mYear, mMonth, mDay, mHour, mMin, true);
                        newOtherMed.addMedDatabase(getApplicationContext());

                    }

                    Intent intent = new Intent(getApplicationContext(), MedTrackingActivity.class);
                    intent.putExtra("com.example.seniorreminder.editedMed", name);
                    startActivity(intent);

                }
            }
        });

        BottomNavViewSetup bottomNavViewSetup = new BottomNavViewSetup();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavViewSetup.setupBottomNavViewSetup(bottomNavigationView, MedAddActivity.this);

        // maybe change this to a text view and have it display the time
        timeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * opens timepicker fragment for user to choose a time
             */
            public void onClick(View v) {
                DialogFragment timePickerFrag = new TimePickerFragment();
                timePickerFrag.show(getSupportFragmentManager(), "Choose time");
            }
        });


        startDateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             * displays datepicker dialog
             */
            public void onClick(View v) {
                showDatePicker();
            }
        });

        /* figure out how to implement an end date
        endDateBTN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePicker();
                int year = Integer.parseInt(yearTV.getText().toString());
                int month = Integer.parseInt(monthTV.getText().toString());
                int dayOfMonth = Integer.parseInt(dayTV.getText().toString());

                // look into using calendar/date class
                ArrayList<Integer> endDate = new ArrayList<>();
                endDate.add(year);
                endDate.add(month);
                endDate.add(dayOfMonth);
            }
        });
        */

    }

    /**
     * setups MedAddActivity with extra edittexts depending on what medication type user is adding
     */
    private void setupMedAddActivity(){

        setContentView(R.layout.activity_med_add);

        medNameET = (EditText)findViewById(R.id.medNameET);
        medBrandET = (EditText)findViewById(R.id.medBrandET);
        medDosageET = (EditText)findViewById(R.id.medDosageET);
        medCountET = (EditText)findViewById(R.id.medCountET);
        extra1ET = (EditText)findViewById(R.id.extra1ET);
        extra2ET = (EditText)findViewById(R.id.extra2ET);

        dosageUnitTV = (TextView)findViewById(R.id.dosageUnitTV);

        submitBTN = (Button)findViewById(R.id.submitBTN);
        timeBTN = (Button)findViewById(R.id.timeBTN);
        startDateBTN = (Button)findViewById(R.id.startDateBTN);

        medTypeTV = (TextView)findViewById(R.id.typeTV);

        String selectedMedType = "";

        if (getIntent().hasExtra("com.example.seniorreminder.selectedMed")) {

            Medication medToEdit = getIntent().getParcelableExtra("com.example.seniorreminder.selectedMed");

            medNameET.setText(medToEdit.getName());
            medBrandET.setText(medToEdit.getBrand());
            medDosageET.setText(Float.toString(medToEdit.getDosageAmt()));
            medCountET.setText(Float.toString(medToEdit.getCountCurrent()));
            dosageUnitTV.setText(medToEdit.getDosageUnit());

            timeBTN.setText(medToEdit.getHour() + "h " + medToEdit.getMinute() + "m");
            mHour = medToEdit.getHour();
            mMin = medToEdit.getMinute();

            startDateBTN.setText(medToEdit.getYear() + "/" + (medToEdit.getMonth() + 1) + "/" + medToEdit.getDay());
            mYear = medToEdit.getYear();
            mMonth = medToEdit.getMonth();
            mDay = medToEdit.getDay();

            selectedMedType = medToEdit.getType();

        }


        if (getIntent().hasExtra("com.example.seniorreminder.medtype")) {

            selectedMedType = getIntent().getExtras().getString("com.example.seniorreminder.medtype");

        }

        if(selectedMedType.equals(getString(R.string.antiobioticTitle))) {
            medTypeTV.setText(getString(R.string.antiobioticTitle));
            extra1ET.setVisibility(View.VISIBLE);
            extra1ET.setHint(getString(R.string.ingestionTypeText));
            extra2ET.setVisibility(View.VISIBLE);
            extra2ET.setHint(getString(R.string.expiryText));
        } else if(selectedMedType.equals(getString(R.string.diabeticTitle))){
            medTypeTV.setText(getString(R.string.diabeticTitle));
            extra1ET.setVisibility(View.VISIBLE);
            extra1ET.setHint(getString(R.string.critNumText));
        } else if(selectedMedType.equals(getString(R.string.heartTitle))){
            medTypeTV.setText(getString(R.string.heartTitle));
            extra1ET.setVisibility(View.VISIBLE);
            extra1ET.setHint(getString(R.string.critNumText));
            extra2ET.setVisibility(View.VISIBLE);
            extra2ET.setHint(getString(R.string.importanceText));
        } else if(selectedMedType.equals(getString(R.string.otherMedTitle))){
            medTypeTV.setText(getString(R.string.otherMedTitle));
        }

    }

    /**
     * Called when the user is done setting a new time and the dialog has
     * closed.
     *
     * @param view      the view associated with this listener
     * @param hourOfDay the hour that was set
     * @param minute    the minute that was set
     */
    @Override
    // use this to pass time to the function
    // maybe change fragment to make more in line with datepicker
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Toast.makeText(MedAddActivity.this, "Time picked is: " + hourOfDay + "h and " + minute, Toast.LENGTH_SHORT).show();
        timeBTN.setText(hourOfDay + "h " + minute + "m");

        mHour = hourOfDay;
        mMin = minute;

    }

    /**
     * @param view       the picker associated with the dialog
     * @param year       the selected year
     * @param month      the selected month (0-11 for compatibility with
 *                   {@link Calendar#MONTH})
     * @param dayOfMonth the selected day of the month (1-31, depending on
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Toast.makeText(MedAddActivity.this, "Date picked is: " + year + "/" + month + "/" + dayOfMonth, Toast.LENGTH_SHORT).show();
        startDateBTN.setText(year + "/" + (month + 1) + "/" + dayOfMonth);

        mYear = year;
        mMonth = month;
        mDay = dayOfMonth;
        /*
         i want to return this array list right here
        ArrayList<Integer> endDate = new ArrayList<>();
        endDate.add(year);
        endDate.add(month);
        endDate.add(dayOfMonth);
        */

    }

    /**
     * displays datepicker dialog with current date
     */
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        // cal.getTime(); use this to get the time
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
        datePickerDialog.show();
    }

    /**
     * checks if fields for medication properties  are empty
     *
     * @return true or false depending on whether fields are empty
     */
    private Boolean notEmpty() {
        Boolean notBlank = false;

        String medName = medNameET.getText().toString();
        String type = medTypeTV.getText().toString();
        String medDosage = medDosageET.getText().toString();
        String medBrand = medBrandET.getText().toString();
        String medCount = medCountET.getText().toString();


        if (medName.isEmpty() || type.isEmpty() || medDosage.isEmpty() || medCount.isEmpty()) {
            Toast.makeText(this, "Some fields are empty", Toast.LENGTH_SHORT).show();
        } else {
            notBlank = true;
        }
        return notBlank;
    }


    /**
     * This method will be invoked when a menu item is clicked if the item
     * itself did not already handle the event.
     *
     * @param item the menu item that was clicked
     * @return {@code true} if the event was handled, {@code false}
     * otherwise
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mlIT:
                dosageUnitTV.setText(getString(R.string.mlText));
                break;
            case R.id.iuIT:
                dosageUnitTV.setText(getString(R.string.iuText));
                break;
            case R.id.ozIT:
                dosageUnitTV.setText(getString(R.string.ozText));
                break;
        }
        return true;
    }

}



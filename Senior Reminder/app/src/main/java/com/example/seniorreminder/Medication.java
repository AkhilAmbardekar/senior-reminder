package com.example.seniorreminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import static androidx.constraintlayout.widget.Constraints.TAG;

// Note: this was an abstract class before (and ideally would still be one), but firebase was running
// into issues with the other "subobjects" (e.g. Antibiotics, medication, etc.) extending an abstract object
/**
 * Abstract class for medications containing methods that apply to all medications and instance
 * variables that all medications should contain
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.3
 */
public class Medication implements Parcelable {

    public String name;
    public String brand;
    public String type;

    private float dosageAmt;

    private String dosageUnit;

    private int countInitial;
    private float countCurrent;
    private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;

    private boolean notifPref;
    // figure out how to implement frequency state/var (w/ frequency as another class)
    // don't forget to add it to constructor

    /**
     * public no arg constructor
     */
    public Medication(){
        // no-arg constructor
    }

    /**
     * Constructor for medication object
     *
     * @param name name of medication
     * @param brand brand of medication
     * @param type type of medication
     * @param dosageAmt dosage of the medication
     * @param dosageUnit unit in which dosage is measured
     * @param countInitial initial count (number) of medication
     * @param countCurrent current number of medication
     * @param year start date (year) of medication
     * @param month start date (month) of medication
     * @param day start date (day) of medication
     * @param hour time (hour) at which medication must be taken during the day
     * @param minute time (minute) at which medication must be taken during the day
     * @param notifPref whether notifications should be sent for the medication
     */
    public Medication(String name, String brand, String type, float dosageAmt, String dosageUnit, int countInitial,
                      float countCurrent, int year, int month, int day, int hour, int minute, Boolean notifPref) {

        this.name = name;
        this.brand = brand;
        this.type = type;
        this.dosageAmt = dosageAmt;
        // decide on default unit
        this.dosageUnit = dosageUnit;
        this.countInitial = countInitial;
        this.countCurrent = countCurrent;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.notifPref = notifPref;
    }

    /**
     * Retrieves data from parcel and created Medication object
     *
     * @param in parcelable data
     */
    protected Medication(Parcel in) {
        name = in.readString();
        brand = in.readString();
        type = in.readString();
        dosageAmt = in.readFloat();
        dosageUnit = in.readString();
        countInitial = in.readInt();
        countCurrent = in.readFloat();
        hour = in.readInt();
        minute = in.readInt();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        notifPref = in.readBoolean();
    }

    /**
     * Creates medication object from parcel
     */
    public static final Creator<Medication> CREATOR = new Creator<Medication>() {

        /**
         * Creates medication object from parcel
         *
         * @param in parcel containing Medication object data
         * @return Medication object
         */
        @Override
        public Medication createFromParcel(Parcel in) {
            return new Medication(in);
        }

        /**
         * creates an array of Medication object data
         *
         * @param size amount of data in the parcel
         * @return Medication object data
         */
        @Override
        public Medication[] newArray(int size) {
            return new Medication[size];
        }
    };

    /**
     * Returns hour of time that medication must be taken
     *
     * @return hour - hour that medication must be taken
     */
    public int getHour() {
        return hour;
    }

    /**
     * Changes hour that medication must be taken
     *
     * @param hour - hour that medication should know be taken
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Returns minute of time that medication must be taken
     *
     * @return minute - minute that medication must be taken
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Changes minute that medication must be taken
     *
     * @param minute - minute that medication should know be taken
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * Returns year of date that medication should start being taken
     *
     * @return year - year that medication should start being taken
     */
    public int getYear() {
        return year;
    }

    /**
     * Changes year of date that medication should start being taken
     *
     * @param year - year that medication should now start being taken
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Returns month of date that medication should start being taken
     *
     * @return month - month that medication should start being taken
     */
    public int getMonth() {
        return month;
    }

    /**
     * Changes month of date that medication should start being taken
     *
     * @param month - month that medication should now start being taken
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * Returns day of date that medication should start being taken
     *
     * @return day - day that medication should start being taken
     */
    public int getDay() {
        return day;
    }

    /**
     * Changes day of date that medication should start being taken
     *
     * @param day - day that medication should now start being taken
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Returns whether user wants notifications for the medication
     *
     * @return true or false depending on whether user wants notifications
     */
    public boolean isNotifPref() {
        return notifPref;
    }

    /**
     * Changes whether user wants notifications for the medication
     *
     * @return true or false depending on whether user now wants notifications
     */
    public void setNotifPref(boolean notifPref) {
        this.notifPref = notifPref;
    }

    /**
     * Returns name of medication
     *
     * @return String - name of medication
     */
    public String getName(){
        return name;
    }

    /**
     * Returns type of medication
     *
     * @return String - type of medication
     */
    public String getType(){
        return type;
    }

    /**
     * Returns brand of medication
     *
     * @return String - brand of medication
     */
    public String getBrand(){
        return brand;
    }

    /**
     * Returns initial count of medication
     *
     * @return int - initial count of medication
     */
    public int getCountInitial(){
        return countInitial;
    }

    /**
     * Returns dosage amount of the medication
     *
     * @return float - dosage of the medication
     */
    public float getDosageAmt(){
        return dosageAmt;
    }

    /**
     * Returns current count of medicine
     *
     * @return float - current count of medication
     */
    public float getCountCurrent(){
        return countCurrent;
    }

    // could change this function to returning void?
    /**
     * Allows user to update name of medication
     *
     * @param nameET User entered new name for medication
     * @return String - new name
     */
    public String editName(EditText nameET){
        this.name = nameET.getText().toString();
        return name;
    }

    /**
     * Allows user to update count of medication
     * (Note: change to current count?)
     *
     * @param countET User entered count of the medication
     * @return int - new count of medication
     */
    public int editCount(EditText countET){
        String newCount = countET.getText().toString();
        // could use this android:inputType="number" in code later
        this.countInitial = Integer.parseInt(newCount);
        return countInitial;
    }

    /**
     * Decreases count of medication when it is taken
     *
     * @return float - current count of medication (remaining)
     */
    public float updateCount(){
        if(countCurrent != 0) {
            this.countCurrent = this.countCurrent - this.dosageAmt;
        } else{
            this.countCurrent = this.countInitial - this.dosageAmt;
        }
        return countCurrent;
    }

    /**
     * determines whether medication was taken
     *
     * @return true/false if medication was taken
     */
    public boolean isMedTaken(){
        // check if medication was taken WIP
        return true;
    }

    /**
     * adds medication to firestore database, in user specific document
     * alerts user if medication was successfully added
     *
     * @param context application context (activity)
     */
    public void addMedDatabase(Context context){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();

                    /*
                    change so that Medication object goes within collection and document within here (to keep
                    ArrayList and Medication separate and organized)
                     */

        DocumentReference documentReference = db.collection("users").document(userID).collection("medication").document();

        documentReference.set(this, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            /**
             * provides message if upload to database is successful
             */
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: ");
            }
        });

        /*
        DocumentReference arrayRef = db.collection("users").document(userID).collection("arrayList").document("array");

        Map<String, Object> medArrayList = new HashMap<>();
        ArrayList<Medication> medProperties = new ArrayList<Medication>();
        medProperties.add(newMed);

        medArrayList.put("medProperties", medProperties);

        arrayRef.set(medArrayList).addOnSuccessListener(new OnSuccessListener<Void>() {
        */

            // @Override
            /*
             * provides message if upload to database is successful
             */
            /*
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: ");
            }
        });

             */
        Toast.makeText(context, "Successfully added medication", Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns dosage unit for medication
     *
     * @return dosage unit of medication
     */
    public String getDosageUnit() {
        return dosageUnit;
    }

    /**
     * changes dosage unit of medication
     *
     * @param dosageUnit new dosage unit of medication
     */
    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(brand);
        dest.writeString(type);
        dest.writeFloat(dosageAmt);
        dest.writeString(dosageUnit);
        dest.writeInt(countInitial);
        dest.writeFloat(countCurrent);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
    }
}
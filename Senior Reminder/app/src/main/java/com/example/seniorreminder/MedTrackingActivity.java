package com.example.seniorreminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * List/pill tracking activity for senior reminder app
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 2.8
 */
public class MedTrackingActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    private static final String TAG = "MedTrackingListActivity";
    public ArrayList<Medication> listMeds = new ArrayList<>();

    private FloatingActionButton addMedFAB;
    private Button sortBTN;

    String userID;

    private ListView medListLV;

    private NotificationManagerCompat notificationManager;

    /**
     * creates layout for list activity
     * and initializes everything in xml layout
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupMedListActivity();

        listMeds = retrieveMedsDatabase();

        removeDuplicates();
        deleteMedAction();

        for (int i = 0; i < listMeds.size(); i++) {
            if (listMeds.get(i).isNotifPref() == Boolean.TRUE) {
                repeatNotifications(listMeds.get(i));
            }
        }

        addMedFAB.setOnClickListener(new View.OnClickListener() {
            /**
             * takes user to activity where they can add medications do the database
             */
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MedTrackingActivity.this, v);
                popupMenu.setOnMenuItemClickListener(MedTrackingActivity.this);
                popupMenu.inflate(R.menu.medtype_menu);
                popupMenu.show();

            }
        });

        sortBTN.setOnClickListener(new View.OnClickListener() {
            /**
             * sorts medications in listview either chronologically or alphabetically by name
             * @param v
             */
            @Override
            public void onClick(View v) {
                if (sortBTN.getText().toString().equals(getString(R.string.timeSortText))) {
                    listMeds = sortTimeSelection(listMeds); // sort by time, then inflate
                    sortBTN.setText(getString(R.string.alphaSortText));

                } else if (sortBTN.getText().toString().equals(getString(R.string.alphaSortText))) {
                    listMeds = sortAlphaSelection(listMeds); // sort by alpha then inflate
                    sortBTN.setText(getString(R.string.timeSortText));
                }
                setupList();
            }
        });

        BottomNavViewSetup bottomNavViewSetup = new BottomNavViewSetup();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavViewSetup.setupBottomNavViewSetup(bottomNavigationView, MedTrackingActivity.this);

    }

    /**
     * deletes medication from list of medications
     */
    private void deleteMedAction() {
        if (getIntent().hasExtra("com.example.seniorreminder.deleteMed")) {
            int deleteMedPos = getIntent().getExtras().getInt("com.example.seniorreminder.deleteMed");
            //listMeds.remove(deleteMedPos);
        }
    }

    /**
     * removes duplicate medications (after editing and saving medication)
     */
    private void removeDuplicates(){
        if (getIntent().hasExtra("com.example.seniorreminder.editedMed")) {
            String editedMedName = getIntent().getExtras().getString("com.example.seniorreminder.editedMed");
            for (int i = 0; i < listMeds.size(); i++) {
                if (listMeds.get(i).getName().equals(editedMedName)) {
                    Toast.makeText(MedTrackingActivity.this, "Removing duplicate entry", Toast.LENGTH_SHORT).show();
                    listMeds.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * sets repeating notifications for medication
     *
     * @param med - medication for which notifications need to be initialized
     */
    private void repeatNotifications(final Medication med){
        Calendar startDate = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();

        startDate.set(Calendar.YEAR, med.getYear());
        startDate.set(Calendar.MONTH, med.getMonth());
        startDate.set(Calendar.DAY_OF_MONTH, med.getDay());

        if (startDate.before(currentDate)) {
            long INTERVAL_MSEC = 1000*60*60*24; // change to day or to match frequency
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    sendNotif(med);
                }


            };
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR, med.getHour());
            time.set(Calendar.MINUTE, med.getMinute());

            timer.scheduleAtFixedRate(task, time.getTime(), INTERVAL_MSEC);
        } else{
            Toast.makeText(getApplicationContext(), "You have not begun taking " + med.getName(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * retrieves medication information from the database (specific to the user)
     * and then generates listview with those medications
     */
    private ArrayList<Medication> retrieveMedsDatabase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        userID = firebaseAuth.getCurrentUser().getUid();

        CollectionReference collectionReference = db.collection("users").document(userID).collection("medication");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                        // may need to change now that there are multiple types of objects
                        // Antibiotics newAntibiotic = documentSnapshot.toObject(Antibiotics.class);
                        // HeartMed newHeartMed = documentSnapshot.toObject(HeartMed.class);
                        // DiabeticMed newDiabeticMed = documentSnapshot.toObject(DiabeticMed.class);
                        // OtherMed newOtherMed = documentSnapshot.toObject(OtherMed.class);
                        Medication newMed = documentSnapshot.toObject(Medication.class);

                        // listMeds.add(newAntibiotic);
                        // listMeds.add(newHeartMed);
                        // listMeds.add(newDiabeticMed);
                        // listMeds.add(newOtherMed);
                        listMeds.add(newMed);
                    }
                    // move this all out of the button (eventually get rid of button) and move to OnStart
                    setupList();
                }
            }
        });
        return listMeds;
    }

    /**
     * creates and loads medication listview
     */
    private void setupList(){
        medListLV = findViewById(R.id.medListLV);
        // need to get info from objects for each item in the text view
        MedListAdapter adapter = new MedListAdapter(MedTrackingActivity.this, R.layout.adapter_medication_listview, listMeds);
        medListLV.setAdapter(adapter);
    }

    /**
     * creates layout for MedListActivity
     */
    private void setupMedListActivity(){
        setContentView(R.layout.activity_med_tracking_list);

        // put this in an if statement
        notificationManager = NotificationManagerCompat.from(this);

        addMedFAB = (FloatingActionButton)findViewById(R.id.addMedFAB);
        sortBTN = findViewById(R.id.sortBTN);
    }

    /**
     * sends notification for medication (different priority depending on type of medication)
     *
     * @param med - medication for which notification must be sent
     */
    public void sendNotif(Medication med) {

        String title = med.getName();
        String message = "Time to take: " + med.getName();
        Intent antibioticIntent = new Intent(this, MedTrackingActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, antibioticIntent, 0);
        Intent broadcastIntent = new Intent(this, NotifReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channel = App.idChannel1;

        // do something similar for notification icons
        if (med.getType().equals(getString(R.string.antiobioticTitle))){
            channel = App.idChannel1;
        }
        else if (med.getType().equals(getString(R.string.diabeticTitle))) {
            channel = App.idChannel2;
        } else if (med.getType().equals(getString(R.string.heartTitle))) {
            channel = App.idChannel3;
        } else if (med.getType().equals(getString(R.string.otherMedTitle))) {
            channel = App.idChannel4;
        }
        Notification notification = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();

        notificationManager.notify(1, notification);

// use this to notify at a certain time
        //AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), contentIntent); // check what code needs to run for alarm to properly display... otherwise https://www.youtube.com/watch?v=nl-dheVpt8o

        /*
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, cal.getTime().getMinutes() + 1);
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, OtherMedReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

         */


    }

    /**
     * Bubble sort for ArrayList of medications
     * Sorts them by their name
     *
     * @param arrayListToSort - ArrayList containing all medications
     * @return sorted ArrayList (alphabetical)
     */
    private ArrayList<Medication> sortAlphaBubble(ArrayList<Medication> arrayListToSort){

        int arrayLength = arrayListToSort.size();

        for (int i = 0; i < arrayLength - 1; i++){

            for (int j = 0; j < arrayLength - i - 1; j++){

                String elmt1 = arrayListToSort.get(j).getName().toLowerCase();
                String elmt2 = arrayListToSort.get(j+1).getName().toLowerCase();

                if (elmt1.charAt(0) > elmt2.charAt(0)){

                    // System.out.println(elmt2);

                    Medication bigElmt = arrayListToSort.get(j);
                    Medication smallElmt = arrayListToSort.get(j+1);

                    arrayListToSort.set(j, smallElmt);
                    arrayListToSort.set(j+1, bigElmt);

                }
            }

        }

        return arrayListToSort;
    }

    /**
     * Selection sort for ArrayList of medications
     * Sorts them by their name
     *
     * @param arrayListToSort - ArrayList containing all medications
     * @return sorted ArrayList (alphabetical)
     */
    private ArrayList<Medication> sortTimeSelection(ArrayList<Medication> arrayListToSort){

        int arrayLength = arrayListToSort.size();
        int smallest;

        for (int i = 0; i < arrayLength - 1 ; i++){

            smallest = i;

            for(int j = i + 1; j < arrayLength ; j++) {

                int elmt1 = arrayListToSort.get(j).getHour();
                int elmt2 = arrayListToSort.get(smallest).getHour();

                int elmt11 = arrayListToSort.get(j).getMinute();
                int elmt22 = arrayListToSort.get(smallest).getMinute();

                if(elmt1 < elmt2 || ((elmt1 == elmt2) && (elmt11 < elmt22)))  {

                    smallest = j ;

                }
            }

            Medication smallElmt = arrayListToSort.get(i);
            Medication bigElmt = arrayListToSort.get(smallest);

            arrayListToSort.set(smallest, smallElmt);
            arrayListToSort.set(i, bigElmt);
        }

        return arrayListToSort;
    }

    /**
     * Selection sort for ArrayList of medications
     * Sorts them by their name
     *
     * @param arrayListToSort - ArrayList containing all medications
     * @return sorted ArrayList (alphabetical)
     */
    private ArrayList<Medication> sortAlphaSelection(ArrayList<Medication> arrayListToSort){

        int arrayLength = arrayListToSort.size();
        int smallest;

        for (int i = 0; i < arrayLength - 1 ; i++){

            smallest = i;

            for(int j = i + 1; j < arrayLength ; j++) {

                String elmt1 = arrayListToSort.get(j).getName().toLowerCase();
                String elmt2 = arrayListToSort.get(smallest).getName().toLowerCase();

                if(elmt1.charAt(0) < elmt2.charAt(0))  {

                    smallest = j ;

                }
            }

            Medication smallElmt = arrayListToSort.get(i);
            Medication bigElmt = arrayListToSort.get(smallest);

            arrayListToSort.set(smallest, smallElmt);
            arrayListToSort.set(i, bigElmt);
        }

        return arrayListToSort;
    }

    /**
     * Binary search algorithm to find medication by name
     *
     * @param arrayListToSearch ArrayList containing all medications
     * @param match name of medication that user is looking for
     * @return integer corresponding to location of medication in arrayListToSearch
     */
    private int binarySearchString(ArrayList<Medication> arrayListToSearch, String match){

        arrayListToSearch = sortAlphaSelection(arrayListToSearch);

        int arrayLength = arrayListToSearch.size();

        int lowElmt = 0;
        int highElmt = arrayLength - 1;

        while (lowElmt <= highElmt){

            int midElmt = (lowElmt + highElmt)/2;

            if (arrayListToSearch.get(midElmt).getName().charAt(0) < match.charAt(0)){

                lowElmt = midElmt + 1;

            } else if(arrayListToSearch.get(midElmt).getName().charAt(0) > match.charAt(0)){

                highElmt = midElmt - 1;

            } else{

                return midElmt;

            }

        }

        return -1;

    }

    // generalize to find different attributes of medication object (other than name)

    /**
     * Linear search algorithm to find medication by name
     *
     * @param arrayListToSearch ArrayList containing all medications
     * @param match name of medication that user is looking for
     * @return integer corresponding to location of medication in arrayListToSearch
     */
    private int linearSearchString(ArrayList<Medication> arrayListToSearch, String match){

        int arrayLength = arrayListToSearch.size();
        int counter = -1;

        for (int i = 0; i < arrayLength - 1; i++){

            if (arrayListToSearch.get(i).getName().equals(match)){

                counter = i;

            }

        }

        return counter;

    }

    /**
     * will be used to retrieve information from database whenever user returns to this activity
     */
    protected void onStart() {
        super.onStart();
        // use this for database code, once button is removed... so that refreshes each time activity is opened, or use finish() after switching activities
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
            case R.id.diabeticMedIT:
                // to pass information to other activity:
                // if(getIntent().hasExtra("com.example.seniorreminder.SOMETHING")) {getIntent().getExtras().getString("com.example.seniorreminder.medtype"}
                // use uri method to go to website android studio for beginners 2
                Intent startIntent = new Intent(MedTrackingActivity.this, MedAddActivity.class);
                startIntent.putExtra("com.example.seniorreminder.medtype", getString(R.string.diabeticTitle));
                startActivity(startIntent);
                break;
            case R.id.heartMedIT:
                // to pass information to other activity:
                // if(getIntent().hasExtra("com.example.seniorreminder.SOMETHING")) {getIntent().getExtras().getString("com.example.seniorreminder.medtype"}
                // use uri method to go to website android studio for beginners 2
                startIntent = new Intent(MedTrackingActivity.this, MedAddActivity.class);
                startIntent.putExtra("com.example.seniorreminder.medtype", getString(R.string.heartTitle));
                startActivity(startIntent);
                break;
            case R.id.antibioticIT:
                // to pass information to other activity:
                // if(getIntent().hasExtra("com.example.seniorreminder.SOMETHING")) {getIntent().getExtras().getString("com.example.seniorreminder.medtype"}
                // use uri method to go to website android studio for beginners 2
                startIntent = new Intent(MedTrackingActivity.this, MedAddActivity.class);
                startIntent.putExtra("com.example.seniorreminder.medtype", getString(R.string.antiobioticTitle));
                startActivity(startIntent);
                break;
            case R.id.otherMedIT:
                // to pass information to other activity:
                // if(getIntent().hasExtra("com.example.seniorreminder.SOMETHING")) {getIntent().getExtras().getString("com.example.seniorreminder.medtype"}
                // use uri method to go to website android studio for beginners 2
                startIntent = new Intent(MedTrackingActivity.this, MedAddActivity.class);
                startIntent.putExtra("com.example.seniorreminder.medtype", getString(R.string.otherMedTitle));
                startActivity(startIntent);
                break;
        }
        return false;
    }
}



/*
sortAlphaBubble run time analysis
for ArrayList of length n:
outer "for" loop runs n times (going through each element in the list)
the inner "for" loop always runs n times, comparing every adjacent element and potentially swapping them
this gives it a computational complexity of O(n^2)
empirical data: when sorting a 5 mb file of medication data, it ran for over 1 hr without completing (200001 elements in the list)

sortAlphaSelection run time analysis
for ArrayList of length n:
outer "for" loop runs n times, until the smallest element isond
inner "for" loop must run n times to ensure that the chosen element is the smallest--> this is n-1 comparisons
the number of comparisons decreases by 1 each time the outer for loop runs
this gives it a worst case computational complexity of O(n^2)
however, always better than bubble sort
empirical data: when sorting the same file, it also ran for over 1 hr without completing

.sort() run time analysis
built in sort function uses a method called merge sort
breaks array into 2 halves, and sorts both arrays using a recurisive method (logn)
then merges the two half arrays (n)
this gives it a computational complexity of O(nlogn)
empirical data: when sorting the same file, it took 0.363s to complete

binarySearchString run time analysis
ignoring the time to sort the array (as that depends on what searching algorithm is used)
the array is constantly divided in half, until the correct area with the index is found --> 2^n times... so O(log(2)n) is the worst case if middle element of the divided arrays is never the "match"
for example, in an array of 15 elements, the array will be divided 4 times and each time a single comparison will be made --> 4 comparisons

linearSearchString run time analysis
the linear search algorithm goes through every element from beginning to the end untilt he "match" is found
in the worst case, the last element is the match, so computational complexity of O(n)

Video Analysis:
What do you notice about the searches that have the thin bars vs. those with the thicker bars?
the searches with thin bars generally longer (presumably because there are more items in the array)
presumably, better sorting algorithms are being used when there are more items in the array (otherwise, they would be considerably longer than searches with less items)
taller bars represent larger pieces of data

Why would someone make this video?
someone would make this video to show the relative speed of processes in each sorting algorithm (run time analysis)
the process in which the sorting algorthms sort through elements
for example selection sort looks for the smallest, puts it at the front, then checks through the rest, looping through this structure
radix sort looks at groups, arranges them, then joins the groups together
some make an approximate sort before sorting perfectly
bogo sort is completely ineffective at sorting

Could this video be skewed to show something that is incorrect? How so?
since the creator of the video isn't using evenly thin/thick bars, it could incorrectly show that certain processes are faster than others just based on the time they take to complete
a sort which takes more time maybe faster but it took more time because there were more items to sort through
it could also be skewed to show that increasing the number of items in an array does not have a major impact on the runtime
however, this is incorrect: if selection sort was used on some of the larger arrays, it would not have completed in a reasonable amount of time
/*

/*
Byte streams are limited to 8-bit bytes
Character streams are limited to 16-bit unicode characters
*/

/* finite data representations
all data on our computers is stored in bits (1s and 0s)
as a result, they can be limited by computer storage.
exact value of pi cannot be stored on a computer (because it is irrational)
when your computer has a 500 GB hard drive, it means that it can store 500 * 10^9 bytes of information (1s and 0s)

a set number of bits is used to represent pieces of data
bytes are 8 bits of data
int is typically 32 bit and can then hold values for 0 to 2^32 (unsigned), or (-2^31) to (2^31) ... signed
floats are typically 32 bits and can represent decimal numbers/fractions (have much larger range than ints)... written similar to scientific notation
however, floats can sometimes store decimals like 4.3 inaccurately as 4.30000000021 due to their method of storing numbers or fractions like 1/3 are never stored exactly
this can result in round-off error... can impact programs that require extremely high precision
doubles are more precise than floats
booleans are 1 bit (true or false)
char is 16 bits and corresponds to a unicode character
string is made up of characters, so they take up a lot more space
doubles and longs are 64 bits
therefore, it is always best to use datatypes that are smaller when possible (e.g. int instead of long
if values assigned to variables are greater than variable types # of bits, computer could show an error (overflow) or truncate the number

these can be casted from one to another (with the exception of a boolean), but data can be lost... cas
for example, when float converted to int, decimals are truncated

float x = 6.789
int x = (int)x
System.out.println(x) --> 6


when int converted to string, mathematical operations are no longer possible

int y = -345
String y = (String)y
int result = y * 6 --> error
*/
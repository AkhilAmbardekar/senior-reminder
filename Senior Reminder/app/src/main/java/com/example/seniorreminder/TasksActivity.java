package com.example.seniorreminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;

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
 * Tasks activity for displaying tasks in listview
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.5
 */
public class TasksActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavView;

    private FloatingActionButton addTaskFAB;

    private ListView tasksLV;

    public ArrayList<CustomTask> tasksList = new ArrayList<>();

    private NotificationManagerCompat notificationManager;

    private TextView tasksMainTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        setupTasksActivity();

        int deleteMedPos = -1;
        if (getIntent().hasExtra("com.example.seniorreminder.selectedTask")) {
            deleteMedPos = getIntent().getExtras().getInt("com.example.seniorreminder.selectedTask");
        }
        setupTasksList();

        tasksLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * allows user to edit task if they click on the task in the listview
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openEditFragment();
            }
        });

        addTaskFAB.setOnClickListener(new View.OnClickListener() {
            /**
             * opens fragment to add task when they click on the button
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                openEditFragment();
            }
        });

    }

    /**
     * initializes listview and populates it
     */
    private void setupTasksList(){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();

        CollectionReference custColRef = db.collection("users").document(userID).collection("customTasks");
        custColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<CustomTask> custTasksList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                        CustomTask retTask = documentSnapshot.toObject(CustomTask.class);

                        custTasksList.add(retTask);
                    }
                    tasksList = custTasksList;

                    tasksLV = findViewById(R.id.tasksLV);
                    // need to get info from objects for each item in the text view
                    TaskListAdapter adapter = new TaskListAdapter(TasksActivity.this, R.layout.adapter_task_list, tasksList);
                    tasksLV.setAdapter(adapter);

                    repeatTasksNotif();

                }
            }
        });

        CollectionReference critColRef = db.collection("users").document(userID).collection("criticalTasks");
        critColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<CustomTask> critTasksList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                        CriticalTask retTask = documentSnapshot.toObject(CriticalTask.class);

                        critTasksList.add(retTask);
                    }

                    tasksList.addAll(critTasksList);

                }
            }
        });
    }

    /**
     * initializes repeating notification for each task in the list
     */
    private void repeatTasksNotif(){
        for (int i =0; i < tasksList.size(); i++) {
            long INTERVAL_MSEC = 1000*60*60*24;

            Timer timer = new Timer();
            final int finalI = i;
            TimerTask notifTask = new TimerTask() {
                @Override
                public void run() {
                    sendTaskNotif(tasksList.get(finalI));
                }
            };

            Calendar cal = Calendar.getInstance();
            // default first notif sent 20 mins after, repeating each day until task is completed
            cal.set(Calendar.MINUTE, cal.getTime().getMinutes() + 20);
            timer.scheduleAtFixedRate(notifTask, cal.getTime(), INTERVAL_MSEC);
        }
    }

    /**
     * sets up task activity layout
     */
    private void setupTasksActivity(){
        addTaskFAB = findViewById(R.id.addTaskFAB);
        tasksLV = findViewById(R.id.tasksLV);
        tasksMainTV = findViewById(R.id.tasksMainTV);

        BottomNavViewSetup bottomNavViewSetup = new BottomNavViewSetup();
        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavViewSetup.setupBottomNavViewSetup(bottomNavView, TasksActivity.this);
    }

    /**
     * opens the edit fragment
     */
    private void openEditFragment(){
        addTaskFAB.setVisibility(View.GONE);
        tasksLV.setVisibility(View.GONE);
        tasksMainTV.setVisibility(View.GONE);

        FragmentManager fragManager = getSupportFragmentManager();
        TaskAddFragment fragment = new TaskAddFragment();
        fragManager.beginTransaction().replace(R.id.tasksContainer, fragment).commit();
    }

    /**
     * sends a notification for the task
     *
     * @param task - task for which notification must be sent
     */
    public void sendTaskNotif(CustomTask task) {

        String title = task.getName();
        String message = "Time to complete: " + task.getName();
        Intent taskIntent = new Intent(this, TasksActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, taskIntent, 0);
        Intent broadcastIntent = new Intent(this, NotifReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, App.idChannel5)
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

    }

}
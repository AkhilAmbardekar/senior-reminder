package com.example.seniorreminder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * class for creating instance of fragment
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.0
 */
public class TaskAddFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView intensityTV;
    private TextView taskImportanceTV;

    private EditText taskNameET;
    private EditText taskTypeET;

    private SeekBar intensitySB;

    private Button submitBTN;

    private int intensity;

    /**
     * no arg constructor for TaskAddFragment
     */
    public TaskAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment taskAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskAddFragment newInstance(String param1, String param2) {
        TaskAddFragment fragment = new TaskAddFragment();

        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * required for fragment
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    /**
     * creates layout for TaskAddFragment
     *
     * @param inflater - inflates view
     * @param container - container of view (e.g. constraint layout, relative layout, etc.)
     * @param savedInstanceState - previous state of fragment
     * @return view for TaskAddFragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_add, container, false);

        setupTaskAddFrag(view);

        updateSeekBar(view);

        submitBTN.setOnClickListener(new View.OnClickListener() {
            /**
             * create task
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                createTask();

            }
        });

        return view;
    }

    /**
     * sets up layout for fragment
     *
     * @param view - view (xml layout) of fragment
     */
    private void setupTaskAddFrag(View view){

        intensity = -1;

        taskImportanceTV = view.findViewById(R.id.taskImportanceTV);
        taskImportanceTV.setOnClickListener(new View.OnClickListener() {
            /**
             * opens menu when text view is clicked
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.inflate(R.menu.tasksbigtype_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    /**
                     * changes text of textview depending on what is clicked
                     *
                     * @param item - which item is clicked
                     * @return false
                     */
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.regularTaskIT:
                                taskImportanceTV.setText(getString(R.string.customTaskText));

                                intensitySB.setVisibility(View.GONE);
                                intensityTV.setVisibility(View.GONE);
                                break;
                            case R.id.criticalTaskIT:
                                taskImportanceTV.setText(getString(R.string.criticalTaskText));

                                intensitySB.setVisibility(View.VISIBLE);
                                intensityTV.setVisibility(View.VISIBLE);
                                break;
                        }
                        return false;
                    }
                });
            }
        });

        taskNameET = view.findViewById(R.id.taskNameET);
        taskTypeET = view.findViewById(R.id.taskNameET);

        submitBTN = view.findViewById(R.id.submitBTN);

    }

    /**
     * creates either custom task or critical task object, depending on user input
     */
    private void createTask(){
        String taskImportance = taskImportanceTV.getText().toString();
        String taskName = taskNameET.getText().toString();
        String taskType = taskTypeET.getText().toString();

        int intensityTask = intensity;

        // would have put this in separate function but running into an error with the view and fragments
        if (taskImportance.isEmpty() && taskName.isEmpty() && taskType.isEmpty() == Boolean.TRUE){
            Toast.makeText(getContext(), "Some fields are empty", Toast.LENGTH_SHORT).show();
        } else {
            if (taskImportance.equals(getString(R.string.customTaskText))) {
                CustomTask task = new CustomTask(taskName, taskType, Boolean.TRUE);

                addTaskDatabase(task, true);
            } else if (taskImportance.equals(getString(R.string.criticalTaskText))) {
                CriticalTask task = new CriticalTask(taskName, taskType, Boolean.TRUE, intensityTask);

                addTaskDatabase(task, false);
            }

        }
    }

    /**
     * uploads the object to its specific directory in the database
     *
     * @param task - task object
     * @param isCustomTask - type of object
     */
    private void addTaskDatabase(CustomTask task, Boolean isCustomTask){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("users").document(userID).collection("customTasks").document();

        if (isCustomTask.equals(Boolean.FALSE)) {
            documentReference = db.collection("users").document(userID).collection("criticalTasks").document();
        }

        documentReference.set(task, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            /**
             * provides message if upload to database is successful
             */
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: ");

                Toast.makeText(getContext(), "Successfully added task", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), TasksActivity.class);
                startActivity(intent);
            }
        });



    }

    /**
     * initializes seekbar and displays value from seekbar
     *
     * @param view - view of fragment
     */
    private void updateSeekBar(View view){
        intensitySB = view.findViewById(R.id.intensitySB);
        intensityTV = view.findViewById(R.id.intensityTV);

        intensityTV.setText("Selected intensity: " + intensitySB.getProgress() + " / " + intensitySB.getMax());

        intensitySB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentSeek;

            /**
             * changes text when seekbar is changed
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                currentSeek = progress;
                intensityTV.setText("Selected intensity: " + progress + " / " + intensitySB.getMax());
                //Toast.makeText(getContext(), "Current intensity being changed", Toast.LENGTH_SHORT).show();
                intensity = progress;

            }

            /**
             * sends message when seekbar begins to be changed
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getContext(), "Intensity starting to be tracked", Toast.LENGTH_SHORT).show();
            }

            /**
             * sends message when seekbar is finished changing
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                intensityTV.setText("Selected intensity: " + currentSeek + " / " + intensitySB.getMax());
                //Toast.makeText(getContext(), "Intensity has stopped being tracked", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
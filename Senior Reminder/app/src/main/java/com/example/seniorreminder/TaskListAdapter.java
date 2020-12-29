package com.example.seniorreminder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * class for adapting an ArrayList into a listview that the user can view for tasks
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.3
 */
public class TaskListAdapter extends ArrayAdapter<CustomTask> {

    private Context activityContext;

    private int resourceID;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public TaskListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CustomTask> objects) {
        super(context, resource, objects);
        activityContext = context;
        resourceID = resource;
    }

    /**
     * generates and displays list of tasks
     *
     * @param position where in list view item appears
     * @return list of tasks in listview
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String taskName = getItem(position).getName();
        String taskType = getItem(position).getType();

        LayoutInflater inflater = LayoutInflater.from(activityContext);
        convertView = inflater.inflate(resourceID, parent, false);

        final TextView taskNameTV = convertView.findViewById(R.id.listTaskNameTV);
        final TextView taskTypeTV = convertView.findViewById(R.id.listTaskDescTV);
        ImageView priorityIV = convertView.findViewById(R.id.priorityIV);
        priorityIV.setVisibility(View.GONE);

        ImageButton doneBTN = convertView.findViewById(R.id.doneBTN);
        final int currentPos = position;
        doneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItem(currentPos).setNotifPref(Boolean.FALSE);
                Toast.makeText(getContext(), "You have completed the task", Toast.LENGTH_SHORT).show();
                removeTaskFromLV(currentPos);
            }
        });

        ImageButton tasksDeleteBTN = convertView.findViewById(R.id.tasksDeleteBTN);
        tasksDeleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTaskFromLV(currentPos);
            }
        });

        ImageButton zoomBTN = convertView.findViewById(R.id.zoomBTN);
        zoomBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTextSize(true, taskNameTV, taskTypeTV);
            }
        });

        zoomBTN.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             * Called when a view has been clicked and held.
             *
             * @param v The view that was clicked and held.
             * @return true if the callback consumed the long click, false otherwise.
             */
            @Override
            public boolean onLongClick(View v) {
                changeTextSize(false, taskNameTV, taskTypeTV);
                return false;
            }
        });


        try {
            int intensity = getItem(position).getIntensity();
            if (intensity > 50) {
                priorityIV.setVisibility(View.VISIBLE);
            } else{
                priorityIV.setVisibility(View.GONE);
            }

            taskTypeTV.setText(taskType + "| intensity: " + Integer.toString(intensity));

        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }

        taskNameTV.setText(taskName);
        taskTypeTV.setText("Type: " + taskType);

        return convertView;
    }

    /**
     * deletes task from the list of tasks
     *
     * @param currentPos - current task that is clicked
     */
    private void removeTaskFromLV(int currentPos){
        Toast.makeText(getContext(), "Deleting task", Toast.LENGTH_SHORT).show();

        Intent deleteIntent = new Intent(activityContext, TasksActivity.class);
        deleteIntent.putExtra("com.example.seniorreminder.selectedTask", currentPos);
        activityContext.startActivity(deleteIntent);
    }

    /**
     * changes text size in listview for specific task
     *
     * @param increase - whether text size should be increased or decreased
     * @param taskNameTV - task name textview
     * @param taskTypeTV - task type textview
     */
    private void changeTextSize(boolean increase, TextView taskNameTV, TextView taskTypeTV){
        double factor = 1;
        if (increase == Boolean.TRUE){
            factor = 1.1;
        } else if (increase == Boolean.FALSE) {
            factor = 0.9;
        }
        float taskNameSize = taskNameTV.getTextSize();
        float taskTypeSize = taskTypeTV.getTextSize();

        taskNameTV.setTextSize((float) (taskNameSize*factor));
        taskTypeTV.setTextSize((float) (taskTypeSize*factor));
    }
}

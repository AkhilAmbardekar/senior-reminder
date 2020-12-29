package com.example.seniorreminder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * class for adapting an ArrayList into a listview that the user can view
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.5
 */
public class MedListAdapter extends ArrayAdapter<Medication> {

    private static final String TAG = "MedListAdapter";

    private Context activityContext;

    private int resourceID;

    private int lastPosition = -1;

    /**
     * ViewHolder class used to optimize data that loads when ListView is opened
     */
    private static class ViewHolder {
        TextView medNameTV;
        TextView timeTV;
        TextView descriptionTV;
        ImageButton editBTN;
        ImageButton notifBTN;
        ImageButton deleteBTN;
    }

    /**
     * Constructor for MedListAdapter
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public MedListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Medication> objects) {
        super(context, resource, objects);
        activityContext = context;
        resourceID = resource;
    }

    /**
     * generates and displays list of medications
     *
     * @param position where in list view item appears
     * @return list of medications in listview
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String medName = getItem(position).getName();
        String medBrand = getItem(position).getBrand();
        String medCount = Float.toString(getItem(position).getCountCurrent());
        String medDosage = Float.toString(getItem(position).getDosageAmt());
        String medDosageUnit = getItem(position).getDosageUnit();
        String medType = getItem(position).getType();
        String medHour = Integer.toString(getItem(position).getHour());
        String medMinute = Integer.toString(getItem(position).getMinute());

        String mainText = medName + "(" + medDosage + " " + medDosageUnit + ")";
        String descriptionText = medType.toUpperCase() + "; " + medBrand + "; " + medCount + " remaining";
        String timeText = medHour + ":" + medMinute;

        final View result;
        ViewHolder holder;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(activityContext);
            convertView = inflater.inflate(resourceID, parent, false);

            holder = new ViewHolder();

            holder.medNameTV = (TextView) convertView.findViewById(R.id.medNameTV);
            holder.descriptionTV = (TextView) convertView.findViewById(R.id.descriptionTV);
            holder.timeTV = (TextView) convertView.findViewById(R.id.timeTV);
            holder.editBTN = (ImageButton) convertView.findViewById(R.id.tasksEditBTN);
            holder.notifBTN = (ImageButton) convertView.findViewById(R.id.notifBTN);
            holder.deleteBTN = (ImageButton) convertView.findViewById(R.id.deleteBTN);

            result = convertView;
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
            result = convertView;
        }

        Animation anim = AnimationUtils.loadAnimation(activityContext, (position > lastPosition) ? R.anim.loading_down_anim : R.anim.loading_up_anim);
        result.startAnimation(anim);
        lastPosition = position;

        holder.medNameTV.setText(mainText);
        holder.descriptionTV.setText(descriptionText);
        holder.timeTV.setText(timeText);

       final int currentPos = position;

        holder.editBTN.setOnClickListener(new View.OnClickListener() {
            /**
             * opens activity to edit medication and passes info to load medication properties
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent editActivity = new Intent(activityContext, MedAddActivity.class);
                editActivity.putExtra("com.example.seniorreminder.selectedMed", getItem(currentPos));
                activityContext.startActivity(editActivity);
            }
        });

        holder.notifBTN.setOnClickListener(new View.OnClickListener() {
            /**
             * stops user from receiving notifications for that medication
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                getItem(currentPos).setNotifPref(Boolean.FALSE);
            }
        });

        holder.deleteBTN.setOnClickListener(new View.OnClickListener() {
            /**
             * passes information so that medication can be properly deleted
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent deleteMed = new Intent(activityContext, MedTrackingActivity.class);
                deleteMed.putExtra("com.example.seniorreminder.deleteMed", currentPos);
                activityContext.startActivity(deleteMed);
            }
        });

        return convertView;
    }
}

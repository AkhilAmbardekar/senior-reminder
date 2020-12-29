package com.example.seniorreminder;

import android.widget.EditText;

/**
 * Object holding task properties for critical tasks
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.0
 */
public class CriticalTask extends CustomTask {

    private int intensity;

    /**
     * This is the constructor for critical task object
     *
     * @param name name of critical task
     * @param type type of critical task
     * @param notifPref whether user wants to receive notifications
     * @param intensity relative importance of the task
     */
    public CriticalTask(String name, String type, Boolean notifPref, int intensity){
        super(name, type, notifPref);
        this.intensity = intensity;
    }

    /**
     * default no argument constructor for critical task object (required for firebase)
     */
    public CriticalTask(){

    }

    /**
     * Returns intensity of the critical task
     *
     * @return String - intensity of task
     */
    public int getIntensity(){
        return intensity;
    }

    // get intensity from number line/dragging thingy... will have to change from EditText type
    /**
     * Allows user to change the intensity of the task
     *
     * @param intensityET - user inputted new intensity of task
     * @return new intensity of task
     */
    public int editIntensity(EditText intensityET){
        String newIntensity = intensityET.getText().toString();
        this.intensity = Integer.parseInt(newIntensity);
        return intensity;
    }
}

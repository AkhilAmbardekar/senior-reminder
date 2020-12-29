package com.example.seniorreminder;

import android.widget.EditText;

/**
 * Object holding task properties
 * and common methods
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.0
 */
public class CustomTask {
    public String name;
    public String type;

    public Boolean notifPref;

    private int intensity;

    /**
     * default no argument constructor for custom task object (required for firebase)
     */
    public CustomTask(){

    }

    /**
     * Returns whether notifications should be sent for custom task
     *
     * @return
     */
    public Boolean getNotifPref() {
        return notifPref;
    }

    /**
     * Changes notification preference for a specific custom task
     *
     * @param notifPref true or false depending on whether user wants to receive notifications
     */
    public void setNotifPref(Boolean notifPref) {
        this.notifPref = notifPref;
    }

    /**
     * This is the constructor function for tasks
     *
     * @param name - initial name of the task
     * @param type - initial type of task
     */
    public CustomTask(String name, String type, Boolean notifPref) {
        this.name = name;
        this.type = type;
        this.notifPref = notifPref;
    }
    /**
     * Returns name of task
     *
     * @return String - name of task
     */
    public String getName(){
        return name;
    }

    /**
     * Returns relative importance of custom task
     *
     * @return intensity/importance of task
     */
    public int getIntensity(){
        return intensity;
    }

    /**
     * Returns type of task
     *
     * @return String - type of task
     */
    public String getType(){
        return type;
    }

    /**
     * Allows user to update name of task
     *
     * @param nameET User entered new name for task
     * @return String - new name
     */
    public String editName(EditText nameET){
        this.name = nameET.getText().toString();
        return name;
    }

    /**
     * Allows user to update type of task
     *
     * @param typeET User entered new type for task
     * @return String - new type
     */
    public String editType(EditText typeET){
        this.type = typeET.getText().toString();
        return type;
    }

}


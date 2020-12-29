package com.example.seniorreminder;

/**
 * Object holding medication properties
 * for antibiotics
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.5
 */
public class Antibiotics extends Medication {

    private String ingestionType;
    // figure out how to store as date
    private int expiry;
    private int timesTaken;

    /**
     * Default constructor for Antibiotics object
     *
     * @param name name of antibiotic
     * @param brand brand of antibiotic
     * @param type type of medication
     * @param dosageAmt dosage of the antibiotic
     * @param dosageUnit unit in which dosage is measured
     * @param countInitial initial count (number) of antibiotic
     * @param countCurrent current number of antibiotic
     * @param year start date (year) of antibiotic
     * @param month start date (month) of antibiotic
     * @param day start date (day) of antibiotic
     * @param hour time (hour) at which antibiotic must be taken during the day
     * @param minute time (minute) at which antibiotic must be taken during the day
     * @param ingestionType how the antibiotic is ingested
     * @param expiry when the antibiotic expires
     * @param timesTaken the number of times the antibiotic has been taken
     * @param notifPref whether notifications should be sent for the antibiotic
     */
    public Antibiotics(String name, String brand, String type, float dosageAmt, String dosageUnit,
                       int countInitial, float countCurrent, int year, int month, int day, int hour, int minute,
                       String ingestionType, int expiry, int timesTaken, Boolean notifPref) {

        super(name, brand, type, dosageAmt, dosageUnit, countInitial, countCurrent, year, month, day, hour, minute, notifPref);
        this.ingestionType = ingestionType;
        this.expiry = expiry;
        this.timesTaken = timesTaken;

    }
    // note: only primitive data types can be handled by firebase, otherwise other objects like Calendar
    // and ArrayLists would have been used in my objects

    /**
     * Returns type of ingestion for the antibiotic (liquid, pill, etc.)
     *
     * @return String - type of ingestion
     */
    public String getIngestionType(){
        return ingestionType;
    }

    /**
     * Returns expiry date of antibiotic
     *
     * @return int - expiry
     */
    public int getExpiry(){
        return expiry;
    }

    /**
     * Returns number of times antibiotic has been taken
     *
     * @return int - times taken
     */
    public int getTimesTaken(){
        return timesTaken;
    }

    /**
     * Updates number of times antibiotic has been taken after it has been taken
     *
     */
    public void updateTimesTaken(){
        this.timesTaken = this.timesTaken++;
    }

}

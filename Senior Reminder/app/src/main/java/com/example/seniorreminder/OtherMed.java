package com.example.seniorreminder;

/**
 * Object holding other medication properties
 * for general types of other medication (not heart, antibiotics, or diabetic)
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.1
 */
public class OtherMed extends Medication {

    /**
     * Constructor for other other medication
     *
     * @param name name of other medication
     * @param brand brand of other medication
     * @param type type of other medication
     * @param dosageAmt dosage of the other medication
     * @param dosageUnit unit in which dosage is measured
     * @param countInitial initial count (number) of other medication
     * @param countCurrent current number of other medication
     * @param year start date (year) of other medication
     * @param month start date (month) of other medication
     * @param day start date (day) of other medication
     * @param hour time (hour) at which other medication must be taken during the day
     * @param minute time (minute) at which other medication must be taken during the day
     * @param notifPref whether notifications should be sent for the other medication
     */
    public OtherMed(String name, String brand, String type, float dosageAmt, String dosageUnit, int countInitial,
                    float countCurrent, int year, int month, int day, int hour, int minute, Boolean notifPref) {

        super(name, brand, type, dosageAmt, dosageUnit, countInitial, countCurrent, year, month, day, hour, minute, notifPref);
    }
}

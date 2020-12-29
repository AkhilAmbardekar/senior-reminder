package com.example.seniorreminder;

/**
 * Object holding medication properties
 * for diabetic medications
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.5
 */
public class DiabeticMed extends Medication {

    private int critNumber;

    /**
     * Default constructor for DiabeticMed object
     *
     * @param name name of DiabeticMed
     * @param brand brand of DiabeticMed
     * @param type type of medication
     * @param dosageAmt dosage of the DiabeticMed
     * @param dosageUnit unit in which dosage is measured
     * @param countInitial initial count (number) of DiabeticMed
     * @param countCurrent current number of DiabeticMed
     * @param year start date (year) of DiabeticMed
     * @param month start date (month) of DiabeticMed
     * @param day start date (day) of DiabeticMed
     * @param hour time (hour) at which DiabeticMed must be taken during the day
     * @param minute time (minute) at which DiabeticMed must be taken during the day
     * @param critNumber minimum number of medication that should be maintained at all times
     * @param notifPref whether notifications should be sent for the DiabeticMed
     */
    public DiabeticMed(String name, String brand, String type, float dosageAmt, String dosageUnit,
                       int countInitial, float countCurrent, int year, int month, int day, int hour, int minute, int critNumber, Boolean notifPref) {

        super(name, brand, type, dosageAmt, dosageUnit, countInitial, countCurrent, year, month, day, hour, minute, notifPref);
        this.critNumber = critNumber;
    }

    /**
     * Returns # of doses that must be maintained at a minimum before new shipment required
     *
     * @return int - # of doses that must be maintained at a minimum
     */
    public int getCritNumber(){
        return critNumber;
    }

    /**
     * Determines whether new diabetic medication must be ordered
     *
     * @param countCurrent - current number of doses left
     */
    public void setEmergencyAlert(int countCurrent){
        if(countCurrent == critNumber){
            // Toast.makeText(MainActivity.this, "Must order new diabetic medication", Toast.LENGTH_LONG).show();
            System.out.println("Must order new diabetic medication");
        }
    }
}

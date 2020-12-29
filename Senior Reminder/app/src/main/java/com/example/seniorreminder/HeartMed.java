package com.example.seniorreminder;

/**
 * Object holding medication properties
 * for heart medications
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.0
 */
public class HeartMed extends Medication {

    private int critNumber;
    private String importance;

    /**
     * Constructor for heart medication
     *
     * @param name name of HeartMed
     * @param brand brand of HeartMed
     * @param type type of medication
     * @param dosageAmt dosage of the HeartMed
     * @param dosageUnit unit in which dosage is measured
     * @param countInitial initial count (number) of HeartMed
     * @param countCurrent current number of HeartMed
     * @param year start date (year) of HeartMed
     * @param month start date (month) of HeartMed
     * @param day start date (day) of HeartMed
     * @param hour time (hour) at which HeartMed must be taken during the day
     * @param minute time (minute) at which HeartMed must be taken during the day
     * @param critNumber minimum number of medication that should be maintained at all times
     * @param importance importance of heart medication
     * @param notifPref whether notifications should be sent for the HeartMed
     */
    public HeartMed(String name, String brand, String type, float dosageAmt, String dosageUnit,
                       int countInitial, float countCurrent, int year, int month, int day, int hour, int minute, int critNumber,
                    String importance, Boolean notifPref) {

        super(name, brand, type, dosageAmt, dosageUnit, countInitial, countCurrent, year, month, day, hour, minute, notifPref);
        this.critNumber = critNumber;
        this.importance = importance;
    }

    /**
     * fetches minimum number of doses that must be maintained
     *
     * @return minimum number of doses that must be maintained
     */
    public int getCritNumber() {
        return critNumber;
    }

    /**
     * changes importance of medication
     *
     * @param critNumber how important it is that medication is taken
     */
    public void setCritNumber(int critNumber) {
        this.critNumber = critNumber;
    }

    /**
     * fetches importance of medication
     *
     * @return importance of medication
     */
    public String getImportance() {
        return importance;
    }

    /**
     * changes importance of medication
     *
     * @param importance how important it is that medication is taken
     */
    public void setImportance(String importance) {
        this.importance = importance;
    }
}

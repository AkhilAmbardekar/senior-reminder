package com.example.seniorreminder;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentProvider;
import android.os.Build;

/**
 * Class for handling notifications throughout the entire application
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.3
 */
public class App extends Application {


    public static final String idChannel1 = "antibiotics";
    public static final String idChannel2 = "diabeticMeds";
    public static final String idChannel3 = "heartMeds";
    public static final String idChannel4 = "otherMeds";
    public static final String idChannel5 = "tasks";

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     *
     * <p>Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.</p>
     *
     * <p>If you override this method, be sure to call {@code super.onCreate()}.</p>
     *
     * <p class="note">Be aware that direct boot may also affect callback order on
     * Android {@link Build.VERSION_CODES#N} and later devices.
     * Until the user unlocks the device, only direct boot aware components are
     * allowed to run. You should consider that all direct boot unaware
     * components, including such {@link ContentProvider}, are
     * disabled until user unlock happens, especially when component callback
     * order matters.</p>
     */
    @Override
    public void onCreate() {
        super.onCreate();

        createNotifChannels();
    }

    /**
     * Initializes notification channels for application
     * Does this for all types of medications and one general channel for tasks
     */
    private void createNotifChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel antibiotics = new NotificationChannel(idChannel1,
                    "Antibiotics", NotificationManager.IMPORTANCE_DEFAULT);
            antibiotics.setDescription("Antibiotics Notification Settings");

            NotificationChannel diabeticMeds = new NotificationChannel(idChannel2,
                    "DiabeticMeds", NotificationManager.IMPORTANCE_DEFAULT);
            diabeticMeds.setDescription("Diabetic Medication Notification Settings");

            NotificationChannel heartMeds = new NotificationChannel(idChannel3,
                    "HeartMeds", NotificationManager.IMPORTANCE_HIGH);
            heartMeds.setDescription("Heart Medication Notification Settings");

            NotificationChannel otherMeds = new NotificationChannel(idChannel4,
                    "OtherMeds", NotificationManager.IMPORTANCE_LOW);
            otherMeds.setDescription("Other Medication Notification Settings");

            NotificationChannel tasks = new NotificationChannel(idChannel5,
                    "Tasks", NotificationManager.IMPORTANCE_HIGH);
            otherMeds.setDescription("Other Medication Notification Settings");

            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(antibiotics);
            manager.createNotificationChannel(diabeticMeds);
            manager.createNotificationChannel(heartMeds);
            manager.createNotificationChannel(otherMeds);
            manager.createNotificationChannel(tasks);
        }

    }
}

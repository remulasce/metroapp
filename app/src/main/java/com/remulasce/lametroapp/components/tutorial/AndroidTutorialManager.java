package com.remulasce.lametroapp.components.tutorial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.remulasce.lametroapp.java_core.analytics.Log;

/**
 * Created by Remulasce on 4/3/2015.
 *
 * does actual tutorial display on Android
 *
 * Uses Settings saver thing to remember if the user has ever done specific actions before.
 */
public class AndroidTutorialManager extends TutorialManager{

    private Context c;
    private Handler uiHandler;
    private long lastDismissalPlay;

    private TextView aboutPaneHint;

    private static final String TUTORIAL_PREFERENCES_NAME = "Tutorial";
    private static final String TUTORIAL_USER_KNOWS_DISMISSAL = "knows_notify_dismissal";
    private static final String TUTORIAL_USER_KNOWS_NOTIFY_SERVICE = "knows_notify_service";
    private static final String TUTORIAL_USER_KNOWS_UNDO_DISMISS = "knows_undo_dismiss";
    private static final String TUTORIAL_USER_KNOWS_ABOUT_PANE = "knows_about_pane";
    private static final String USER_EXPERIENCE_COUNT = "user_experience_count";


    private boolean appRunning = false;

    public AndroidTutorialManager(Context c, TextView aboutPaneHint) {
        this.c = c;
        this.uiHandler = new Handler( Looper.getMainLooper() );

        this.aboutPaneHint = aboutPaneHint;
        if (aboutPaneNeedsHint()) {
            aboutPaneHint.setVisibility(View.VISIBLE);
        } else {
            aboutPaneHint.setVisibility(View.INVISIBLE);
        }

    }

    public void appStarted() { appRunning = true; }
    public void appStopped() { appRunning = false; }


    @Override
    public void tripsNewlyShown() {
        if (!userKnows( TUTORIAL_USER_KNOWS_NOTIFY_SERVICE)) {
            showTutorial("Tap an arrival to set a notification for it", 3000);
        }

        if (!userKnows( TUTORIAL_USER_KNOWS_DISMISSAL)) {
            showTutorial("Swipe an arrival to dismiss it", 5000);
        }

        hideAboutHint();
    }

    private void hideAboutHint() {
        aboutPaneHint.setVisibility(View.INVISIBLE);
    }

    private void showTutorial(final String message, int delayMillis) {
        Log.i("AndroidTutorialManager", "Showing tutorial: " + message+" in "+delayMillis);
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (appRunning) {
                    Toast.makeText(c, message, Toast.LENGTH_LONG).show();
                }
            }
        }, delayMillis);
    }

    private boolean userKnows( String tutorialUserKnowsNotifyService) {
        SharedPreferences preferences = c.getSharedPreferences(TUTORIAL_PREFERENCES_NAME, 0);
        return preferences.getBoolean(tutorialUserKnowsNotifyService, false);
    }

    @Override
    public void tripDismissed() {
        setUserHasDone(TUTORIAL_USER_KNOWS_DISMISSAL);

        if (!userKnows(TUTORIAL_USER_KNOWS_UNDO_DISMISS) && System.currentTimeMillis() > lastDismissalPlay + 30000) {
            showTutorial("Trip dismissed. Tap the stop name to show this trip again", 0);
            lastDismissalPlay = System.currentTimeMillis();
        }
    }

    private void setUserHasDone(String actionName) {
        SharedPreferences preferences = c.getSharedPreferences(TUTORIAL_PREFERENCES_NAME, 0);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(actionName, true);
        editor.apply();
    }

    @Override
    public boolean requestListNeedsHint() {
//        return true;
        return getUserExperienceCount() < 10;
    }

    @Override
    public boolean tripListNeedsHint() {
//        return true;
        return getUserExperienceCount() < 10;
    }

    @Override
    public boolean aboutPaneNeedsHint() {
//        return true;
        return getUserExperienceCount() >= 10 && !userKnows(TUTORIAL_USER_KNOWS_ABOUT_PANE);
    }

    @Override
    public void userOpenedApp() {
        SharedPreferences preferences = c.getSharedPreferences(TUTORIAL_PREFERENCES_NAME, 0);

        SharedPreferences.Editor editor = preferences.edit();

        int timesOpened = preferences.getInt(USER_EXPERIENCE_COUNT, 0);
        timesOpened++;
        editor.putInt(USER_EXPERIENCE_COUNT, timesOpened);

        editor.apply();
    }

    public int getUserExperienceCount() {
        SharedPreferences preferences = c.getSharedPreferences(TUTORIAL_PREFERENCES_NAME, 0);
        int timesOpened = preferences.getInt(USER_EXPERIENCE_COUNT, 0);

        return timesOpened;
    }

    @Override
    public void aboutPaneOpened() {
        aboutPaneHint.setVisibility(View.INVISIBLE);

        setUserHasDone(TUTORIAL_USER_KNOWS_ABOUT_PANE);
    }


    @Override
    public void notifyServiceSet() {
        setUserHasDone(TUTORIAL_USER_KNOWS_NOTIFY_SERVICE);
    }

    @Override
    public void tripDismissalUndone() {
        setUserHasDone(TUTORIAL_USER_KNOWS_UNDO_DISMISS);
    }
}

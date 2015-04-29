package com.remulasce.lametroapp.components.tutorial;

import com.remulasce.lametroapp.java_core.analytics.Log;

/**
 * Created by Remulasce on 4/3/2015.
 *
 * We display 'how to' messages to teach new users how to do things.
 * They're kind of annoying.
 *
 * So this thing is supposed to figure out whether a tutorial is actually needed.
 *
 * Also this helps platform-abstract the tutorials.
 * Even though it's totally not actually going to java_core.
 *
 * Singleton OK. Who cares about tutorial side effects? Nobody. Except for the tutorial itself.
 * The actual UI bit is platform-dependent.
 *
 * So we fall into the singleton-does-nothing-unless-platform-does-something pattern.
 * Catchy name, huh?
 *
 * (extend TutorialManager with platform-specific features. Then set the static tutorialmanager
 * to be that manager upon app load).
 *
 */
public class TutorialManager {
    private static final String TAG = "TutorialManager";

    private static TutorialManager tutorialManager;
    public static TutorialManager getInstance() {
        if (tutorialManager == null) {
            tutorialManager = new TutorialManager();
        }

        return tutorialManager;
    }

    // Presumably you'd give an extended, platform-specific TutorialManager that can actually
    // present tutorials.
    public static void setTutorialManager(TutorialManager customManager) {
        tutorialManager = customManager;
    }

    /* Tutorial text options:
      "Tap arrival to start notification"
      "Swipe to dismiss"
      "Undo dismissal"

      For each of 3, don't show if each's action has been performed.

      So once user has dismissed any trip, you shouldn't show the swipe-dismiss tutorial any more.
      */
    public void tripsNewlyShown() {
        Log.w(TAG, "tripsNewlyShown not implemented");
    }

    public void tripDismissed() {
        Log.w(TAG, "tripDismissed not implemented");
    }

    public void notifyServiceSet() {
        Log.w(TAG, "notifyServiceSet not implemented");
    }

    public void tripDismissalUndone() {
        Log.w(TAG, "tripDismissalUndone");
    }

    public void aboutPaneOpened() {
        Log.w(TAG, "aboutPaneOpened");
    }

    public boolean requestListNeedsHint() {
        Log.w(TAG, "requestListNeedsHint");
        return true;
    }

    public boolean tripListNeedsHint() {
        Log.w(TAG, "tripListNeedsHint");
        return true;
    }

    public boolean aboutPaneNeedsHint() {
        Log.w(TAG, "aboutPaneNeedsHint");
        return true;
    }

    public void userOpenedApp() {
        Log.w(TAG, "userOpenedApp");
    }
}

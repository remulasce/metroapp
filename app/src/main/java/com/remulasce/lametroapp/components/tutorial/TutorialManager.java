package com.remulasce.lametroapp.components.tutorial;

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


    private static TutorialManager tutorialManager;
    public static void getInstance() {
        if (tutorialManager == null) {
            tutorialManager = new TutorialManager();
        }
    }

    // Presumably you'd give an extended, platform-specific TutorialManager that can actually
    // present tutorials.
    public static void setTutorialManager(TutorialManager customManager) {
        tutorialManager = customManager;
    }

    

}

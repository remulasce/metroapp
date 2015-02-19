package com.remulasce.lametroapp.analytics;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Remulasce on 12/13/2014.
 *
 * This is a background task that reads logcat output and writes it to file.
 * That way I can peruse the output offline later.
 */
public class Logging {

    private static RunTaskAble logger = null;

    public static void StartSavingLogcat(final Context context) {
        Log.d("LogSaver", "LogSaver starting");
        if (logger != null) {
            Log.w("LogSaver", "LogSaver tried to start but already started!");
            return;
        }

        logger = new RunTaskAble() {
            public boolean run = true;
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec("logcat -d");

                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    File root = Environment.getExternalStorageDirectory();
                    File dir = new File (root.getAbsolutePath() + "/metroapp/log");
                    dir.mkdirs();
                    File logFile = new File(dir, "logcatLog"+(System.currentTimeMillis()/1000)+".txt");

                    logFile.createNewFile();

                    FileOutputStream fileOutputStream = new FileOutputStream(logFile);
                    BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream);

                    String line;
                    while (run) {
                        if ((line = bufferedReader.readLine()) != null) {
                            stream.write((line+"\n").getBytes());
                        }
                    }

                    stream.close();
                    Log.d("LogSaver", "LogSaver Exiting");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                logger = null;
            }

            @Override
            public void stopRunning() {
                run = false;
            }
        };

        new Thread(logger, "Logcat Saver").start();
    }

    public static void StopSavingLogcat() {
        Log.d("LogSaver", "LogSaver stopping");
        if (logger == null) {
            Log.w("LogSaver", "LogSaver tried to stop but was not started");
            return;
        }
        logger.stopRunning();
    }

    private interface RunTaskAble extends Runnable {
        public void stopRunning();
    }
}

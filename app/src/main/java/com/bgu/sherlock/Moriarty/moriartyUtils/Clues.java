package com.bgu.sherlock.Moriarty.moriartyUtils;

import static android.content.ContentValues.TAG;

import com.bgu.sherlock.Moriarty.SGTPuzzles;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by simondzn on 26/11/2015.
 */
public class Clues {
    String extend;
    FileWriter logger;
    JsonObject data;
    JsonArray array;
    File clueFile;

//    public Clues(String TAG, String extend) {

    public void SendLog(String TAG, String extend) {
        File StorageDir = MoriaryService.getDir();
        clueFile = new File(StorageDir, "MoriartyClues.txt");
        data = new JsonObject();
        data.addProperty("Action", TAG);
        data.addProperty("ActionType", "benign");
        data.addProperty("Details", extend);
        data.addProperty("UUID", System.currentTimeMillis());
        if(SGTPuzzles.isEvil!=null&& SGTPuzzles.isEvil){
            data.addProperty("SessionType", "malicious");
        }else data.addProperty("SessionType", "benign");
        data.addProperty("Version", SGTPuzzles.version );
        data.addProperty("SessionID", SGTPuzzles.sessionID);
        try {
            logger = new FileWriter(clueFile, true);
            if(clueFile.length()>0){
                logger.write("," + data.toString());
                logger.close();
            }else{
                logger.write(data.toString());
                logger.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void SendLog(String TAG, String extend, String actionType){

        File StorageDir = MoriaryService.getDir();
        clueFile = new File(StorageDir, "MoriartyClues.txt");
        data = new JsonObject();
        data.addProperty("Action", TAG);
        data.addProperty("ActionType", actionType);
        data.addProperty("Details", extend);
        data.addProperty("UUID", System.currentTimeMillis());
        if(SGTPuzzles.isEvil!=null&& SGTPuzzles.isEvil){
            data.addProperty("SessionType", "malicious");
        }else data.addProperty("SessionType", "benign");
        data.addProperty("Version", SGTPuzzles.version );
        data.addProperty("SessionID", SGTPuzzles.sessionID);
        try {
            logger = new FileWriter(clueFile, true);
            if(clueFile.length()>0){
                logger.write("," + data.toString());
                logger.close();
            }else{
                logger.write(data.toString());
                logger.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
public void writeToFile(){
    try {
        logger = new FileWriter(clueFile, true);
            logger.write(TAG + " " + extend + " at: " + System.currentTimeMillis() + "\n");
        logger.write(array.toString());
        logger.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}

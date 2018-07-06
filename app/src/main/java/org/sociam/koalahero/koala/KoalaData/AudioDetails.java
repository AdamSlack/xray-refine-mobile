package org.sociam.koalahero.koala.KoalaData;

import android.util.Base64;

import com.google.android.gms.common.util.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sociam.koalahero.PreferenceManager.PreferenceManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

public class AudioDetails extends JSONData {
    public String studyID;
    public String fileID;
    public Date date;
    public long fileSize;
    public long length;
    public String filePath;

    public AudioDetails() {
        this.studyID = "";
        this.fileID = "";
        this.date = new Date();
        this.fileSize = -1;
        this.length = -1;
        this.filePath = "";
    }

    private byte[] readFileAsBytes(String fp) {
        File file = new File(fp);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            bytes = IOUtils.toByteArray(new FileInputStream(file));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public JSONObject toJSONData() throws JSONException {
        JSONObject json= new JSONObject();
        json.put("study_id", this.studyID);
        json.put("date" , this.date.toString());
        json.put("file_size", this.fileSize);
        json.put("length", this.length);


        byte[] fileData = Base64.encode(this.readFileAsBytes(this.filePath), Base64.DEFAULT);

        json.put("file_data", new String(fileData));
        return json;
    }
}

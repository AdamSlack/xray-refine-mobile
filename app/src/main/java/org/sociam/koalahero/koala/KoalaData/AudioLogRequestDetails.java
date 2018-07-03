package org.sociam.koalahero.koala.KoalaData;

import android.util.Base64;

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

public class AudioLogRequestDetails extends JSONData {
    public String studyID;
    public String fileID;
    public Date date;
    public long fileSize;
    public long length;
    public String filePath;

    public AudioLogRequestDetails() {
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
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
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
        json.put("file", Base64.encodeToString(this.readFileAsBytes(this.filePath), Base64.NO_WRAP + Base64.URL_SAFE));
        return json;
    }
}

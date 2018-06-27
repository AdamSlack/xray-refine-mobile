package org.sociam.koalahero.JsonParsers;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.util.ArrayList;

public class JsonArrayParser {

    public static ArrayList<String> parseStringArrayList(JsonReader jsonReader) {
        ArrayList<String> strings = new ArrayList<String>();
        try {
            if(jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray();
                while(jsonReader.hasNext()) {
                    strings.add(jsonReader.nextString());
                }
                jsonReader.endArray();
            }
        }
        catch (IOException exc) {

        }
        return strings;
    }

}

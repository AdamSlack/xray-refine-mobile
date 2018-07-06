package org.sociam.koalahero.koala;

import android.util.JsonReader;

import org.sociam.koalahero.koala.KoalaData.SuccessResponse;
import org.sociam.koalahero.koala.KoalaData.TokenResponse;

import java.io.IOException;

public class KoalaJsonParser {

    public SuccessResponse parseSuccessResponse(JsonReader jr) {
        SuccessResponse res = new SuccessResponse();
        try {
            jr.beginObject();
            while(jr.hasNext()) {
                String name = jr.nextName();
                if(name.equals("success")) {
                    res.success = jr.nextString();
                }
                else {
                    jr.skipValue();
                }
            }
            jr.endObject();

        }
        catch (IOException exc) {

        }
        return res;
    }

    public TokenResponse parseTokenResponse(JsonReader jr) {
        TokenResponse token = new TokenResponse();
        try {
            jr.beginObject();
            while(jr.hasNext()) {
                String name = jr.nextName();
                if(name.equals("expires")) {
                    token.expires = jr.nextString();
                }
                else if(name.equals("token")) {
                    token.token = jr.nextString();
                }
                else {
                    jr.skipValue();
                }
            }
            jr.endObject();
        }
        catch (IOException exc) {

        }
        return token;
    }
}

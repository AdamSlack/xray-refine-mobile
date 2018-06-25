package org.sociam.koalahero.koala;

import android.util.JsonReader;

import java.io.IOException;

public class KoalaJsonParser {

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
        }
        catch (IOException exc) {

        }
        return token;
    }
}

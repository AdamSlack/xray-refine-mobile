package org.sociam.koalahero.koala;

public class TokenResponse {
    public String expires;
    public String token;

    public TokenResponse() {
        expires = "";
        token = "";
    }

    public TokenResponse(String expires, String token) {
        this.expires = expires;
        this.token = token;
    }
}

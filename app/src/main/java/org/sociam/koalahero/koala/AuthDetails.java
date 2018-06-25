package org.sociam.koalahero.koala;

public class AuthDetails {
    public String email;
    public String token;

    public AuthDetails() {
        this.email = "";
        this.token = "";
    }

    public AuthDetails(String email, String token) {
        this.email = email;
        this.token = token;
    }
}

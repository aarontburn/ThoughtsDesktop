package com.beanloaf.thoughtsdesktop.database;

public record ThoughtUser(String localId, String email, String displayName, String idToken, boolean registered,
                          String refreshToken, String expiresIn) {


}

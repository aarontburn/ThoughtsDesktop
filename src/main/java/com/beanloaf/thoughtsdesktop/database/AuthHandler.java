package com.beanloaf.thoughtsdesktop.database;

import com.beanloaf.thoughtsdesktop.handlers.Logger;
import com.google.common.io.BaseEncoding;

import org.apache.commons.codec.binary.Base32;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public final class AuthHandler {

    private AuthHandler() {
        throw new RuntimeException("Should not be instantiated");
    }

    private static final String KEY = "IFEXUYKTPFATGSCXPFHE6OC2LBSEG4JYIJGDC4RNIFWXUUS7L5HEOM2CJ4YWYMA";

    private static final Base32 b32 = new Base32();

    public static ThoughtUser signIn(final String email, final String password) {


        try {
            final URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + new String(b32.decode(KEY)));


            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            final Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("email", email);
            requestMap.put("password", password);
            requestMap.put("returnSecureToken", true);

            final OutputStream outputStream = connection.getOutputStream(); // error here
            outputStream.write(new JSONObject(requestMap).toString().getBytes());
            outputStream.flush();
            outputStream.close();

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }


            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(new StringReader(responseBuilder.toString()));

            final String userId = (String) json.get("localId");
            final String userEmail = (String) json.get("email");
            final String displayName = (String) json.get("displayName");
            final String idToken = (String) json.get("idToken");
            final boolean registered = (Boolean) json.get("registered");
            final String refreshToken = (String) json.get("refreshToken");
            final String expiresIn = (String) json.get("expiresIn");

            return new ThoughtUser(userId, userEmail, displayName, idToken, registered, refreshToken, expiresIn);


        } catch (Exception e) {
            Logger.logException(e);
        }

        return null;

    }

    public static ThoughtUser signUp(final String userName,
                                     final String email,
                                     final String password) {
        try {
            final URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + new String(b32.decode(KEY)));

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            final Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("email", email);
            requestMap.put("password", password);
            requestMap.put("returnSecureToken", true);

            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(new JSONObject(requestMap).toString().getBytes());
            outputStream.flush();
            outputStream.close();


            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }


            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(new StringReader(responseBuilder.toString()));

            final String userId = (String) json.get("localId");
            final String userEmail = (String) json.get("email");
            final String idToken = (String) json.get("idToken");
            final String refreshToken = (String) json.get("refreshToken");
            final String expiresIn = (String) json.get("expiresIn");

            return new ThoughtUser(userId,
                    userEmail,
                    setUsername(userName, idToken),
                    idToken,
                    true,
                    refreshToken,
                    expiresIn);


        } catch (Exception e) {
            Logger.logException(e);
        }

        return null;
    }

    public static String setUsername(final String userName, final String token) {
        try {
            final URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:update?key=" + new String(b32.decode(KEY)));

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            final Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("idToken", token);
            requestMap.put("displayName", userName);
            requestMap.put("returnSecureToken", true);

            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(new JSONObject(requestMap).toString().getBytes());
            outputStream.flush();
            outputStream.close();

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }

            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(new StringReader(responseBuilder.toString()));

            return (String) json.get("displayName");


        } catch (Exception e) {
            Logger.logException(e);
        }
        return "";
    }


    public static boolean sendPasswordResetLink(final String email) {
        try {
            final URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" + new String(b32.decode(KEY)));
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);


            final Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("requestType", "PASSWORD_RESET");
            requestMap.put("email", email);

            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(new JSONObject(requestMap).toString().getBytes());
            outputStream.flush();
            outputStream.close();

            // Check the response code
            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Logger.log("Password reset email sent successfully!");
            } else {
                Logger.log("Failed to send password reset email. Response code: " + responseCode);
                return false;
            }

            return true;


        } catch (Exception e) {
            Logger.logException(e);

        }

        return false;

    }


    public static String sp(final String p, final boolean d) {
        if (!d) {
            try {
                final String ch = "ABCDEFGHIJKLNOPRSTUVWXYZabcdefghjklmnopqrstuvxyz01245678";
                final StringBuilder s = new StringBuilder(16);

                final SecureRandom random = new SecureRandom();
                for (int i = 0; i < s.capacity(); i++) {
                    s.append(ch.charAt(random.nextInt(ch.length())));
                }

                return BaseEncoding.base32().encode((BaseEncoding.base32().encode(p.getBytes()) + s).getBytes());

            } catch (Exception e) {
                return null;
            }
        }
        return new String(b32.decode(r(b32.decode(p))));
    }


    public static byte[] r(final byte[] o) {
        final byte[] r = new byte[o.length - 16];
        System.arraycopy(o, 0, r, 0, o.length - 16);
        return r;
    }

}

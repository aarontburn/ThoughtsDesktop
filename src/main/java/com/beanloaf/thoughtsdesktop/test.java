package com.beanloaf.thoughtsdesktop;

import com.beanloaf.thoughtsdesktop.calendar.objects.DayEvent;
import com.beanloaf.thoughtsdesktop.handlers.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

public class test {

    private final static String TOKEN = "10~SwouCwH1YXP16vSxKZDMVYCWDO2Jc8zcPFXaR64rTBteJ1dQFNxxXCPLugTxYFjU";
    private final static String apiURL = "https://canvas.instructure.com/api/v1/calendar_events";


    public static void main(String[] args) {


//        try {
//            final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
//            connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
//            connection.setRequestProperty("type", "event");
//            connection.setRequestProperty("start_date", "2023-09-13");
//            connection.setDoOutput(true);
//
//
//            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            final StringBuilder responseBuilder = new StringBuilder();
//            String line;
//            while ((line = responseReader.readLine()) != null) {
//                responseBuilder.append(line);
//            }
//            responseReader.close();
//
//            final JSONArray jsonArray = (JSONArray) new JSONParser().parse(new StringReader(responseBuilder.toString()));
//
//
//            final JSONObject json = (JSONObject) jsonArray.get(0);
//            Logger.log(json.get("title"));
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }





    }









}

package me.harshithgoka.socmed.Misc;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by akashtrehan on 03/10/17.
 */

public class Utils {

    public static final String TAG = Utils.class.getName();

    public static JsonParser jsonParser = new JsonParser();

    public static String getQuery(List<AbstractMap.SimpleEntry> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (AbstractMap.SimpleEntry pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode((String) pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static JsonObject getAndParse(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream in = new BufferedInputStream(inputStream)) {
            int nbytes;
            byte[] bytes = new byte[1024];
            while ((nbytes = in.read(bytes, 0, 1024)) != -1) {
                stringBuilder.append(new String(bytes, 0, nbytes));
            }
        } catch (IOException e) {
            Log.d("PARSING", e.toString());
        }

        return jsonParser.parse(stringBuilder.toString()).getAsJsonObject();
    }

    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;

        long elapsedWeeks = different / weeksInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedWeeks > 0) {
            return elapsedWeeks + "w";
        }
        else if (elapsedDays > 0) {
            return elapsedDays + "d";
        }
        else if (elapsedHours > 0) {
            return elapsedHours + "h";
        }
        else if (elapsedMinutes > 0) {
            return elapsedMinutes + "m";
        }
        else if (elapsedSeconds > 0) {
            return elapsedSeconds + "s";
        }
        else {
            return "0s";
        }
    }

    public static String convertTimestamp (String strtimestamp) {
        String strTimestamp = strtimestamp.split("\\.")[0];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(strTimestamp);
            return printDifference(date, Calendar.getInstance().getTime()) + " ago";
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            Log.d(TAG, e.toString());
            return "Random";
        }

    }
}

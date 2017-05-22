package mx.unam.passlist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ivan on 20/05/2017.
 */

/**
 * Class with helper methods to manipulate JSON
 */
public class JSONBuilder {
    public static final JSONObject buildUserRegistration(String email, String firstName, String lastName, String motherLastName,
                                                         String password, String passwordConfirmation) {
        /** Create a JSON object like the following
         {
             "user": {
                 "email": "example@email.com",
                 "first_name": "John",
                 "last_name": "Doe",
                 "maiden_name": "M.",
                 "password": "password",
                 "password_confirmation": "password"
             }
         }
         */
        JSONObject jsonObject = new JSONObject();
        JSONObject userData = new JSONObject();
        try {
            userData.put("email", email);
            userData.put("first_name", firstName);
            userData.put("last_name", lastName);
            userData.put("maiden_name", motherLastName);
            userData.put("password", password);
            userData.put("password_confirmation", passwordConfirmation);
            jsonObject.put("user", userData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static JSONObject buildUserCredentials(String email, String password) {
        /** Create a JSON object like the following
         {
             "user": {
                 "email": "example@email.com",
                 "password": "password"
             }
         }
         */
        JSONObject jsonObject = new JSONObject();
        JSONObject userCredentials = new JSONObject();
        try {
            userCredentials.put("email", email);
            userCredentials.put("password", password);
            jsonObject.put("user", userCredentials);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject buildGroup(String name, String subject, String beginDate, String endDate, JSONArray classDays) {
        /** Create a JSON object like the following
         {
             "group": {
                 "name": "2101",
                 "subject": "Programacion 2",
                 "begin_date": "2016-02-01",
                 "end_date": "2016-05-27",
                 "classdays_attributes": [
                     {
                         "day": "Lunes",
                         "begin_time": "08:00",
                         "end_time": "10:00"
                     }
                 ]
             }
         }
         */
        JSONObject jsonObject = new JSONObject();
        JSONObject groupData = new JSONObject();
        try {
            groupData.put("name", name);
            groupData.put("subject", subject);
            groupData.put("begin_date", beginDate);
            groupData.put("end_date", endDate);
            groupData.put("classdays_attributes", classDays);
            jsonObject.put("group", groupData);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONArray buildJSONArrayFromClassDays(String[][] classDaysArray) {
        /** Create a JSON Arrau like the following
         [
             {
                 "day": "Lunes",
                 "begin_time": "08:00",
                 "end_time": "10:00"
             },
             {
                 "day": "Viernes",
                 "begin_time": "08:00",
                 "end_time": "10:00"
             }
         ]
         */
        JSONArray jsonArray = new JSONArray();
        try {
            for(String[] classDay : classDaysArray) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("day", classDay[0]);
                jsonObject.put("begin_time", classDay[1]);
                jsonObject.put("end_time", classDay[2]);
                jsonArray.put(jsonObject);
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    // Returns a string with all the messages inside of the "full_messages" array
    // { "errors": "full_messages": [] }
    public static String getStringFromErrorMessages(String errors) {
        String errorMessagesStr = "";
        try {
            JSONObject jsonErrors = new JSONObject(errors);
            JSONArray errorMessages = jsonErrors.getJSONObject("errors").getJSONArray("full_messages");
            errorMessagesStr = joinJSONStringArray(errorMessages);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return errorMessagesStr;
    }

    // Returns a string with all the messages inside of the "errors" array
    // { "errors": [] }
    public static String getStringFromErrors(String errors) {
        String errorMessagesStr = "";
        try {
            JSONObject jsonErrors = new JSONObject(errors);
            JSONArray errorMessages = jsonErrors.getJSONArray("errors");
            errorMessagesStr = joinJSONStringArray(errorMessages);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return errorMessagesStr;
    }

    // Returns a joined string array by a newline
    // ["1", "2", "3"] => "1\n2\n3"
    private static String joinJSONStringArray(JSONArray jsonArray) {
        StringBuilder stringBuilder = new StringBuilder("");
        if(jsonArray.length() > 0) {
            try {
                stringBuilder.append(jsonArray.getString(0));
                for (int i = 1; i < jsonArray.length(); i++) {
                    stringBuilder.append("\n").append(jsonArray.getString(i));
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}

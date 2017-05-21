package mx.unam.passlist;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ivan on 20/05/2017.
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
}

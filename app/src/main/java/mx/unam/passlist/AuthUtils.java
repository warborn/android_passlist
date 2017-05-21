package mx.unam.passlist;

import android.content.SharedPreferences;
import com.androidnetworking.common.RequestBuilder;

import okhttp3.Response;

/**
 * Created by Ivan on 20/05/2017.
 */

/**
 * Class that contains helper methods used in the process of authentication
 */
public class AuthUtils {
    private static SharedPreferences preferences;

    // Save the headers of the given HTTP response into a shared preference
    public static final void saveAuthHeadersToPreferences(Response okHttpResponse, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        // Save the returned headers into a shared preference

        // unique token that serves as the user's password
        editor.putString("Access-Token", okHttpResponse.header("Access-Token"));
        // unique value that is used to identify the user
        editor.putString("Uid", okHttpResponse.header("Uid"));
        // unique value that enables the use of multiple simultaneous sessions on different clients
        editor.putString("Client", okHttpResponse.header("Client"));
        // the date at which the current session will expire
        editor.putString("Expiry", okHttpResponse.header("Expiry"));
        editor.apply();
    }

    // Add the stored header values to a FastAndroidNetworking object
    // This allows the request to be recognized as sent by an authenticated user or not
    public static final RequestBuilder addAuthHeaders(RequestBuilder androidNetworking) {
        androidNetworking
            .addHeaders("Access-Token", preferences.getString("Access-Token", ""))
            .addHeaders("Uid", preferences.getString("Uid", ""))
            .addHeaders("Client", preferences.getString("Client", ""))
            .addHeaders("Expiry", preferences.getString("Expiry", ""));
        return androidNetworking;
    }

    public static void setPreferences(SharedPreferences sharedPreferences) {
        preferences = sharedPreferences;
    }
}

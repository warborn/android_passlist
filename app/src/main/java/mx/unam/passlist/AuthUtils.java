package mx.unam.passlist;

import android.content.SharedPreferences;
import com.androidnetworking.common.RequestBuilder;

import okhttp3.Response;

/**
 * Created by Ivan on 20/05/2017.
 */

public class AuthUtils {
    private static SharedPreferences preferences;

    public static final void saveAuthHeadersToPreferences(Response okHttpResponse, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        // save the returned token into a shared preference
        editor.putString("Access-Token", okHttpResponse.header("Access-Token"));
        editor.putString("Uid", okHttpResponse.header("Uid"));
        editor.putString("Client", okHttpResponse.header("Client"));
        editor.putString("Expiry", okHttpResponse.header("Expiry"));
        editor.apply();
    }

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

package mx.unam.passlist;

import android.content.SharedPreferences;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.common.RequestBuilder;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by Ivan on 20/05/2017.
 */

public class AuthUtils {
    private static boolean stillValidHeaders;

    public static final void saveHeadersToPreferences(Response okHttpResponse, SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        // save the returned token into a shared preference
        editor.putString("Access-Token", okHttpResponse.header("Access-Token"));
        editor.putString("Uid", okHttpResponse.header("Uid"));
        editor.putString("Client", okHttpResponse.header("Client"));
        editor.putString("Expiry", okHttpResponse.header("Expiry"));
        editor.apply();
    }



    public static void validateHeaders(SharedPreferences preferences, JSONObjectRequestListener listener) {
        ANRequest.GetRequestBuilder androidNetworking = (ANRequest.GetRequestBuilder)
                addAuthHeaders(AndroidNetworking.get("https://unam-passlist.herokuapp.com/auth/validate_token"), preferences);

        androidNetworking.setTag("validate_token")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(listener);
    }

    private static final RequestBuilder addAuthHeaders(RequestBuilder androidNetworking, SharedPreferences preferences) {
        androidNetworking
            .addHeaders("Access-Token", preferences.getString("Access-Token", ""))
            .addHeaders("Uid", preferences.getString("Uid", ""))
            .addHeaders("Client", preferences.getString("Client", ""))
            .addHeaders("Expiry", preferences.getString("Expiry", ""));
        return androidNetworking;
    }
}

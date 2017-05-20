package mx.unam.passlist;

import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONObject;

/**
 * Created by Ivan on 20/05/2017.
 */

public final class PasslistService {
    private static final String API_URL = "https://unam-passlist.herokuapp.com";
    private static final String SIGNIN_URL = API_URL + "/auth/sign_in";
    private static final String VALIDATE_TOKEN_URL = API_URL + "/auth/validate_token";

    public static final void login(String email, String password, OkHttpResponseAndJSONObjectRequestListener requestListener) {
        JSONObject credentials = AuthUtils.createJsonCredentials(email, password);
        // Send a HTTP post request to the sign_in API endpoint
        AndroidNetworking.post(SIGNIN_URL)
                .addJSONObjectBody(credentials)
                .setTag("sign_in")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponseAndJSONObject(requestListener);
    }

    public static final void validateToken(SharedPreferences preferences, JSONObjectRequestListener requestListener) {
        AuthUtils.setPreferences(preferences);
        ANRequest.GetRequestBuilder androidNetworking = (ANRequest.GetRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.get(VALIDATE_TOKEN_URL));

        androidNetworking.setTag("validate_token")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(requestListener);
    }
}

package mx.unam.passlist;

import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 20/05/2017.
 */

public final class PasslistService {
    private static final String API_URL = "https://unam-passlist.herokuapp.com";
    private static final String SIGNIN_URL = API_URL + "/auth/sign_in";
    private static final String SIGNUP_URL = API_URL + "/auth";
    private static final String VALIDATE_TOKEN_URL = API_URL + "/auth/validate_token";
    private static final String GROUPS_URL = API_URL + "/groups";
    private static final String GROUP_URL = API_URL + "/groups/{id}";
    private static final String CLASS_URL = API_URL + "/classes/{id}";
    private static final String MARK_STUDENT_AS_ASSISTANCE = API_URL + "/classes/{class_id}/students/{student_id}/assist";

    public static final void login(String email, String password, OkHttpResponseAndJSONObjectRequestListener requestListener) {
        JSONObject credentials = JSONBuilder.buildUserCredentials(email, password);
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

    public static final void registerUser(String email, String firstName, String lastName, String motherLastName,
                                          String password, String passwordConfirmation,
                                          JSONObjectRequestListener requestListener) {

        JSONObject jsonUser = JSONBuilder.buildUserRegistration(email, firstName, lastName, motherLastName,
                                                                password, passwordConfirmation);

        ANRequest.PostRequestBuilder androidNetworking = (ANRequest.PostRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.post(SIGNUP_URL));

        androidNetworking.setTag("sign_up")
                .addJSONObjectBody(jsonUser)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(requestListener);
    }

    public static final void getGroups(JSONArrayRequestListener requestListener) {
        ANRequest.GetRequestBuilder androidNetworking = (ANRequest.GetRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.get(GROUPS_URL));

        androidNetworking.setTag("get_groups")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(requestListener);
    }

    public static final void getGroup(String id, JSONObjectRequestListener requestListener) {
        String groupURL = injectIdInUrl(GROUP_URL, id);
        ANRequest.GetRequestBuilder androidNetworking = (ANRequest.GetRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.get(groupURL));

        androidNetworking.setTag("get_group")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(requestListener);
    }

    public static final void getClass(String id, JSONObjectRequestListener requestListener) {
        String groupURL = injectIdInUrl(CLASS_URL, id);
        ANRequest.GetRequestBuilder androidNetworking = (ANRequest.GetRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.get(groupURL));

        androidNetworking.setTag("get_class")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(requestListener);
    }

    public static final void markAssistance(String classId, String studentId, JSONObjectRequestListener requestListener) {
        String markStudentAssistanceUrl = injectClassIdInUrl(MARK_STUDENT_AS_ASSISTANCE, classId);
        markStudentAssistanceUrl = injectStudentIdInUrl(markStudentAssistanceUrl, studentId);

        ANRequest.PatchRequestBuilder androidNetworking = (ANRequest.PatchRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.patch(markStudentAssistanceUrl));
        androidNetworking
                .setTag("assist")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(requestListener);
    }

    private static final String injectIdInUrl(String urlPattern, String id) {
        return replacePattern(urlPattern, id, "id");
    }

    private static final String injectClassIdInUrl(String urlPattern, String id) {
        return replacePattern(urlPattern, id, "class_id");
    }

    private static final String injectStudentIdInUrl(String urlPattern, String id) {
        return replacePattern(urlPattern, id, "student_id");
    }

    private static final String replacePattern(String str, String value, String pattern) {
        Pattern p = Pattern.compile("\\{" + pattern + "\\}", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        return m.replaceAll(value);
    }
}

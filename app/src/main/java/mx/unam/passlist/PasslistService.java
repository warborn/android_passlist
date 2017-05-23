package mx.unam.passlist;

import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 20/05/2017.
 */

/**
 * Service class to interact with the Passlist API at https://unam-passlist.herokuapp.com
 * This class is the one in charge to send HTTP Request to the API, it has wrapper methods to send specific requests like
 *      * Login a user
 *      * Validate the stored token of the user in the shared preferences to know if it's still a valid token
 *      * Register a new user
 *      * Create a new group for the current logged in user
 *      * Get all the groups of the current logged in user
 *      * Get one group information and a list of the school class days (the calendar)
 *      * Get one class day information and a list of the students in that class to allow the user to mark students assistance
 *      * Toggle a student's assistance between true and false
 *
 */
public final class PasslistService {
    private static final String API_URL = "https://unam-passlist.herokuapp.com";
    private static final String SIGNIN_URL = API_URL + "/auth/sign_in";
    private static final String SIGNUP_URL = API_URL + "/auth";
    private static final String LOGOUT_URL = API_URL + "/auth/sign_out";
    private static final String VALIDATE_TOKEN_URL = API_URL + "/auth/validate_token";
    private static final String GROUPS_URL = API_URL + "/groups";
    private static final String GROUP_URL = API_URL + "/groups/{id}";
    private static final String CLASS_URL = API_URL + "/classes/{id}";
    private static final String MARK_STUDENT_AS_ASSISTANCE = API_URL + "/classes/{class_id}/students/{student_id}/assist";
    private static final String IMPORT_STUDENTS_URL = API_URL + GROUP_URL + "/students/import";

    // Attempt a login using the given email and password
    public static final void login(String email, String password, OkHttpResponseAndJSONObjectRequestListener requestListener) {
        JSONObject credentials = JSONBuilder.buildUserCredentials(email, password);
        // Send a POST request to the sign_in API endpoint
        AndroidNetworking.post(SIGNIN_URL)
                .addJSONObjectBody(credentials)
                .setTag("sign_in")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponseAndJSONObject(requestListener);
    }

    public static final void logout(JSONObjectRequestListener requestListener) {
        ANRequest.DeleteRequestBuilder androidNetworking = (ANRequest.DeleteRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.delete(LOGOUT_URL));

        androidNetworking.setTag("validate_token")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(requestListener);
    }

    // Validate the stored token, UID, client (client consuming the API) and expiry time
    // to know if the user will need to login again or not
    public static final void validateToken(SharedPreferences preferences, JSONObjectRequestListener requestListener) {
        AuthUtils.setPreferences(preferences);
        ANRequest.GetRequestBuilder androidNetworking = (ANRequest.GetRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.get(VALIDATE_TOKEN_URL));

        androidNetworking.setTag("validate_token")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(requestListener);
    }

    // Register a new user based on the given information
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

    // Create a new group with the given group information and school class days
    public static final void createGroup(String name, String subject, String beginDate, String endDate,
                                         JSONArray classDays, JSONObjectRequestListener requestListener) {
        JSONObject jsonGroup = JSONBuilder.buildGroup(name, subject, beginDate, endDate, classDays);

        ANRequest.PostRequestBuilder androidNetworking = (ANRequest.PostRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.post(GROUPS_URL));

        androidNetworking.setTag("create_group")
                .addJSONObjectBody(jsonGroup)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(requestListener);
    }

    // Get the groups information owned by the current logged in user
    public static final void getGroups(JSONArrayRequestListener requestListener) {
        ANRequest.GetRequestBuilder androidNetworking = (ANRequest.GetRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.get(GROUPS_URL));

        androidNetworking.setTag("get_groups")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(requestListener);
    }

    // Get the information of a single group by it's ID, along with the list of school classdays
    public static final void getGroup(String id, JSONObjectRequestListener requestListener) {
        String groupURL = injectIdInUrl(GROUP_URL, id);
        ANRequest.GetRequestBuilder androidNetworking = (ANRequest.GetRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.get(groupURL));

        androidNetworking.setTag("get_group")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(requestListener);
    }

    // Get the information of a single class day by it's ID, including the list of students
    public static final void getClass(String id, JSONObjectRequestListener requestListener) {
        String groupURL = injectIdInUrl(CLASS_URL, id);
        ANRequest.GetRequestBuilder androidNetworking = (ANRequest.GetRequestBuilder)
                AuthUtils.addAuthHeaders(AndroidNetworking.get(groupURL));

        androidNetworking.setTag("get_class")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(requestListener);
    }

    // Toggle the assistance of a student between true and false, given the class and student ID
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

    public static final void importStudents(String groupId, File file, JSONObjectRequestListener requestListener) {
        String importStudentsURL = injectIdInUrl(IMPORT_STUDENTS_URL, groupId);
        AndroidNetworking.upload(IMPORT_STUDENTS_URL)
                .addMultipartFile("student_import[file]",file)
                .setTag("import_students")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(requestListener
                );
    }

    // Replace the {id} pattern in a string with an actual value
    private static final String injectIdInUrl(String urlPattern, String id) {
        return replacePattern(urlPattern, id, "id");
    }

    // Replace the {class_id} pattern in a string with an actual value
    private static final String injectClassIdInUrl(String urlPattern, String id) {
        return replacePattern(urlPattern, id, "class_id");
    }

    // Replace the {student_id} pattern in a string with an actual value
    private static final String injectStudentIdInUrl(String urlPattern, String id) {
        return replacePattern(urlPattern, id, "student_id");
    }

    // Replace the {ANYTHING} pattern in a string with an actual value
    private static final String replacePattern(String str, String value, String pattern) {
        Pattern p = Pattern.compile("\\{" + pattern + "\\}", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        return m.replaceAll(value);
    }
}

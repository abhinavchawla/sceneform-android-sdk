package com.example.testapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final PhoneNumberUtil sPhoneNumberUtil = PhoneNumberUtil.getInstance();
    private final static String FCM_END_POINT_URL = "https://fcm.googleapis.com/fcm/send";
    private final static String API_KEY = "AIzaSyA1MShjL-IIhdN665X_GhYKMf0Gin7Pp6M";

    private final static String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    private final static String HEADER_AUTH_KEY = "Authorization";
    private final static String HEADER_CONTENT_TYPE_VALUE = "application/json";
    private static final String HEADER_AUTH_VALUE = "key=";

    private final static String PARAM_JSON_PAYLOAD_WITH_COMMA = ",\"%s\":\"%s\"";
    private final static String PARAM_JSON_PAYLOAD_FORMAT = "\"%s\":\"%s\"";
    private final static String PARAM_DATA_KEY = "data";
    private final static String PARAM_JSON_PAYLOAD_WITH_BRACKETS = "{%s}";
    private final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.test);
        tv.setText(formatToInternational("+919945563150","IN"));

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("paypal://three_ds_webview/?arg_threeds_pa_req=&arg_threeds_term_url=&arg_toolbar_title=Add card&arg_threeds_url=https://base64.ru/"));
                intent.putExtra("arg_threeds_background_theme", 1);
                intent.putExtra("allow_internal_deeplink", true); // added this
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        sendMockPushNotificationForRegPurchaseToGCM();


    }

    public static String formatToInternational(String phone, String regionCode) {
        String formattedPhoneNumber;
        try {
            Phonenumber.PhoneNumber phoneNumber = sPhoneNumberUtil.parseAndKeepRawInput(phone, regionCode.toUpperCase());
            formattedPhoneNumber = sPhoneNumberUtil.formatInOriginalFormat(phoneNumber, "IN");
        } catch (NumberParseException ex) {
            // phone number is invalid
            return null;
        }
        return formattedPhoneNumber;
    }

    private void sendMockPushNotificationToGCM(Map<String, String> pushNotificationData) {
        JSONObject jsonMockBody = getJSONMockBody(pushNotificationData);
        if (jsonMockBody == null) {
            Log.e(LOG_TAG, "Device Registration or data for push notification is null.");
            return;
        }

        //Response Listener for GCM request
        Response.Listener<JSONObject> gcmResponseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(LOG_TAG, "Response:" + jsonObject);
                Log.w(LOG_TAG, "GCM network call is successful.");
            }
        };

        //Error Listener for GCM request
        Response.ErrorListener gcmErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                throw new IllegalArgumentException("Error in calling GCM ::" + volleyError.getMessage());
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);


        JsonObjectRequest sendMoneyPushNotificationRequestJson = new JsonObjectRequest(
                Request.Method.POST,
                FCM_END_POINT_URL,
                jsonMockBody,
                gcmResponseListener, gcmErrorListener) {
            /**
             * Passing GCM request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put(HEADER_CONTENT_TYPE_KEY, HEADER_CONTENT_TYPE_VALUE);
                headers.put(HEADER_AUTH_KEY, HEADER_AUTH_VALUE + API_KEY);
                return headers;
            }
        };
        sendMoneyPushNotificationRequestJson.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        //Adding the Push Notification Network request to the Volley queue.
        queue.add(sendMoneyPushNotificationRequestJson);
    }

    public void sendMockPushNotificationForRegPurchaseToGCM() {
        sendMockPushNotificationToGCM(getPurchaseRegularPushNotificationPayLoad());
    }

    private static Map<String, String> getPurchaseRegularPushNotificationPayLoad() {
        Map<String, String> regularPurchasePayLoad = new HashMap<>();
        regularPurchasePayLoad.put("ET", "8");
        regularPurchasePayLoad.put("ES", "REG_PUR");
        regularPurchasePayLoad.put("loc_key_1", "$4.58");
        regularPurchasePayLoad.put("loc_key_2", "Central Cafe");
        regularPurchasePayLoad.put("EI", "purchaseActivityItem001");
        regularPurchasePayLoad.put("ID", "P7J83QDVFT6WY");
        return regularPurchasePayLoad;
    }

    private JSONObject getJSONMockBody(Map<String, String> pushNotificationData) {
        if (pushNotificationData == null) {
            return null;
        }
        String mDeviceRegistrationId = "fUG-evODKyI:APA91bGIHYA8HhdtLGnK3bLVE46ayEn3KjlAcTi1GqaW8NgWCq-B50rqMlHP9LhfvifefFVWZDmcxe7_wVWmRLxVR2xDL8YMxYzCN8rs8ot_lII6AtEaN5QTEANgWoXKa8Q3nt86pQE2";
        JSONObject jsonMockBody = new JSONObject();
        try {
            JSONArray jsonDeviceIds = new JSONArray();
            jsonDeviceIds.put(mDeviceRegistrationId);
            //Adding Device GCM registration Id to json Body
            jsonMockBody.put("registration_ids", jsonDeviceIds);
            //Building jsonPayloadString e.g. JSONObject jsonBody = new JSONObject("{\"ET\":\"1\",\"loc_key_1\":\"600\", \"loc_key_2\":\"John\"}");
            StringBuilder jsonPayLoad = new StringBuilder();
            for (Map.Entry<String, String> entry : pushNotificationData.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    if (!TextUtils.isEmpty(jsonPayLoad)) {
                        jsonPayLoad.append(String.format(PARAM_JSON_PAYLOAD_WITH_COMMA, entry.getKey(), entry.getValue()));
                    } else {
                        jsonPayLoad.append(String.format(PARAM_JSON_PAYLOAD_FORMAT, entry.getKey(), entry.getValue()));
                    }
                } else {
                    throw new IllegalArgumentException("PayLoad for Mock Push Notification is empty.");
                }
            }
            JSONObject jsonSendMoneyPayLoad = new JSONObject(String.format(PARAM_JSON_PAYLOAD_WITH_BRACKETS, jsonPayLoad));
            //Adding Push Notification Payload
            jsonMockBody.put(PARAM_DATA_KEY, jsonSendMoneyPayLoad);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        System.out.println(jsonMockBody);
        return jsonMockBody;
    }



}

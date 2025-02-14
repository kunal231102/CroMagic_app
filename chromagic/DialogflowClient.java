package com.example.chromagic;

import android.content.Context;
import android.util.Log;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import java.io.InputStream;
import java.util.UUID;

public class DialogflowClient {
    private static final String TAG = "DialogflowClient";
    private final String projectId;
    private SessionsClient sessionsClient;

    public DialogflowClient(Context context, String projectId) {
        this.projectId = projectId;

        try {
            InputStream stream = context.getResources().openRawResource(R.raw.chromia_dialogflow_credentials);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);

            SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            sessionsClient = SessionsClient.create(sessionsSettings);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create Dialogflow client: " + e.getMessage());
        }
    }

    public void sendMessage(String message, ResponseCallback callback) {
        String sessionId = UUID.randomUUID().toString();
        SessionName session = SessionName.of(projectId, sessionId);

        TextInput textInput = TextInput.newBuilder()
                .setText(message)
                .setLanguageCode("en")
                .build();
        QueryInput queryInput = QueryInput.newBuilder()
                .setText(textInput)
                .build();

        new Thread(() -> {
            try {
                DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
                String reply = response.getQueryResult().getFulfillmentText();
                callback.onResponse(reply);
            } catch (Exception e) {
                Log.e(TAG, "Error sending message to Dialogflow: " + e.getMessage());
            }
        }).start();
    }

    public interface ResponseCallback {
        void onResponse(String reply);
    }
}

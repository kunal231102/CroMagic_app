package com.example.chromagic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatBotActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageView chatBotImage;
    private Animation bounceAnim;
    private DialogflowClient dialogflowClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        // Customize the status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        // Initialize views
        chatBotImage = findViewById(R.id.chatBot_img);
        bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        chatBotImage.startAnimation(bounceAnim);

        recyclerView = findViewById(R.id.chats);
        messageInput = findViewById(R.id.type_message);
        sendButton = findViewById(R.id.sendBtn);

        // Initialize chat list and adapter
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Initialize Dialogflow client
        try {
            dialogflowClient = new DialogflowClient(this, "chromia-wayg"); // Replace with your project ID
        } catch (Exception e) {
            Toast.makeText(this, "Failed to initialize chatbot: " + e.getMessage(), Toast.LENGTH_LONG).show();
            sendButton.setEnabled(false); // Disable the send button if initialization fails
            return;
        }

        // Set up the send button click listener
        sendButton.setOnClickListener(v -> sendMessage());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage() {
        String userMessage = messageInput.getText().toString().trim();
        if (userMessage.isEmpty()) {
            Toast.makeText(this, "Please enter a message!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user message to the chat list
        chatList.add(new Chat(userMessage, true));
        chatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(chatList.size() - 1);

        // Clear the input field
        messageInput.setText("");

        // Send the message to Dialogflow
        dialogflowClient.sendMessage(userMessage, botReply -> {
            if (botReply == null || botReply.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, "No response from the chatbot", Toast.LENGTH_SHORT).show());
                return;
            }

            // Add chatbot reply to the chat list
            runOnUiThread(() -> {
                chatList.add(new Chat(botReply, false));
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatList.size() - 1);
            });
        });
    }
}

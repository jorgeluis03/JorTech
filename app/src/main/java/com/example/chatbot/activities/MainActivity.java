package com.example.chatbot.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chatbot.R;
import com.example.chatbot.adapter.MessageAdapter;
import com.example.chatbot.databinding.ActivityMainBinding;
import com.example.chatbot.model.MessageModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    RecyclerView recyclerView;
    EditText inputMessage;
    ImageButton buttonSend;
    MessageAdapter adapter;
    List<MessageModel> messageList;
    public static final MediaType JSON = MediaType.get("application/json");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.recycleView;
        inputMessage = binding.editTextInputMessage;
        buttonSend = binding.buttonSend;

        messageList = new ArrayList<>();

        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonSend.setOnClickListener(v -> {
            String inputQuestion = inputMessage.getText().toString().trim();
            if (!inputQuestion.isEmpty()){

                addToChat(inputQuestion,MessageModel.SENT_BY_ME);
                inputMessage.setText(null);

                callAPI(inputQuestion);

            }else {
                Toast.makeText(this,"Entrada vacía",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callAPI(String inputQuestion) {
        messageList.add(new MessageModel("...",MessageModel.SENT_BY_BOT));

        JSONObject jsonObject = new JSONObject();


    }

    private void addToChat(String inputQuestion, String sentByMe) {
        runOnUiThread(() -> {

            messageList.add(new MessageModel(inputQuestion,sentByMe));
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(adapter.getItemCount()); /*se utiliza para realizar un desplazamiento suave hasta la última posición del RecyclerView.
             obtiene la cantidad total de elementos en el adaptador asociado al RecyclerView.*/


        });
    }
}
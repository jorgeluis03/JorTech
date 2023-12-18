package com.example.chatbot.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chatbot.R;
import com.example.chatbot.adapter.MessageAdapter;
import com.example.chatbot.databinding.ActivityMainBinding;
import com.example.chatbot.model.MessageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    RecyclerView recyclerView;
    EditText inputMessage;
    ImageButton buttonSend;
    MessageAdapter adapter;
    List<MessageModel> messageList;
    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient();

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
        try {
            jsonObject.put("model","text-davinci-003");
            jsonObject.put("prompt",inputQuestion);
            jsonObject.put("max_tokens",5000);
            jsonObject.put("temperature",0);


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponde("Error: "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                if(response.isSuccessful()){
                    Log.d("msg-test","success");

                }else {
                    try {
                        String errorBody = response.body().string();
                        Log.d("msg-test", "Error: " + errorBody);
                    } catch (IOException e) {
                        Log.e("msg-test", "Error reading error body", e);
                    }
                }
            }
        });
    }

    public void addResponde(String s){
        messageList.remove(messageList.size()-1);
        addToChat(s,MessageModel.SENT_BY_BOT);
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
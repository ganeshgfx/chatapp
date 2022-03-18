package ganesh.gfx.chatapp.main.chatPage;


import static ganesh.gfx.chatapp.utils.Consts.CURRENTLY_ACTIVE_CHAT;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ganesh.gfx.chatapp.R;

import ganesh.gfx.chatapp.data.db.Sms;

import ganesh.gfx.chatapp.databinding.FragmentSecond3Binding;
import ganesh.gfx.chatapp.main.MainpageActivity;
import ganesh.gfx.chatapp.utils.Tools;

public class Second3Fragment extends Fragment {

    private FragmentSecond3Binding binding;

    private static final String TAG = "appgfx";

    RecyclerView recyclerView;
    chatAdapter adapter;
    public String MY_FIRE_ID;

    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecond3Binding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();
        MainpageActivity.hideFab();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainpageActivity.hideFab();
//        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(Second3Fragment.this)
//                        .navigate(R.id.action_Second3Fragment_to_First3Fragment);
//            }
//        });

        mDatabase = FirebaseDatabase.getInstance("https://dream-cee59-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();


        MY_FIRE_ID = FirebaseAuth.getInstance().getUid();

        //Log.d(TAG, "onViewCreated: "+cuttentActiveChatUser);

        Sms db = new Sms(this.getContext(), CURRENTLY_ACTIVE_CHAT);

        //db.addSms(new Chat("Hi", FirebaseAuth.getInstance().getUid()));
        //db.addSms(new Chat("df",  "she"));
        //Log.d(TAG, "Recycle Chat : "+ Tools.gson.toJson(db.getAllChats()));

        recyclerView = (RecyclerView) view.findViewById(R.id.recycledChats);
        adapter = new chatAdapter(db.getAllChats());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        scrol(false);

        chatTextInputLayout = (TextInputLayout) view.getRootView().findViewById(R.id.chatBox);
        chatTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d(TAG, "onTextChanged: "+s);
                checkText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        DatabaseReference chatLocation =
                mDatabase.child("sms").child(CURRENTLY_ACTIVE_CHAT).child(MY_FIRE_ID);

        chatTextInputLayout.setEndIconOnClickListener(v -> {
            if (checkText()) {

                Chat chat = new Chat(chatTextInputLayout.getEditText().getText() + "", MY_FIRE_ID);

                addNewChatinUI(chat);
                chatTextInputLayout.getEditText().setText("");

                Map<String, Object> fireSMS = new HashMap<>();
                fireSMS.put("message", chat.message);

                chatLocation.push().setValue(fireSMS)
                        .addOnSuccessListener(aVoid -> {

                            if (addToDb(db, chat.message, MY_FIRE_ID)) {

                            } else Log.d(TAG, "Err adding msg to db");

                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Failed to send sms : " + e.getMessage());
                        });


            }

        });


        FirebaseDatabase
                .getInstance("https://dream-cee59-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference()
                .child("sms")
                .child(FirebaseAuth.getInstance().getUid())
                .child(CURRENTLY_ACTIVE_CHAT)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        proccessData(snapshot, db);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                .addValueEventListener(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        proccessData(snapshot, db);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.d(TAG, "onCancelled : Error Reading Chats - "+error.getMessage());
//                    }
//                });
    }

    private void proccessData(@NonNull DataSnapshot data, Sms db) {

        Chat value = data.getValue(Chat.class);
        //value.message = (String) data.getValue();
        value.userId = CURRENTLY_ACTIVE_CHAT;

        //Log.d(TAG, "\n"+ Tools.gson.toJson(data.getValue()));
        //Log.d(TAG,""+data.getKey());

        addNewChatinUI(value);

        if (addToDb(db, value.message, CURRENTLY_ACTIVE_CHAT)) {

            FirebaseDatabase
                    .getInstance("https://dream-cee59-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference()
                    .child("sms")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(CURRENTLY_ACTIVE_CHAT)
                    .child(data.getKey())
                    .removeValue();

        } else Log.d(TAG, "Err adding msg to db");

//        for (DataSnapshot data : snapshot.getChildren()) {
//
//
//        }
    }

    private boolean addToDb(Sms sms, String message, String fireId) {
        return sms.addSms(new Chat(message, fireId));
    }

    boolean checkText() {
        EditText editText = chatTextInputLayout.getEditText();
        String text = editText.getText().toString();
        int max = 100;

        if (text.length() > max) {
            editText.setText(text.substring(0, max));
            editText.setSelection(editText.length());
            chatTextInputLayout.setHelperText("Massage limit " + max + " letters \uD83D\uDC7E");
        } else chatTextInputLayout.setHelperText("");

        return !text.isEmpty();
    }

    TextInputLayout chatTextInputLayout;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void addNewChatinUI(Chat chat) {

        adapter.addNewChat(chat);
        scrol(true);
    }

    void scrol(boolean smooth) {
        if (smooth) {
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        } else recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }
}
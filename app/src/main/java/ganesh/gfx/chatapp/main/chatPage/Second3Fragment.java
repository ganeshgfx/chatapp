package ganesh.gfx.chatapp.main.chatPage;

import static com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM;
import static com.google.android.material.textfield.TextInputLayout.END_ICON_NONE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import ganesh.gfx.chatapp.R;


import ganesh.gfx.chatapp.data.db.Sms;


import ganesh.gfx.chatapp.databinding.FragmentSecond3Binding;
import ganesh.gfx.chatapp.main.MainpageActivity;
import ganesh.gfx.chatapp.main.chatPage.Chat;
import ganesh.gfx.chatapp.main.chatPage.chatAdapter;
import ganesh.gfx.chatapp.main.contactPage.ListAdapter;
import ganesh.gfx.chatapp.utils.Tools;

public class Second3Fragment extends Fragment {

    private FragmentSecond3Binding binding;

    private static final String TAG = "appgfx";

    RecyclerView recyclerView;
    chatAdapter adapter;
    public String myFireId;

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

        myFireId = FirebaseAuth.getInstance().getUid();

        Sms sms = new Sms(this.getContext());

//        sms.addSms(new Chat("Hi", FirebaseAuth.getInstance().getUid()));
//        sms.addSms(new Chat("df",  "she"));

        //Log.d(TAG, "Recycle Chat : "+ Tools.gson.toJson(sms.getAllChats()));

        recyclerView = (RecyclerView) view.findViewById(R.id.recycledChats);
        adapter = new chatAdapter(sms.getAllChats());
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



        chatTextInputLayout.setEndIconOnClickListener(v -> {
            if(checkText()) {

                Chat chat = new Chat(chatTextInputLayout.getEditText().getText() + "",myFireId);

                if (addToDb(sms)) {
                    addNewChatinUI(chat);
                    chatTextInputLayout.getEditText().setText("");
                }else Log.d(TAG, "Err adding msg to db");
            }

        });


    }

    private boolean addToDb(Sms sms) {
        return sms.addSms(new Chat(chatTextInputLayout.getEditText().getText() + "", FirebaseAuth.getInstance().getUid()));
    }

    boolean checkText(){
        EditText editText = chatTextInputLayout.getEditText();
        String text = editText.getText().toString();
        int max = 100;

        if (text.length()>max){
            editText.setText(text.substring(0,max));
            editText.setSelection(editText.length());
            chatTextInputLayout.setHelperText("Massage limit "+max+" letters \uD83D\uDC7E");
        }else chatTextInputLayout.setHelperText("");

        return !text.isEmpty();
    }

    TextInputLayout chatTextInputLayout;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    void addNewChatinUI(Chat chat){

        adapter.addNewChat(chat);
        scrol(true);
    }
    void scrol(boolean smooth){
        if(smooth){
            recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        }
        else recyclerView.scrollToPosition(adapter.getItemCount()-1);
    }
}
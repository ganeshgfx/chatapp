package ganesh.gfx.chatapp.main.contactPage;

import static ganesh.gfx.chatapp.main.MainpageActivity.hideFab;
import static ganesh.gfx.chatapp.utils.Consts.cuttentActiveChatUser;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ganesh.gfx.chatapp.R;
import ganesh.gfx.chatapp.data.db.Contacts;
import ganesh.gfx.chatapp.main.MainpageActivity;
import ganesh.gfx.chatapp.utils.Tools;


public class First3Fragment extends Fragment {

    private static final String TAG = "appgfx";
    private ganesh.gfx.chatapp.databinding.FragmentFirst3Binding binding;

    @Override
    public void onResume() {
        super.onResume();
        MainpageActivity.showFab();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = ganesh.gfx.chatapp.databinding.FragmentFirst3Binding.inflate(inflater, container, false);
        return binding.getRoot();

    }
    boolean firstRun = true;
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!firstRun)
            MainpageActivity.showFab();
        firstRun = false;

        Contacts db = new Contacts(this.getContext());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycledCons);
        ListAdapter adapter = new ListAdapter(db.getAllContactsList());

        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());



        adapter.setOnItemListenerListener(new ListAdapter.OnItemListener() {
            @Override
            public void OnItemClickListener(View view, int position) {

//                Toast.makeText(MainActivity.this,
//                        "project"+position+"Click!",
//                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "OnItemClickListener: "+position);

               openChat("position");
                hideFab();
            }

            @Override
            public void OnItemLongClickListener(View view, int position) {

//                Toast.makeText(MainActivity.this,
//                        "project"+position+"Be pressed long!",
//                        Toast.LENGTH_SHORT).show();
               // Log.d(TAG, "OnItemLongClickListener: "+position);

                //openChat();
                //hideFab();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void openChat(String chat){
        cuttentActiveChatUser = chat;
        NavHostFragment.findNavController(First3Fragment.this)
                .navigate(R.id.action_First3Fragment_to_Second3Fragment);
    }

}
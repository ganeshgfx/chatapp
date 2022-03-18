package ganesh.gfx.chatapp.main.contactPage;

import static ganesh.gfx.chatapp.main.MainpageActivity.hideFab;
import static ganesh.gfx.chatapp.utils.Consts.CURRENTLY_ACTIVE_CHAT;

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
import ganesh.gfx.chatapp.data.Friend;
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
    ListAdapter adapter;
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!firstRun)
            MainpageActivity.showFab();
        firstRun = false;

        Contacts db = new Contacts(this.getContext());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycledCons);
        adapter = new ListAdapter(db.getAllContactsList());

        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(recyclerItemClickListener(recyclerView));
    }
    RecyclerItemClickListener recyclerItemClickListener(final RecyclerView recyclerView){
        return new RecyclerItemClickListener (getContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        Friend friend = adapter.getData(position);

                        //Log.d(TAG, "OnItemClickListener: "+Tools.gson.toJson(friend));

                        openChat(friend.getID());
                        hideFab();

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void openChat(String chat){
        CURRENTLY_ACTIVE_CHAT = chat;
        NavHostFragment.findNavController(First3Fragment.this)
                .navigate(R.id.action_First3Fragment_to_Second3Fragment);
    }

}
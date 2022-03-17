package ganesh.gfx.chatapp.main.chatPage;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ganesh.gfx.chatapp.R;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder> {
    private static final String TAG = "appgfx";
    List<Chat> chats = new ArrayList<Chat>();

    public chatAdapter(List<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public chatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.chat_view, parent, false);
        chatAdapter.ViewHolder viewHolder = new chatAdapter.ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat cht = chats.get(position);
        holder.chatBBl.setText(cht.dbTime+" : "+cht.message);
        if (chats.get(position).sendByMe) {

            holder.chatBBl.setGravity(Gravity.END);
            holder.chatBBl.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.chatBBl.setBackgroundColor(holder.chatBBl.getContext().getResources().getColor(R.color.SenderChipSurface));
            holder.linearLayout.setGravity(Gravity.END);

        } else {

            holder.chatBBl.setGravity(Gravity.START);
            holder.chatBBl.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.chatBBl.setBackgroundColor(holder.chatBBl.getContext().getResources().getColor(R.color.ReciverChipSurface));
            holder.linearLayout.setGravity(Gravity.START);

        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public Button chatBBl;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            //this.imageView = (ImageView) itemView.findViewById(R.id.conImg);
            this.chatBBl = (Button) itemView.findViewById(R.id.chip4sms);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.chatsListLayout);
        }
    }

    public void addNewChat(Chat newChat){
        chats.add(newChat);
        notifyDataSetChanged();
    }

}

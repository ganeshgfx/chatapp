package ganesh.gfx.chatapp.main.contactPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ganesh.gfx.chatapp.R;
import ganesh.gfx.chatapp.data.Friend;
import ganesh.gfx.chatapp.main.chatPage.Chat;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private final List<Friend> listdata;

    private OnItemListener onItemListener;

    public ListAdapter(Friend[] listdata) {
        this.listdata = ArrayToListConversion(listdata);
    }
    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.contact_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        MyViewHolder myViewHolder = new MyViewHolder(listItem);
        myViewHolder.conCard.setOnClickListener(new conItemListener());
        myViewHolder.conCard.setOnLongClickListener(new conItemListener());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ViewHolder holder, int position) {
        //final ListData myListData = listdata[position];
        holder.textView.setText(listdata.get(position).getName());
        holder.imageView.setImageResource(listdata.get(position).getImgId());
        holder.conCard.setOnClickListener(new conItemListener());
    }

    @Override
    public int getItemCount() {
        return  listdata.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public LinearLayout linearLayout;
        public CardView conCard;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.conImg);
            this.textView = (TextView) itemView.findViewById(R.id.conText);
            this.conCard = (CardView) itemView.findViewById(R.id.contactCard);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.contactLayout);
        }
    }

    //Inner class, bound control
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv1;
        ImageView iv1;
        CardView conCard;
        LinearLayout linearLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.contactLayout);
            conCard = itemView.findViewById(R.id.contactCard);
            tv1 = itemView.findViewById(R.id.conText);
            iv1 = itemView.findViewById(R.id.conImg);
        }
    }

    public  interface OnItemListener{
        void OnItemClickListener(View view, int position);
        void OnItemLongClickListener(View view, int position);
    }
    class conItemListener implements View.OnClickListener, View.OnLongClickListener{

        @Override
        public void onClick(View view) {
            if (onItemListener != null){
                onItemListener.OnItemClickListener(view, view.getId());
            }
        }
        @Override
        public boolean onLongClick(View view) {
            if (onItemListener != null){
                onItemListener.OnItemLongClickListener(view,view.getId());
            }
            return true;
        }
    }
    public void setOnItemListenerListener(OnItemListener listener){
        this.onItemListener = listener;
    }
    public static <T> List<T> ArrayToListConversion(T array[]) {
        List<T> list = new ArrayList<>();
        for (T t : array) {
            list.add(t);
        }
        return list;
    }
    Friend getData(int pos){
        return listdata.get(pos);
    }
    public void addNewFriend(Friend friend){
        listdata.add(friend);
        notifyDataSetChanged();
    }
    public void removeFriend(int pos){
        listdata.remove(pos);
        notifyDataSetChanged();
    }
}

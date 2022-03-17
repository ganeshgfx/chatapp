package ganesh.gfx.chatapp.data;

import ganesh.gfx.chatapp.R;

public class Friend {
    String _id;
    String _name;

    public Friend(){   }
    public Friend(String id, String name){
        this._id = id;
        this._name = name;
    }

    public Friend(String name){
        this._name = name;
    }
    public String getID(){
        return this._id;
    }

    public void setID(String id){
        this._id = id;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public int getImgId(){
        return R.drawable.ispidy;
    }
}
package ganesh.gfx.chatapp.main.chatPage;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;

public class Chat {

    public String dbId;
    public String message;
    private String userId;
    public String dbTime;

    public boolean sendByMe;


    public Chat(String message, boolean sendByMe){
        this.message = message;
        this.sendByMe =  sendByMe;
    }

    //consttructor


    public Chat(String dbId, String message, String userId, String dbTime) {

        this.dbId = dbId;
        this.message = message;
        this.userId = userId;
        this.dbTime = dbTime;

        if(userId.equals(FirebaseAuth.getInstance().getUid())) this.sendByMe = true;
        else this.sendByMe = false;

    }

    //Create New for sending
    public Chat(String message, String key) {

        this.message = message;
        this.dbTime = "null";
        this.userId = key;
       // this.dbTime = ServerValue.TIMESTAMP.get();

        if(key.equals(FirebaseAuth.getInstance().getUid())) this.sendByMe = true;
        else this.sendByMe = false;
    }

    public String getMessage() {
        return message;
    }

    public String getDbTime() {
        return dbTime;
    }

    public String getUserId() {
        return userId;
    }
}

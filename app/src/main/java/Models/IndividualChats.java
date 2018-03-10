package Models;

import com.orm.SugarRecord;

import java.util.List;

public class IndividualChats extends SugarRecord {
    private String from,to,type,chat_message,object_id;
    Long time;
    public IndividualChats(){

    }

    public IndividualChats(Message message){
        this.from = message.getMessageFromn();
        this.to = message.getMessageTo();
        this.time = Long.valueOf(message.getTime());
        this.type = message.getType();
        this.chat_message = message.getChat_message();
        this.object_id = message.getObjectId();
    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChat_message() {
        return chat_message;
    }

    public void setChat_message(String chat_message) {
        this.chat_message = chat_message;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
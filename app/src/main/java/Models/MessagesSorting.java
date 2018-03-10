package Models;

import com.orm.SugarRecord;

public class MessagesSorting extends SugarRecord {

    String from,to,type,chat_message,object_id;
    Long time;

    public MessagesSorting() {

    }

    public MessagesSorting(Message message){
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

    public String getTo() {
        return to;
    }

    public String getType() {
        return type;
    }

    public String getChat_message() {
        return chat_message;
    }

    public String getObject_id() {
        return object_id;
    }

    public Long getTime() {
        return time;
    }
}
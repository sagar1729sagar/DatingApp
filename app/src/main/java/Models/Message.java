package Models;

import com.orm.SugarRecord;

/**
 * Created by sagar on 19/02/18.
 */

public class Message extends SugarRecord {

    private String message_from,message_to,time,type,chat_message,objectId;

    public Message(){

    }

    public String getMessage_from() {
        return message_from;
    }

    public void setMessage_from(String message_from) {
        this.message_from = message_from;
    }

    public String getMessage_to() {
        return message_to;
    }

    public void setMessage_to(String message_to) {
        this.message_to = message_to;
    }

    //    public String getMessageFromn() {
//        return messageFromn;
//    }
//
//    public void setMessageFromn(String messageFromn) {
//        this.messageFromn = messageFromn;
//    }
//
//    public String getMessageTo() {
//        return messageTo;
//    }
//
//    public void setMessageTo(String messageTo) {
//        this.messageTo = messageTo;
//    }

    //    public String getFrom() {
//        return from;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//
//    public String getTo() {
//        return to;
//    }
//
//    public void setTo(String to) {
//        this.to = to;
//    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}

package Models;

import com.orm.SugarRecord;

public class MessagesSorting extends SugarRecord {

    String message_from,message_to,type,chat_message,object_id;
    Long time;

    public MessagesSorting() {

    }

    public MessagesSorting(Message message){
        this.message_from = message.getMessage_from();
        this.message_to = message.getMessage_to();
        this.time = Long.valueOf(message.getTime());
        this.type = message.getType();
        this.chat_message = message.getChat_message();
        this.object_id = message.getObjectId();
    }

    public String getMessage_from() {
        return message_from;
    }

    public String getMessage_to() {
        return message_to;
    }

    //    public String getFrom() {
//        return from;
//    }
//
//    public String getTo() {
//        return to;
//    }


//    public String getMessageFromn() {
//        return messageFromn;
//    }
//
//    public String getMessageTo() {
//        return messageTo;
  //  }

    //    public String getMessageto() {
//        return messageto;
//    }

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
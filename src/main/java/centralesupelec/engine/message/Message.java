package centralesupelec.engine.message;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Message {

    private ACLMessage message;
    private String content;


    public Message(Integer performative, String content, String conv_id, AID receiver){

        this.content = content;
        message = new ACLMessage(performative);
        message.setContent(content);
        message.setConversationId(conv_id);
        message.setReplyWith(conv_id + System.currentTimeMillis()); // Unique value
        message.addReceiver(receiver);
    }

    public ACLMessage getMessage(){
        return message;
    }
    public String getContent(){
        return content;
    }
}

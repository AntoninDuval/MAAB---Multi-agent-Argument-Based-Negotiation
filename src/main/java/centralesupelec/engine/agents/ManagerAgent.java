package centralesupelec.engine.agents;

import centralesupelec.engine.preferences.Item;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;

public class ManagerAgent extends Agent{

    private ArrayList<Item> list_initials_items;
    private AID[] EngineersAgents = {new AID("engineer1", AID.ISLOCALNAME),
                                    new AID("engineer2", AID.ISLOCALNAME)};

    @SuppressWarnings("unchecked")
    protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! Manager-agent "+getLocalName()+" is ready.");
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            list_initials_items = (ArrayList<Item>) args[0];
            for(Item item : list_initials_items){
                System.out.println("Manager wants to discuss about " + item.get_name());
            }
            addBehaviour(new SendDiscussedItems());
            addBehaviour(new ReceivedEndOfDiscussion());
        }
    }

    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Manager agent " + getLocalName() + " terminating.");
    }


    private class SendDiscussedItems extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM_REF);
                reply.setContent("Sending engine list");
                reply.setContent(convert_to_string_list(list_initials_items));

                reply.addReceiver(msg.getSender());
                myAgent.send(reply);
                System.out.println(getLocalName() + " to " + msg.getSender().getLocalName() + " INFORM_REF(Items)");
            }
        }
    }

    private class ReceivedEndOfDiscussion extends CyclicBehaviour{
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM_REF);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                String item_name = msg.getContent();
                System.out.println(getLocalName() + " is informed that " + item_name + " is the selected item");
                myAgent.doDelete();
            }
        }
    }

    public String convert_to_string_list(ArrayList<Item> list){
        String final_string = "";
        for(Item item:list){
            final_string += item.get_name() + "_";
        }
        return final_string;
    }
}

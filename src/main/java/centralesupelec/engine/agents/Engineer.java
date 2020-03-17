package centralesupelec.engine.agents;

import centralesupelec.engine.arguments.Arguments;
import centralesupelec.engine.preferences.*;
import centralesupelec.engine.message.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.StaleProxyException;
import javafx.util.Pair;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Engineer extends Agent {

    private Preferences agent_pref; // Agent's preference
    private AID manager_agent = new AID("manager", AID.ISLOCALNAME); //AID for manager
    private String state; // State the engineer is in


    private Boolean use_real; // Do we use real value for preferences
    private  Integer nb_crit; // Number of criterion to use
    private  Integer nb_value; // Number of different values for criterions

    private Integer nb_iterations = 0; // To track the number of iterations
    private List<Integer> nb_arguments = new ArrayList<>(); // To track the number of arguments

    private Item proposed_item; // Item currently in discussion
    private Boolean position_item; // Position of the engineer for this item
    private Arguments argument_item;  // Argument for this item
    private Boolean first_argument = Boolean.FALSE; // Are we giving the first argument

    public ArrayList<Item> list_initial_items = null; // List of initials item at the start
    public ArrayList<Item> list_items = null; // Item currently available to discuss
    public ArrayList<Item> list_proposed_items = new ArrayList<>(); // Item previously discussed
    public AID other_engineer = null;


    protected void setup() {
        System.out.println("Hallo! Engineer-agent "+getLocalName()+" is ready.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            state = (String) args[0]; //get initial state
            use_real = (Boolean) args[1];// Do we use real value for preferences
            nb_crit = (Integer) args[2];
            nb_value = (Integer) args[3];
        }


        if (getLocalName().equals("ENGINEER0")) {
            other_engineer = new AID("ENGINEER1", AID.ISLOCALNAME);
        }
        else{
            other_engineer = new AID("ENGINEER0", AID.ISLOCALNAME);
        }

        // Request to the manager the list of item to discuss
        SequentialBehaviour comportementSequentiel = new SequentialBehaviour();
        comportementSequentiel.addSubBehaviour(new RequestItemList());
        comportementSequentiel.addSubBehaviour(new DiscussItem());
        addBehaviour(comportementSequentiel);
    }

    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Engineer agent " + getLocalName() + " terminating.");
    }


    private class RequestItemList extends Behaviour {

        private MessageTemplate mt; // The template to receive replies
        private int step = 0;


        public void action(){
            switch (step) {
                case 0: // Send a message to manager to request the list of items to discuss
                    ACLMessage rqt = new ACLMessage(ACLMessage.QUERY_REF);
                    rqt.addReceiver(manager_agent);
                    rqt.setConversationId("sending_initial_list");
                    rqt.setReplyWith("rqt" + System.currentTimeMillis());
                    myAgent.send(rqt);
                    System.out.println(getLocalName() + " to " + manager_agent.getLocalName() + " QUERY_REF(Items) ?");
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("sending_initial_list"),
                            MessageTemplate.MatchInReplyTo(rqt.getReplyWith()));
                    step = 1;
                    break;
                case 1:// Check if the manager replied
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.INFORM_REF) {
                            // This is the list item to discussed

                                //list_initial_items = create_list_item_from_string(reply.getContent());
                                agent_pref = new Preferences(use_real);
                                list_initial_items = use_csv_preferences(myAgent.getLocalName());
                                list_items = new ArrayList<>(list_initial_items);
                                //agent_pref.generating_random_preferences(list_initial_items, nb_crit, nb_value);

                                //agent_pref.print_preferences(list_initial_items);

                        }
                        else {
                            block();
                        }
                        step = 2;
                        break;
                    }
            }
            }
        public boolean done() {return (step == 2 );
        }
        }



    private class DiscussItem extends Behaviour {
        /**
         * Handle the discussion between agents for an item. It ends when the agent is in state DONE.
         */

        public void action() {
            switch (state) {
                case "WAIT": // When in state WAIT

                    // Case when we received a proposition
                    ACLMessage reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
                    if (reply != null) {

                        nb_iterations += 1; // Update number of proposed item
                        String proposed_item_name = reply.getContent(); // Get Item name proposed by other agent
                        proposed_item = Item.get_item_from_name(proposed_item_name, list_items);

                        list_proposed_items.add(proposed_item); // add item to the list of proposed items

                        if (agent_pref.most_prefered_item(list_items).equals(proposed_item)) {
                            state = "ACCEPT"; //Put the agent in state ACCEPT
                        }
                        else {
                            state = "ASK_WHY"; //Put the agent in state ASK_WHY
                        }

                        list_items.remove(proposed_item); // remove item from list of initial items
                        break;
                    }

                    // Case when we received a COMMIT
                    reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
                    if (reply != null) {
                        state = "COMMIT";
                        // Send to the manager the item
                        Message msg_end = new Message(ACLMessage.INFORM_REF,
                                getProposed_item().get_name(),
                                "item_selected",
                                manager_agent);
                        myAgent.send(msg_end.getMessage());
                        break;
                    } else {
                        block();
                    }

                    // Case when we received an ASK_WHY. We generate an argument.
                    reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF));
                    if (reply != null) {
                        argument_item = new Arguments(proposed_item, position_item, agent_pref); // Create the argumentation
                        first_argument =  Boolean.TRUE;
                        myAgent.addBehaviour(new Argumentation()); // Start the argumentation process
                        break;
                    }
                    else {
                        block();
                    }
                    break;

                case "PROPOSE":
                    nb_iterations += 1; // Update number of proposed item
                    Pair<Item, Boolean> a = get_next_favorite_item();

                    if(a.getValue()){ // If the next best item hasn't been proposed yet

                        proposed_item = a.getKey();
                        agent_pref.most_prefered_item(list_items);
                        list_items.remove(proposed_item);
                        Message msg_propose = new Message(ACLMessage.PROPOSE,
                                getProposed_item().get_name(),
                                "propose_item",
                                other_engineer);
                        myAgent.send(msg_propose.getMessage());
                        System.out.println(myAgent.getLocalName() + ":" + state + "(" + proposed_item.get_name() + ")");

                        position_item = Boolean.TRUE; // We want to give argument FOR this item

                        state = "WAIT";//Put agent in WAIT mode
                        break;
                    }
                    else{ // If the other agent already proposed an item that is better
                        proposed_item = a.getKey();
                        state = "ACCEPT";//Put agent in ACCEPT mode
                        break;
                    }

                case "ACCEPT":
                    Message msg_accept = new Message(ACLMessage.ACCEPT_PROPOSAL,
                            getProposed_item().get_name(),
                            "accept_item",
                            other_engineer);
                    System.out.println(myAgent.getLocalName() + ":" + state + "(" + msg_accept.getContent() + ")");
                    myAgent.send(msg_accept.getMessage());
                    state = "COMMIT";//Put agent in WAIT mode
                    break;

                case "COMMIT":
                    Message msg_commit = new Message(ACLMessage.CONFIRM,
                            getProposed_item().get_name(),
                            "confirm",
                            other_engineer);
                    System.out.println(myAgent.getLocalName() + ": COMMIT(" + getProposed_item().get_name() + ")");
                    myAgent.send(msg_commit.getMessage());
                    state =  "DONE";//Put agent in DONE mode
                    //System.out.println("Number of proposed item : "+nb_iterations);
                    //System.out.println("Number of arguments for each item : "+ nb_arguments);
                    break;

                case "ASK_WHY":
                    Message msg_ask = new Message(ACLMessage.QUERY_REF,
                            getProposed_item().get_name(),
                            "query_ref",
                            other_engineer);
                    myAgent.send(msg_ask.getMessage());
                    System.out.println(myAgent.getLocalName() + ": ASK_WHY(" + getProposed_item().get_name() + ")");

                    position_item = Boolean.FALSE; // We want to give argument AGAINST this item
                    list_proposed_items.add(proposed_item); // We add this item to the item proposed by the other agent we are against

                    argument_item = new Arguments(proposed_item, position_item, agent_pref); // Create the argumentation
                    state =  "WAIT";
                    myAgent.addBehaviour(new Argumentation()); // Start the argumentation process
                    break;
            }
        }
        public boolean done() {return (state.equals("DONE")); }

        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }

    private class Argumentation extends Behaviour{

        private Boolean state_argument = Boolean.FALSE; // Boolean to decide whether or not we end the argumentation process
        private int nb_argument = 0; // To track nb of arguments for this item

        public void action(){
            /**
             * Handle the procedure for argumentation.
             */
            Boolean waiting_for_answer = Boolean.TRUE;
            // RECEIVE RESPONSE TO PREVIOUS ARGUMENT
            String name_argument_to_counter = null;

            // Check if we received an argument
            ACLMessage reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (reply != null) {
                String argument = reply.getContent(); // Get the argument of the agent
                name_argument_to_counter = argument.split("=")[0];
                waiting_for_answer = Boolean.FALSE; // We want to generate an argument after that
            }

            reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
            if (reply != null) { // The other agent sends an ACCEPT for an item
                String proposed_item_name = reply.getContent(); // Get Item name proposed by other agent
                proposed_item = Item.get_item_from_name(proposed_item_name, list_initial_items);
                state_argument = Boolean.TRUE;
                state = "COMMIT"; //We go to wait stage to receive the commit
                nb_arguments.add(nb_argument);
            }

            // The other agent don't have any other arguments for previous item. He send a new item to discuss.
            reply = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
            if (reply != null) {
                nb_iterations += 1; // Update number of proposed item
                String proposed_item_name = reply.getContent(); // Get the item of the agent
                proposed_item = Item.get_item_from_name(proposed_item_name, list_items);
                state = test_pref_for_item(proposed_item); // We check our preferences for this new item.
                list_items.remove(proposed_item); // We remove this item from the list
                state_argument = Boolean.TRUE; // We leave the discussion
                nb_arguments.add(nb_argument);
            }

            if (!waiting_for_answer |first_argument){ // If we want to give first argument or if we received an argument

                //////////////////////
                // GENERATE ARGUMENT//
                //////////////////////

                String argument_string = "";


                if (first_argument){ // Si c'est le premier argument de la discussion
                    CriterionValue argument_criterion = argument_item.get_best_argument();
                    argument_item.getArgument_score().remove(argument_criterion);
                    argument_string += argument_criterion.get_criterion_name().getName() +"="+argument_criterion.get_value().get_value();
                    first_argument = Boolean.FALSE;

                }

                else { // Si un autre argument a été donné auparavant, lui répondre.
                    CriterionName argument_to_counter = agent_pref.get_criterion_from_string(name_argument_to_counter);
                    CriterionValue argument_criterion = argument_item.find_argument(argument_to_counter);
                    argument_item.getArgument_score().remove(argument_criterion);

                    try { // Try creating the argument
                        argument_string += argument_criterion.get_criterion_name().getName() +
                                "=" + argument_criterion.get_value().get_value() +
                                " AND " +
                                argument_criterion.get_criterion_name().getName() + ">" + name_argument_to_counter;
                    }
                    catch (NullPointerException e){ // If we can't, it means we have no argument to answer
                        nb_arguments.add(nb_argument);
                        state_argument= Boolean.TRUE; // End the argumentation at the end of the behavior
                        if (position_item){
                            state =  "PROPOSE"; // If we in favor of this item and we lost the argumentation, we propose a new one
                        }
                        else{
                            state =  "ACCEPT"; // Else, we accept.
                        }
                    }
                }


                Message msg_commit = new Message(ACLMessage.INFORM,
                        argument_string,
                        "argument",
                        other_engineer);

                if (!state_argument) { // If we have found an argument
                    nb_argument += 1; // Update nb_argument
                    myAgent.send(msg_commit.getMessage());
                    //PRINT THE ARGUMENT
                    String against = "";
                    if(!position_item){against = "NOT ";}
                    System.out.println(myAgent.getLocalName() + " : " +
                            "ARGUE(" + against +getProposed_item().get_name() + " <= " +
                            argument_string + ")");
                }
            }
        }


        public boolean done() {
            return (state_argument);
        }
    }

    public String test_pref_for_item(Item item){
        if (agent_pref.most_prefered_item(list_items).equals(item)) {
            return "ACCEPT"; //Put the agent in state ACCEPT
        }
        else {
            return "ASK_WHY"; //Put the agent in state ASK_WHY
        }
    }

    public Item getProposed_item(){
        return proposed_item;
    }

    public ArrayList<Item> create_list_item_from_string(String s){
        ArrayList<Item> final_list = new ArrayList<>();
        for(String name:s.split("_")){
            final_list.add(new Item(name,"description"));
        }
        return final_list;
    }

    public ArrayList<Item> use_csv_preferences(String agent_name){
        try {
            return agent_pref.load_preferences(agent_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Pair<Item,Boolean> get_next_favorite_item(){
        /**
         * Return a Pair of the next favorite item, and True if it is a new item not proposed before.
         */

        Item best_item = agent_pref.most_prefered_item(list_items); // We get the best item still not proposed
        Item best_old_item = agent_pref.most_prefered_item(list_proposed_items); // We get the best item already proposed

        if(best_old_item != null){
            if(agent_pref.is_prefered_item(best_item,best_old_item)){
                return new Pair<>(best_item,Boolean.TRUE);
            }
            else{
                return new Pair<>(best_old_item,Boolean.FALSE);}
        }
        return new Pair<>(best_item,Boolean.TRUE);
    }
}

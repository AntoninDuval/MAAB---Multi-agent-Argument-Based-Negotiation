package centralesupelec.engine.preferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Preferences {

    private ArrayList<CriterionName> criterion_name_list = new ArrayList<>();
    private ArrayList<CriterionValue> criterion_values = new ArrayList<>();

    String PATH = "C:\\Users\\Anton\\Git_Project\\template_java_jade\\src\\main\\java\\centralesupelec\\engine\\preferences\\";

    public Preferences() {
    }

    // Constructor
    public void set_criterion_name_list(ArrayList<CriterionName> items) {
        criterion_name_list = items;
    }

    public void set_criterion_value(ArrayList<CriterionValue> criterion_values) {
        this.criterion_values = criterion_values;
    }

    public void add_criterion_value(CriterionValue criterion_value) {
        this.criterion_values.add(criterion_value);
    }

    // Getter
    public ArrayList<CriterionName> get_criterion_name_list() {
        return criterion_name_list;
    }

    public ArrayList<CriterionValue> get_criterion_values_list() {
        return criterion_values;
    }

    public Long get_number_of_criterions(Item item) {

        Stream<CriterionValue> sp;
        sp = this.criterion_values.stream();
        return sp.filter(x -> x.get_item().equals(item))
                .count();
    }

    public Boolean is_prefered_criterion(CriterionName crit1, CriterionName crit2) {
        // Return the prefered criterion according to the index in the criterion_name_list
        int pos1 = criterion_name_list.indexOf(crit1);
        int pos2 = criterion_name_list.indexOf(crit2);
        if (pos1 < pos2) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public Double get_score(Item item) {

        int number_of_criterion = criterion_name_list.size();
        // For each criterion,
        Stream<CriterionValue> sp;
        sp = this.criterion_values.stream();
        Double score = sp.filter(x -> x.get_item().get_name().equals(item.get_name())) // get the weights and value of each criterion
                .map(x -> (number_of_criterion - criterion_name_list.indexOf(x.get_criterion_name()))* (double) x.get_value().get_score())
                .reduce((double) 1, Double::sum);
        return score / get_number_of_criterions(item);
    }

    public Integer get_item_rank(Item item, List<Item> list_items){
        /*
          @param item
         * @param list_items
         * @return
         */

        return 1;
    }

    public Integer get_criterion_rank(CriterionValue criterionValue){
        return criterion_name_list.indexOf(criterionValue.get_criterion_name());
    }

    public Boolean is_prefered_item(Item item1, Item item2) {
        /**
         * @param item1
         * @param item2
         * @return
         */

        Double score_1 = get_score(item1);
        Double score_2 = get_score(item2);

        if (score_1 > score_2) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public CriterionName get_criterion_from_string(String name_criterion) {
        Stream<CriterionName> sp = criterion_name_list.stream();
        Optional<CriterionName> answer = sp.filter(x -> x.getName().equals(name_criterion)).findFirst();
        // Optional is returned.
        if (answer.isPresent()) {
            return answer.get();
        } else {
            System.out.println("This item is not on the list");
            return null;
        }
    }


    public Item most_prefered_item(List<Item> list_item){

        Item prefered_item = null;
        Double max_score = 0.0;

        if(list_item != null) {
            for (Item item : list_item) {
                Double score = get_score(item);
                if (score > max_score) {
                    max_score = score;
                    prefered_item = item;
                }
            }
            return prefered_item;
        }
        return prefered_item;
    }

    public void print_preferences(ArrayList<Item> list_item) {

        for (Item item : list_item) {
            Stream<CriterionValue> sp = criterion_values.stream();
            List<CriterionValue> list_crit = sp.filter(x -> x.get_item().equals(item)).collect(Collectors.toList());
            System.out.println(item.get_name());
            for (CriterionValue crit : list_crit) {
                System.out.println(crit.get_criterion_name().getName() + " : " + crit.get_value().get_value());
            }
        }
    }

    public ArrayList<Item> load_preferences(String agent_id) throws IOException {
        /**
         * Method to load preferences from csv file for each agent. Depends of the agent name.
         */

        String row;
        String path_agent;
        int i = 0;
        ArrayList<Item> item_list = new ArrayList<>();
        ArrayList<CriterionName> list_criterions_name = new ArrayList<>();

        if(agent_id.equals("ENGINEER0")){
            path_agent = PATH + "test_pref0.csv";
        }
        else{path_agent = PATH + "test_pref1.csv";}

        BufferedReader csvReader = new BufferedReader(new FileReader(path_agent));
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(";");
            if(i==0){ // If we read the first line
                for(int k=1; k <= data.length-1; k++){
                    item_list.add(new Item(data[k], "An engine"));
                }

            }
            else{
                CriterionName criterionName = new CriterionName(data[0]);
                list_criterions_name.add(criterionName);

                for (int j = 1; j <= item_list.size(); j++) {
                        Value value = new Value(data[j]);
                        CriterionValue criterionValue = new CriterionValue(item_list.get(j - 1), criterionName, value);
                        add_criterion_value(criterionValue);
                    }
            }
            i+=1;
        }
        csvReader.close();
        set_criterion_name_list(list_criterions_name);

        return item_list;
    }

    public void generating_random_preferences(List<Item> list_item){

        // Generating a list of criterions
        ArrayList<CriterionName> list_criterions_name = new ArrayList<>(Arrays.asList(new CriterionName("PRODUCTION_COST"),
                                            new CriterionName("ENVIRONMENT_IMPACT"),
                                            new CriterionName("CONSUMPTION"),
                                            new CriterionName("DURABILITY"),
                                            new CriterionName("NOISE"),
                                            new CriterionName("WEIGHT"),
                                            new CriterionName("POPULARITY")));


        Collections.shuffle(list_criterions_name);// We shuffle the criterion

        set_criterion_name_list(list_criterions_name);

        // Generating a list of values
        ArrayList<Value> list_values = new ArrayList<>(Arrays.asList(new Value("Very Good"),
                new Value("Good"),
                new Value("Bad"),
                new Value("Very Bad")));

        //Randomly create criterion value
        for(Item item : list_item){
            for(CriterionName criterionName : list_criterions_name){
                Random rand = new Random();
                Value value = list_values.get(rand.nextInt(list_values.size()));
                CriterionValue criterionValue = new CriterionValue(item, criterionName, value);
                add_criterion_value(criterionValue);
            }
        }
    }
}




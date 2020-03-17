package centralesupelec.engine.preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Item implements Serializable {

    private String name;
    private String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String get_name() {
        return this.name;
    }

    public String get_description() {
        return this.description;
    }

    public Value get_value(Preferences agent_pref, CriterionName criterion_name) {
        ArrayList<CriterionValue> list_values = agent_pref.get_criterion_values_list();
        Optional<CriterionValue> criterion_value = list_values.stream()
                .filter(elt -> elt.get_criterion_name().equals(criterion_name) && elt.get_item().equals(this))
                .findFirst();
        Value result = null;
        if (criterion_value.isPresent()) {
            result = criterion_value.get().get_value();
        }
        return result;
    }

    public Double get_score(Preferences agent_pref) {
        return agent_pref.get_score(this);
    }

    public static Item get_item_from_name(String name, List<Item> list_item) {
        Stream<Item> sp = list_item.stream();
        sp = list_item.stream();
        Optional<Item> answer = sp.filter(x -> x.get_name().equals(name)).findFirst();
        // Optional is returned.
        if (answer.isPresent()) {
            return answer.get();
        } else {
            System.out.println("This item is not on the list");
            return null;
        }
    }

    public static ArrayList<Item> generate_random_items(int nb_items) {
        ArrayList<Item> list_inital_items = new ArrayList<>();
        for (int j = 1; j <= nb_items; j++) {
            list_inital_items.add(new Item("Motor " + j, "A cool motor"));
        }
        return list_inital_items;
    }
}



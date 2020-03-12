package centralesupelec.engine.preferences;

public class CriterionValue {

    private CriterionName name;
    private Value value;
    private Item item;

    public CriterionValue(Item test, CriterionName name, Value value) {
        this.item = test;
        this.name = name;
        this.value = value;
    }

    public CriterionName get_criterion_name(){
        return this.name;
    }

    public Value get_value(){
        return this.value;
    }

    public Item get_item(){
        return this.item;
    }

}

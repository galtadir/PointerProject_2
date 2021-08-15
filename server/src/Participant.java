import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Participant extends Thread {

    private ArrayList<Item> items;
    private int money;
    private String name;
    private HashMap<Integer,Integer> historyAsset;

    public Participant(ArrayList<Item> items, int money, String name) {
        this.items = items;
        this.money = money;
        this.name = name;
        this.historyAsset = new HashMap<>();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void decreaseMoney(int amount){
        money-=amount;
    }

    public void increaseMoney(int amount){
        money+=amount;
    }

    public void addItem(Item item){
        items.add(item);
    }

    public void removeItem(Item item){
        items.remove(item);
    }

    public String getNameString() {
        return name;
    }
    public void endRound(int round){
        historyAsset.put(round,getTotalValuesCurrentRound());
        items = new ArrayList<>();
        money = 0;
    }

    public int getAssetsValue(){
        int assetsValue = 0;
        for(Item item:items){
            assetsValue+=item.getValue();
        }
        return assetsValue;
    }

    public int getTotalValuesCurrentRound(){
        return money+getAssetsValue();
    }

    public int getTotalValuesAllRounds(){
        int value = 0;
        for(Integer round:historyAsset.keySet()){
            value+=historyAsset.get(round);
        }
        return value;
    }
}

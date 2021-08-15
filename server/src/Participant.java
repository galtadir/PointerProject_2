import java.util.ArrayList;
import java.util.HashMap;

//abstract class for present all participant in the game
abstract class Participant extends Thread {

    private ArrayList<Item> items;
    private int money; //money in cash
    private String name;
    private HashMap<Integer,Integer> historyAsset; //save total values from prev rounds

    Participant(ArrayList<Item> items, int money, String name) {
        this.items = items;
        this.money = money;
        this.name = name;
        this.historyAsset = new HashMap<>();
    }

    ArrayList<Item> getItems() {
        return items;
    }

    void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    int getMoney() {
        return money;
    }

    void setMoney(int money) {
        this.money = money;
    }

    void decreaseMoney(int amount){
        money-=amount;
    }

    void increaseMoney(int amount){
        money+=amount;
    }

    void addItem(Item item){
        items.add(item);
    }

    void removeItem(Item item){
        items.remove(item);
    }

    String getNameString() {
        return name;
    }
    void endRound(int round){
        historyAsset.put(round,getTotalValuesCurrentRound());
        items = new ArrayList<>();
        money = 0;
    }

    int getAssetsValue(){
        int assetsValue = 0;
        for(Item item:items){
            assetsValue+=item.getValue();
        }
        return assetsValue;
    }

    int getTotalValuesCurrentRound(){
        return money+getAssetsValue();
    }

    int getTotalValuesAllRounds(){
        int value = 0;
        for(Integer round:historyAsset.keySet()){
            value+=historyAsset.get(round);
        }
        return value;
    }
}

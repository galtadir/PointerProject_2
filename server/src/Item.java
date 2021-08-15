import java.util.HashSet;

public class Item {

    private int value;
    private int minPrice;
    private int itemNumber;
    private String name;
    private int auctionPrice;
    private Participant owner;
    private int bidsCounter;
    private HashSet<Player> playersWhoBids;

    public Item(int value,int itemNumber, Participant owner) {
        this.value = value;
        this.minPrice = value/2;
        this.itemNumber = itemNumber;
        this.name = "Item " + itemNumber;
        this.auctionPrice = 0;
        this.owner = owner;
        this.bidsCounter = 0;
        this.playersWhoBids = new HashSet<>();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public String getName() {
        return name;
    }

    public int getAuctionPrice() {
        return auctionPrice;
    }

    public void setAuctionPrice(int auctionPrice) {
        this.auctionPrice = auctionPrice;
    }

    public Participant getOwner() {
        return owner;
    }

    public void setOwner(Participant owner) {
        this.owner = owner;
    }


    public void incrementBidsCounter(Player player){
        this.bidsCounter = this.bidsCounter+1;
        playersWhoBids.add(player);
    }

    public boolean checkWarFactorCondition(int numOfPlayers){
        if(playersWhoBids.size()>=2 && bidsCounter>=2*numOfPlayers){
            return true;
        }
        return false;
    }

    public int getBidsCounter() {
        return bidsCounter;
    }
}

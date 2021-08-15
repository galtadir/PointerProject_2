import java.util.HashSet;

//represent item in the auction
class Item {

    private int value;
    private int minPrice;
    private String name;
    private int auctionPrice;
    private Participant owner;
    private int bidsCounter;
    private HashSet<Player> playersWhoBids;

    Item(int value, int itemNumber, Participant owner) {
        this.value = value;
        this.minPrice = value/2;
        this.name = "Item " + itemNumber;
        this.auctionPrice = 0;
        this.owner = owner;
        this.bidsCounter = 0;
        this.playersWhoBids = new HashSet<>();
    }

    int getValue() {
        return value;
    }

    void setValue(int value) {
        this.value = value;
    }

    int getMinPrice() {
        return minPrice;
    }


    String getName() {
        return name;
    }

    int getAuctionPrice() {
        return auctionPrice;
    }

    void setAuctionPrice(int auctionPrice) {
        this.auctionPrice = auctionPrice;
    }

    Participant getOwner() {
        return owner;
    }

    void setOwner(Participant owner) {
        this.owner = owner;
    }


    void incrementBidsCounter(Player player){
        this.bidsCounter = this.bidsCounter+1;
        playersWhoBids.add(player);
    }

    boolean checkWarFactorCondition(int numOfPlayers){
        return playersWhoBids.size() >= 2 && bidsCounter >= 2 * numOfPlayers;
    }

    int getBidsCounter() {
        return bidsCounter;
    }
}

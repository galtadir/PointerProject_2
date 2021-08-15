import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//represent the dealer in the game
public class Dealer extends Participant{

    private ArrayList<Player> activePlayers;
    private ArrayList<Player> playersThatLeave;
    private Login login;
    private HashMap<Character,Integer> params;
    private Scanner sc = new Scanner(System.in);
    private GameStatus status;
    private int itemCounter = 1;
    private int accumulateValues;
    private Item currentItem;
    private int howManyItems;
    private long auctionEndTime;
    private int round;
    private double warFactor;


    Dealer(String[] args) {
        super(new ArrayList<>(), 0,"Dealer");
        activePlayers = new ArrayList<>();
        login = new Login(this);
        this.warFactor = 0.01;
        params = getParams(args);
        status = GameStatus.WaitingForBegin;
        this.round = 0;
        playersThatLeave = new ArrayList<>();
    }

    @Override
    public void run(){
        try{
            while (true){
                initDealerItems(params.get('n'), params.get('m'),params.get('x'), params.get('y'));
                round++;
                for(Player player : activePlayers){
                    player.setMoney(accumulateValues);
                }
                waitingForDealerToBegin();
                miniGame();
                miniGameSummaryAndResetAsset();
                status = GameStatus.WaitingForBegin;
                sendMsgToAllClient("Waiting for dealer to decide if to start another Mini-Game");
                System.out.println("Please enter exit to finish or anything else to continue ");
                String line  = sc.nextLine();
                if(line.equals("exit")){
                    entireGameSummary();
                    System.exit(0);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //init params
    private HashMap<Character,Integer> getParams(String[] args){
        HashMap<Character,Integer> params = new HashMap<>();
        params.put('n',10);
        params.put('m',20);
        params.put('x',100);
        params.put('y',2000);

        try {
            File myObj = new File("Dealer.config");

            Scanner myReader = new Scanner(myObj);
            while (true) {
                String data = myReader.nextLine();
                if(data.equals("}")){
                    break;
                }
                if(data.contains(":")){
                    data = data.replace(",","");
                    if(data.charAt(0)=='w'){
                        warFactor = Double.parseDouble(data.substring(2));
                        break;
                    }
                    try{
                        int number = Integer.parseInt(data.substring(2));
                        if(number>0)
                            params.put(data.charAt(0),number);
                    }catch (Exception ignored){
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException ignored) {
        }
        if(args.length>0){
            try{
                int number = Integer.parseInt(args[0]);
                if(number>0)
                    params.put('n',number);
            }catch (Exception ignored){
            }
        }
        if(args.length>1){
            try{
                int number = Integer.parseInt(args[1]);
                if(number>0)
                    params.put('m',number);
            }catch (Exception ignored){
            }
        }
        if(args.length>2){
            try{
                int number = Integer.parseInt(args[2]);
                if(number>0)
                    params.put('x',number);
            }catch (Exception ignored){
            }
        }
        if(args.length>3){
            try{
                int number = Integer.parseInt(args[3]);
                if(number>0)
                    params.put('y',number);
            }catch (Exception ignored){
            }
        }
        if(args.length>4){
            try{
                double number = Double.parseDouble(args[4]);
                if(number>=0 && number<=1)
                    warFactor = number;
            }catch (Exception ignored){
            }
        }


        return params;
    }

    private void initDealerItems(int n, int m, int x, int y){
        ArrayList<Item> items = new ArrayList<>();
        int accumulateValue = 0;
        Random random = new Random();
        howManyItems = random.nextInt((m - n) + 1) + n;
        System.out.println("Auction items: ");
        for(int i=0 ; i<howManyItems ; i++){
            int currentValue = round(random.nextInt((y - x) + 1) + x);
            Item newItem = new Item(currentValue,itemCounter, this);
            System.out.format("%6S %3S %5S", newItem.getName() , " = "+ " $", newItem.getValue());
            System.out.println();
            itemCounter++;
            items.add(newItem);
            accumulateValue+=currentValue;
        }
        this.setItems(items);
        this.accumulateValues = accumulateValue;
    }

    private int round(int n) {
        // Smaller multiple
        int a = (n / 10) * 10;

        // Larger multiple
        int b = a + 10;

        // Return of closest of two
        return (n - a > b - n)? b : a;
    }

    private void waitingForDealerToBegin() throws InterruptedException {
        sendMsgToAllClient("waiting for dealer to start the game");
        System.out.println("enter something to start the game");
        sc.nextLine();
        String msgStartGame = "Starting Mini-Game #"+ round +" with "+howManyItems+" items, total suggested value of items is $"+accumulateValues+ " and that also the amount of $ in your pocket.";
        sendMsgToAllClient(msgStartGame);
        status = GameStatus.InAuction;
    }

    void transferPlayers(){
        ArrayList<Player> waitingPlayers = login.getWaitingPlayers();
        if(waitingPlayers.size()>0){
            for(Player player: waitingPlayers){
                player.setStatus(PlayerStatus.Play);
                activePlayers.add(player);
            }
            waitingPlayers.clear();
        }

    }

    void sendMsgToAllClient(String msg){
        System.out.println(msg);
        for(Player p: activePlayers){
            p.send(msg);
        }
        login.sendMsgToAllWaiting(msg);
    }

    private void miniGame() throws InterruptedException {
        for(Item item:this.getItems()){
            Thread.sleep(1);
            transferPlayers();
            currentItem = item;
            biddingWar(item);
            if(item.checkWarFactorCondition(activePlayers.size())){
                sendMsgToAllClient("Encounter war factor condition");
                int newValue = item.getValue() + (int)(warFactor*item.getValue()*item.getBidsCounter()/activePlayers.size());
                sendMsgToAllClient(item.getName() + " new value is " + newValue);
                item.setValue(newValue);
            }
            currentItem=null;
        }
        updateDealerItems();

    }

    private void  biddingWar( Item item) throws InterruptedException {
        sendMsgToAllClient("");
        String msg = item.getName() + " is being auctioned. Suggested value is $" + item.getValue();
        sendMsgToAllClient(msg);
        sendMsgToAllClient("10 seconds left");
        auctionEndTime = System.currentTimeMillis()+ 10000;
        while (System.currentTimeMillis()<auctionEndTime){
            Thread.sleep(auctionEndTime - System.currentTimeMillis());
        }
        if(currentItem.getOwner()==this){
            sendMsgToAllClient("No one won the item");
        }
        else{
            sendMsgToAllClient(currentItem.getOwner().getNameString() + " won the item for $" + currentItem.getAuctionPrice());
            this.increaseMoney(currentItem.getAuctionPrice());
            currentItem.getOwner().decreaseMoney(currentItem.getAuctionPrice());
            currentItem.getOwner().addItem(currentItem);
        }

    }


    GameStatus getStatus() {
        return status;
    }

    Item getCurrentItem() {
        return currentItem;
    }

    void removePlayer(Player player){
        player.setStatus(PlayerStatus.CurrentRoundFinished);
        player.send("goodbye");
        activePlayers.remove(player);
        sendMsgToAllClient(player.getNameString() + " left");
        playersThatLeave.add(player);
    }

    void makeABid(int bid, Player player){
        if(bid > currentItem.getMinPrice()){
            if(bid> currentItem.getAuctionPrice()){
                if( currentItem.getOwner()!=this){
                    currentItem.getOwner().removeItem(currentItem);
                }
                currentItem.setOwner(player);
                currentItem.setAuctionPrice(bid);
                sendMsgToAllClient(player.getNameString() + " bid $" + bid + " for current item - fair offer");
            }
            else if(bid== currentItem.getAuctionPrice()){
                currentItem.getOwner().removeItem(currentItem);
                currentItem.setOwner(this);
                currentItem.setAuctionPrice(bid);
                sendMsgToAllClient(player.getNameString() + " bid $" + bid + " for current item - fair offer");
            }
            else{
                player.send("offer is too small");
                sendMsgToAllClient(player.getNameString() + " bid $" + bid + " for current item - too small offer");
            }
        }
        else {
            player.send("offer is too small");
            sendMsgToAllClient(player.getNameString() + " bid $" + bid + " for current item - too small offer");
        }
        sendMsgToAllClient("10 seconds left");
        currentItem.incrementBidsCounter(player);
        auctionEndTime = System.currentTimeMillis()+ 10000;
    }

    int getAccumulateValues() {
        return accumulateValues;
    }

    int getHowManyItems() {
        return howManyItems;
    }

    private void updateDealerItems(){
        ArrayList<Item> items = new ArrayList<>();
        for (Item item: this.getItems()){
            if(item.getOwner()==this){
                items.add(item);
            }
        }
        this.setItems(items);
    }

    int getRound() {
        return round;
    }

    private void miniGameSummaryAndResetAsset(){
        sendMsgToAllClient("Mini-Game #" + round + " finished");
        HashSet<Participant> winners = new HashSet<>();
        winners.add(this);
        int winnerValue = this.getTotalValuesCurrentRound();
        sendMsgToAllClient("Dealer assets: $" + this.getMoney() + " in Cash and $"+ this.getAssetsValue()+" value of assets, total value: $" + this.getTotalValuesCurrentRound());
        for(Player player: activePlayers){
            sendMsgToAllClient( player.getNameString() +" assets: $" + player.getMoney() + " in Cash and $"+ player.getAssetsValue()+" value of assets, total value: $"+ player.getTotalValuesCurrentRound());
            winnerValue = updateWinnersRound(winners, winnerValue,  player);
        }
        for(Player player: playersThatLeave){
            if(player.getStatus()==PlayerStatus.CurrentRoundFinished){
                sendMsgToAllClient( player.getNameString() +" assets: $" + player.getMoney() + " in Cash and $"+ player.getAssetsValue()+" value of assets, total value: $"+ player.getTotalValuesCurrentRound());
                winnerValue = updateWinnersRound(winners, winnerValue,  player);
            }
        }
        printWinners(winners,winnerValue,"#" + round);

        this.endRound(round);
        for(Player player:activePlayers){
            player.endRound(round);
        }
        for (Player player:playersThatLeave){
            player.setStatus(PlayerStatus.Finish);
            player.endRound(round);
        }
    }

    private void entireGameSummary(){
        sendMsgToAllClient("Dealer decided to end the game!");

        HashSet<Participant> winners = new HashSet<>();
        winners.add(this);

        int winnerValue = this.getTotalValuesAllRounds();
        sendMsgToAllClient("Dealer total assets: $" + this.getTotalValuesAllRounds());
        for(Player player: activePlayers){
            sendMsgToAllClient(player.getNameString() + " total assets: $" + player.getTotalValuesAllRounds());
            winnerValue = updateWinnersEntire(winners, winnerValue,  player);
        }
        for(Player player: playersThatLeave){
            sendMsgToAllClient(player.getNameString() + " total assets: $" + player.getTotalValuesAllRounds());
            winnerValue = updateWinnersEntire(winners, winnerValue,  player);
        }
        printWinners(winners,winnerValue,"the entire game");
        sendMsgToAllClient("The game is over");

    }

    private int updateWinnersRound(HashSet<Participant> winners, int winnerValue, Player player){
        if(player.getTotalValuesCurrentRound()>winnerValue){
            winners.clear();
            winners.add(player);
            return player.getTotalValuesCurrentRound();
        }
        else if(player.getTotalValuesCurrentRound()==winnerValue){
            winners.add(player);
            return player.getTotalValuesCurrentRound();
        }
        return winnerValue;
    }

    private int updateWinnersEntire(HashSet<Participant> winners, int winnerValue, Player player){
        if(player.getTotalValuesAllRounds()>winnerValue){
            winners.clear();
            winners.add(player);
            return player.getTotalValuesAllRounds();
        }
        else if(player.getTotalValuesAllRounds()==winnerValue){
            winners.add(player);
            return player.getTotalValuesAllRounds();
        }
        return winnerValue;
    }

    private void printWinners(HashSet<Participant> winners, int winnerValue, String part){
        String winnersNames = "";
        for(Participant winner:winners ){
            winnersNames= winnersNames + winner.getNameString()+ " ";
        }
        String msg = "The winner for " + part;
        if(winners.size()==1){
            msg += " is ";
        }
        else {
            msg += " are ";
        }
        msg+=winnersNames;
        msg+="for $" + winnerValue + " value";
        sendMsgToAllClient( msg);

    }
}



import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Login extends Thread {

    private ArrayList<Player> waitingPlayers;
    private int playersCounter;
    private Dealer dealer;
    private long createdMillis;

    Login(Dealer dealer){
        waitingPlayers = new ArrayList<>();
        playersCounter=1;
        this.dealer = dealer;
        this.createdMillis = System.currentTimeMillis();
        start();
    }


    @Override
    public void run() {

        try(ServerSocket serverSocket = new ServerSocket(5000)){
            while (true){
                String newPlayerName = "player " + (playersCounter);
                playersCounter++;
                Player player = new Player(serverSocket.accept(),dealer , newPlayerName,dealer.getAccumulateValues());
                int age = getAgeInSeconds();
                String playerMsg = newPlayerName+ " join in "+age + " sec";
                dealer.sendMsgToAllClient(playerMsg);
                sendMsgToAllWaiting(playerMsg);
                waitingPlayers.add(player);
                player.start();
                String msg = "You are " +player.getNameString();
                if(dealer.getStatus()==GameStatus.WaitingForBegin){
                    msg+= ", we are waiting for the game to begin.";
                }
                else if(dealer.getStatus()==GameStatus.InAuction){
                    msg+=", we are waiting for a Bidding-War on "+dealer.getCurrentItem().getName()+ " to end and then you will be joining the game.";
                }
                player.send(msg);
                player.send("You can enter Exit at any time to exit the game");
                if(dealer.getStatus()==GameStatus.WaitingForBegin){
                    dealer.transferPlayers();
                }
                else if(dealer.getStatus()==GameStatus.InAuction){
                    String msgStartGame = "You Join Mini-Game #"+ dealer.getRound() +" with "+dealer.getHowManyItems()+" items, total suggested value of items is $"+dealer.getAccumulateValues()+ " and that also the amount of $ in your pocket.";
                    player.send(msgStartGame);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsgToAllWaiting(String msg){
        for(Player p: waitingPlayers){
            p.send(msg);
        }
    }

    private int getAgeInSeconds() {
        long nowMillis = System.currentTimeMillis();
        return (int)((nowMillis - this.createdMillis) / 1000);
    }


    ArrayList<Player> getWaitingPlayers() {
        return waitingPlayers;
    }
}

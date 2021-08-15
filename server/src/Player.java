import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

//represent players in the game and responsible to communicate with client side
public class Player extends Participant {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Dealer dealer;
    private PlayerStatus status;

    Player(Socket socket, Dealer dealer, String name, int money) {
        super(new ArrayList<>(), money, name);
        this.socket = socket;
        this.dealer = dealer;
        this.status = PlayerStatus.Waiting;
        try{
            this.input = new BufferedReader((new InputStreamReader(socket.getInputStream())));
            this.output = new PrintWriter(socket.getOutputStream(),true);
        }catch (IOException e){
            System.out.println("exception... " + e.getMessage());
        }
    }

    @Override
    public void run(){
        try{
            String nextInput;
            while (true){
                nextInput = input.readLine();
                if(nextInput.equals("exit")){
                    dealer.removePlayer(this);
                    break;
                }
                try {
                    int bid = Integer.parseInt(nextInput);
                    if(status==PlayerStatus.Waiting){
                        send("You are still waiting to join the game");
                    }
                    else if(dealer.getCurrentItem()==null){
                        send("a Bidding-War has not started yet");
                    }
                    else if(bid>this.getMoney()){
                        send("you do not have enough money");
                    }
                    else{
                        dealer.makeABid(bid,this);
                    }
                } catch (NumberFormatException ex) {
                    send("invalid input");
                }
            }

        }catch (IOException e){
            System.out.println("exception... " + e.getMessage());
        }
        finally {
            try{
                socket.close();
            }catch (IOException e){
                System.out.println("exception... " + e.getMessage());
            }

        }

    }

    void send(String msg) {
        output.println(msg);
    }

    void setStatus(PlayerStatus status) {
        this.status = status;
    }

    PlayerStatus getStatus() {
        return status;
    }
}

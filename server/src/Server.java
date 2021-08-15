import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {

    public static void main(String[] args) throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        System.out.println("Your ip address is: " + ip +", your port is 5000");
        Dealer dealer = new Dealer(args);
        dealer.start();
    }
}

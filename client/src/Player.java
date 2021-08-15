import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


//represent client in the game in the client side
public class Player {
    private static String ip = "";
    private static int port = 0;
    private static Socket socket = null;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        getIpAndPort();
    }

    //get ip address and port number to connect the server. if not succeed try again
    private static void getIpAndPort(){
        System.out.println("Please enter ip address:");
        ip = sc.nextLine();
        System.out.println("Please enter port number:");
        String portStr = sc.nextLine();
        try{
            port = Integer.parseInt(portStr);
            connect();
        }catch (Exception e){
            System.out.println(portStr + " is not number");
            getIpAndPort();
        }

    }

    //try connect to the server
    private static void connect(){
        try {
            socket = new Socket(ip,port);
            Read r = new Read(socket);
            Write w = new Write(socket);
            r.join();
            w.join();
        } catch (IOException | InterruptedException e) {
            System.out.println("Exception: can't connect to: " + ip + " from port: " + port);
            getIpAndPort();
        } finally{
            try {
                if(socket!=null)
                    socket.close();
            }
            catch (Exception e) {
                System.out.println("Exception:" + e.getMessage());
            }
        }

    }



}

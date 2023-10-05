import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    String host = "localhost";
    int port = 1024;
    boolean initialized = false, winner = false;
    Console console;

    Socket client1, client2;
    InputStreamReader c1_ISR, c2_ISR;
    OutputStreamWriter c1_OSW, c2_OSW;
    BufferedReader c1_bRead, c2_bRead;
    BufferedWriter c1_bWrite, c2_bWrite;

    ServerSocket serverSocket;

    //Thread gameThread, socketThread, client1, client2;
    ConnectFour C4;

    public Server(){
        C4 = new ConnectFour();
        console = System.console();

        while(true) {
            //wait for clients
            if (!initialized) {
                try {
                    initialized = initNet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //play game
            if (initialized) {
                System.out.println("Starting Game!");
                try {
                    GameOn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Sets up socket specifications for both clients;
    private boolean initNet() throws IOException{
        int attempts = 0;

        serverSocket = new ServerSocket(port);
        client1 = serverSocket.accept();
        System.out.println("Client 1 Connected");
        client2 = serverSocket.accept();
        System.out.println("Client 2 Connected");

        c1_ISR = new InputStreamReader(client1.getInputStream());
        c2_ISR = new InputStreamReader(client2.getInputStream());

        c1_OSW = new OutputStreamWriter(client1.getOutputStream());
        c2_OSW = new OutputStreamWriter(client2.getOutputStream());

        c1_bRead = new BufferedReader(c1_ISR);
        c2_bRead = new BufferedReader(c2_ISR);

        c1_bWrite = new BufferedWriter(c1_OSW);
        c2_bWrite = new BufferedWriter(c2_OSW);

        String initMsg1, initMsg2;
        while (true){
            initMsg1 = c1_bRead.readLine();
            if (initMsg1.equals("200")){
                System.out.println("Client 1 Ready!");
                attempts = 0;
                break;
            }
            //c1_bRead.reset();
            attempts++;
            if(attempts >= 5000) return false;
        }
        while (true){
            initMsg2 = c2_bRead.readLine();
            if (initMsg2.equals("200")) {
                System.out.println("Client 2 Ready!");
                updateBoard(1); //send initial board to inform clients that the game has started
                return true;
            }
            //c1_bRead.reset();
            attempts++;
            if(attempts >= 5000) return false;

        }
    }

    private void GameOn() throws IOException{
        String msgFrom1, msgFrom2;
        while (!winner) {
            //client 1 turn
            System.out.println("Waiting For Client 1");
            while (true) {
                msgFrom1 = c1_bRead.readLine();
                System.out.print("P1 MSG Recieved: " + msgFrom1);
                if (msgFrom1 != null && viableOption(msgFrom1)) {
                    System.out.print(" [VIABLE]\n");
                    C4.p1Insert(Integer.parseInt(msgFrom1));
                    msgFrom1 = null;
                   // c1_bRead.reset();
                    break;
                }
            }

            updateBoard(2);
            //check for p1 win
            if (C4.checkForWin(C4.p1Char, C4.getLastXY() / 10, C4.getLastXY() % 10)) {
                System.out.println("Player 1 Wins");
                EndGame(1);
                winner = true;
                return;
            }

            //client 2 turn
            System.out.println("Waiting For Client 2");
            while (true) {
                msgFrom2 = c2_bRead.readLine();
                System.out.print("P2 MSG Recieved: " + msgFrom2);
                if (msgFrom2 != null && viableOption(msgFrom2)) {
                    System.out.print(" [VIABLE]\n");
                    C4.p2Insert(Integer.parseInt(msgFrom2));
                    msgFrom2 = null;
                    //c2_bRead.reset();
                    break;
                }
            }
            updateBoard(1);
            //check for p2 win
            if (C4.checkForWin(C4.p2Char, C4.getLastXY() / 10, C4.getLastXY() % 10)) {
                System.out.println("Player 2 Wins");
                EndGame(2);
                winner = true;
                return;
            }
        }

    }

    private void EndGame(int n){
        try {
            SendWinMessage(n);
        }catch (IOException e){

        }
        winner = true;
    }

    private void updateBoard(int p) throws IOException{
        System.out.println("Updating Board...");
        String yourTurn = "!";

        //place 'yourTurn' char at beginning of message

            if (p == 1) c1_bWrite.write(yourTurn);
            else c2_bWrite.write(yourTurn);

            c1_bWrite.append(C4.getGridString());
            c1_bWrite.newLine();
            c1_bWrite.flush();

            c2_bWrite.append(C4.getGridString());
            c2_bWrite.newLine();
            c2_bWrite.flush();

    }

    private void SendWinMessage(int n) throws IOException{
        String msg;
        if (n == 1){
            msg = "#P1##";
        } else msg = "#P2#";
        c1_bWrite.write(msg);
        c1_bWrite.newLine();
        c1_bWrite.flush();

        c2_bWrite.append(msg);
        c2_bWrite.newLine();
        c2_bWrite.flush();
    }

    private boolean viableOption(String s){
        String[] options = {"1", "2", "3", "4", "5", "6", "7"};
        for (int i = 0; i < options.length; i++){
            if (s.equals(options[i])) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        new Server();
    }
}

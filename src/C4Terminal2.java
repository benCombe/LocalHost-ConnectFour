import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class C4Terminal2 {
    Client client;
    BufferedReader bRead;
    ConsoleColours CC;

    Thread UIThread, clientThread;

    boolean yourTurn, playing = false;

    public C4Terminal2(){
        bRead = new BufferedReader(new InputStreamReader(System.in));
        CC = new ConsoleColours();
        initMenu();
        //client.setMessage("200"); //sets initial message to "200" to notify server that THIS player is ready
        try {
            client.sendMessage("200");
        } catch (IOException e){
            System.out.println("Failed to Send 'Ready' Message");
        }
        clientThread.start();
        playGame();

        try {
            if (bRead != null) bRead.close();
        } catch (IOException e){

        }

    }

    private void initMenu(){
        int opt;
        while(true) {
            jout(" ==== CONNECT 4 ==== \n");
            jout("1. Start Game" +
                    "\n2. Exit" +
                    "\n\nOPTION: ");
            opt = Integer.parseInt(jin());
            if (opt != 1) break;
            else {
                try {
                    client = new Client();
                    clientThread = new Thread(client);
                    playing = true;
                    System.out.println("Waiting For Other Player To Join...");
                } catch (NullPointerException e){
                    jout("\nFAILED TO CONNECT TO SERVER\n");
                    e.printStackTrace();
                }
            }
            break;
        }
    }

    private void playGame(){
        while (playing){
            waitForP2();
            if (playing)
            makeSelection();
        }
        client.Close();
    }

    private void waitForP2(){
        String incoming = null;
        int indxStart, col;
        //wait to receive new grid
        while (incoming == null){
            client.waitToReceive();
            try{
                Thread.sleep(500);
            }catch (InterruptedException e){

            }
            incoming = client.getMessage();
            if (incoming != null){
                if (incoming.length() > 5) break;
                else {

                    if (client.EOGReceived){
                        playing = false;
                        System.out.println(client.winner + " WINS!");
                        return;
                    }
                    incoming = null;
                }
            }

        }
       // client.setNeedToReceive(false);
        if (incoming.charAt(0) == '!'){
            yourTurn = true;
            indxStart = 1;
        }
        else{
            yourTurn = false;
            indxStart = 0;
        }

        //print out the grid and prompt for selection
        printGrid(incoming, indxStart);

    }

    private void makeSelection(){
        int col;
        if (yourTurn){
            while (true) {
                jout("\rChoose a Column (1-7): ");
                col = Integer.parseInt(jin());
                if (col > 0 && col < 8) break;
            }
            //send selection
            try{
                client.sendMessage(Integer.toString(col));
                yourTurn = false;
            } catch (IOException e){
                jout("\nFailed To Send");
            }
        }
        else{
            jout("\nWaiting For Other Player...\n");
            return;
        }

    }

    private void printGrid(String s, int indx){
        s = s.substring(indx);
        int charIndex;
        char currChar;
        for (int i = 0; i < 6; i++){
            jout("|\t");
            for (int j = 0; j < 7; j++){
                charIndex = (i*7)+j;
                currChar = s.charAt(charIndex);
                if (currChar == 'O') jout(CC.RED);
                else if (currChar == 'X') jout(CC.YELLOW);
                jout(currChar + "\t" + CC.RESET);
            }
            jout("|\n");
        }
        jout("\t1\t2\t3\t4\t5\t6\t7");
    }

    private boolean receivedEndOfGame(){
        return client.EOGReceived;
    }

    //UI Methods
    //keeps code clean and reduces the amount of try/catch needed for reading user input
    private void jout(String s){
        System.out.print(s);
    }
    private void jout(char s){
        System.out.print(s);
    }
    private String jin(){
        String result;
        try {
            result = bRead.readLine();
        } catch (IOException e){
            result = null;
        }
        return result;
    }

    public static void main(String[] args) {
        new C4Terminal2();
    }

}

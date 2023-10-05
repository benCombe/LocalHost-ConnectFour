import java.io.*;
import java.net.Socket;
import java.nio.channels.ScatteringByteChannel;
import java.util.Scanner;

public class Client implements Runnable{
    String host = "localhost";
    int port = 1024;

    String received;
    String msgToSend;
    String status;
    boolean open, EOGReceived;
    String winner;

    Socket socket;
    InputStreamReader ISR;
    OutputStreamWriter OSWriter;
    BufferedReader bRead;
    BufferedWriter bWrite;

    Scanner scanner;

    public Client(){
        EOGReceived = false;
        try {
            socket = new Socket(host, port);
            ISR = new InputStreamReader(socket.getInputStream());
            OSWriter = new OutputStreamWriter(socket.getOutputStream());

            bRead = new BufferedReader(ISR);
            bWrite = new BufferedWriter(OSWriter);

            scanner = new Scanner(System.in);

            open = true;

        }catch (IOException e){
            System.out.println("ERROR: Could Not Connect to '" + host + ":" + port + "'");
        }
    }

    public void setMessage(String s){
        this.msgToSend = s;
        System.out.println("Message Set to '" + s + "'");
    }

    public String getMessage(){
        String msg = received;
        //checks if received message declares a winner '(###Player n###)', stores winner name
        if (msg.charAt(0) == '#'){
            winner = msg.substring(1,3);
            EOGReceived = true;
        }
        received = null;
        return msg;
    }

    public void Close(){
        open = false;
    }

    private void wait(int i){
        try{
            Thread.sleep(i);
        } catch (InterruptedException e){

        }
    }

    public void sendMessage(String msg) throws IOException{
        //System.out.println("Sending: " + msg);
        bWrite.write(msg);
        bWrite.newLine();
        bWrite.flush();
    }


    public void waitToReceive(){
        String r = null;
        while (r == null){
            //setStatus("Waiting for message...");
            //System.out.println(status); //remove later
            try {
                r = bRead.readLine();
            } catch (IOException e){

            }
            wait(500);
        }
        received = r;
    }

    @Override
    public void run() {
        msgToSend = null;
        while (open);
        try {
            if (socket != null) socket.close();
            if (ISR != null) ISR.close();
            if (OSWriter != null) OSWriter.close();
            if (bRead != null) bRead.close();
            if (bWrite != null) bWrite.close();
            if (scanner != null) scanner.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

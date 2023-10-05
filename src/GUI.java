import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GUI extends JDialog {
    private JPanel contentPane;
    private JButton btn_StartGame;
    private JButton btn_Exit;
    private JTextArea Board;
    private JButton a1Button;
    private JButton a2Button;
    private JButton a3Button;
    private JButton a4Button;
    private JButton a5Button;
    private JButton a6Button;
    private JButton a7Button;
    private JTextField txt_Status;
    private JButton playWithCPUButton;
    private JButton sendSelectionButton;
    private JButton board4;
    private JButton board7;
    private JButton board8;
    private JButton board3;
    private JButton board2;
    private JButton board0;
    private JButton board1;
    private JButton board5;
    private JButton board14;
    private JButton board6;
    private JButton board21;
    private JButton board28;
    private JButton board35;
    private JButton board15;
    private JButton board9;
    private JButton board10;
    private JButton board11;
    private JButton board12;
    private JButton board13;
    private JButton board16;
    private JButton board17;
    private JButton board18;
    private JButton board19;
    private JButton board20;
    private JButton board22;
    private JButton board23;
    private JButton board24;
    private JButton board25;
    private JButton board26;
    private JButton board27;
    private JButton board29;
    private JButton board30;
    private JButton board31;
    private JButton board32;
    private JButton board33;
    private JButton board34;
    private JButton board36;
    private JButton board37;
    private JButton board38;
    private JButton board39;
    private JButton board40;
    private JButton board41;

    private JButton[] colSelect = {a1Button, a2Button, a3Button, a4Button, a5Button, a6Button, a7Button};
    private JButton[] C4Board = {board0,board1,board2,board3,board4,board5,board6,board7,board8,board9,board10,
                                board11, board12, board13, board14, board15,board16,board17,board18,board19,board20,
                                board21,board22,board23,board24,board25,board26,board27,board28,board29,board30,
                                board31,board32,board33,board34,board35,board36,board37,board38,board39,board40,board41};


    int col, tempCol;
    boolean CPUGame = false, yourTurn = false;
    Client client;
    Thread clientThread;//, gameThread;
    ConnectFour C4;
    String incoming;


    public GUI() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btn_StartGame);

        sendSelectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectionSent();
            }
        });

        playWithCPUButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                playWithCPUButton.setEnabled(false);
                btn_StartGame.setEnabled(false);
                playCPU();
            }
        });

        btn_StartGame.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                playWithCPUButton.setEnabled(false);
                btn_StartGame.setEnabled(false);
                onStartGame();
            }
        });

        btn_Exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

        //set action listeners for column selection + color
        for (int i = 0; i < colSelect.length; i++){
            int sel = i+1;
            colSelect[i].setBackground(Color.LIGHT_GRAY);
            colSelect[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    columnSelect(sel);
                }
            });
        }

        //setupBoard
        for (int i = 0; i < C4Board.length; i++){
            C4Board[i].setBackground(Color.CYAN);
            C4Board[i].setEnabled(false);
            C4Board[i].setText(null);
            C4Board[i].setPreferredSize(new Dimension(5,45));
        }

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onStartGame() {
        // add your code here
        setAllColor(C4Board, Color.CYAN);
        setupClient();
        playPlayer();
    }

    private void onExit() {
        // add your code here if necessary
        try {
            if (clientThread != null) clientThread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        dispose();
    }

    private void setupClient(){
        client = new Client();
        clientThread = new Thread(client);
        try {
            client.sendMessage("200");
        } catch (IOException e){
            System.out.println("Failed to Send 'Ready' Message");
        }
        clientThread.start();
    }

    private void playPlayer(){
        C4 = new ConnectFour();
        CPUGame = false;
        waitForP2();
    }

    private void playCPU(){
        setAllColor(C4Board, Color.CYAN);
        C4 = new ConnectFour();
        CPUGame = true;
        enableColButtons(true);
        txt_Status.setText("Select a Column");
    }

    //Waits for incoming message from server or CPU
    private void waitForP2(){
        txt_Status.setText("Waiting for Other Player");
        setAllColor(colSelect, Color.LIGHT_GRAY);
        tempCol = -1;
        enableColButtons(false);
        if (CPUGame) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            C4.p2Insert(C4.randomSelection());
            printBoard(C4.getGridString(), 0);
            if (C4.checkForWin(C4.p2Char, C4.getLastXY()/10, C4.getLastXY()%10)){
                txt_Status.setText("YOU LOST!");
                EndGame();
                return;
            }
            txt_Status.setText("Select a Column!");
            enableColButtons(true);
        }
        else{
            incoming = null;
            int indxStart;
            while (incoming == null){
                client.waitToReceive();
                System.out.println("Waiting For Board Update...");
                //sleep then check if incoming is no longer null
                try {
                 Thread.sleep(500);
                }catch (InterruptedException e){

                }
                incoming = client.getMessage();
                if (incoming != null){
                    if (incoming.length() > 5) break;
                    else System.out.println("Server: " + incoming);
                }
            }
            yourTurn = incoming.charAt(0) == '!';
            System.out.println("Your Turn: " + yourTurn);
            if (yourTurn) indxStart = 1;
            else indxStart = 0;

            if (client.EOGReceived){
                EndGame();
            }
            //print out the grid and prompt for selection
            enableColButtons(yourTurn);
            printBoard(incoming, indxStart);
            if (yourTurn){
                enableColButtons(true);
                txt_Status.setText("Select a Column");
            }

        }
    }

    private void printBoard(String s, int start){

        //clearBoard();
        if (client.EOGReceived){
            EndGame();
        }
        System.out.println("UPDATED BOARD");
        int boardIndex;
        s = s.substring(start);
        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 7; j++){
                boardIndex = (i*7)+j;
                if (s.charAt(boardIndex) == C4.p1Char) C4Board[boardIndex].setBackground(Color.RED);
                else if (s.charAt(boardIndex) == C4.p2Char) C4Board[boardIndex].setBackground(Color.YELLOW);
                else C4Board[boardIndex].setBackground(Color.CYAN);
            }
        }

        if (!yourTurn) waitForP2();
        else txt_Status.setText("Waiting For Other Player");
    }

    private void setAllColor(JButton[] b, Color c){
        for (int i = 0; i < b.length; i++){
            b[i].setBackground(c);
        }
    }
    private void enableColButtons(boolean b){
        for (int i = 0; i < 7; i++){
            colSelect[i].setEnabled(b);
        }
    }

    //Select temp value of column
    private void columnSelect(int i){
        this.tempCol = i;
        for (int j = 0; j < colSelect.length; j++){
            colSelect[j].setBackground(Color.LIGHT_GRAY);
        }
        colSelect[i-1].setBackground(Color.BLUE);
    }

    //set column selection to tempCol, insert into board or send to server
    private void selectionSent(){

        col = tempCol;
        if (CPUGame){
            C4.p1Insert(col);
            printBoard(C4.getGridString(), 0);
            if (C4.checkForWin(C4.p1Char, C4.getLastXY() / 10, C4.getLastXY() % 10)){
                txt_Status.setText("YOU WIN!");
                EndGame();
                return;
            }
        }
        else{
            try {
                client.sendMessage(Integer.toString(col));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        waitForP2();
    }

    private void EndGame(){
        if (!CPUGame){
            txt_Status.setText("WINNER " + client.winner);
        }
        btn_StartGame.setEnabled(true);
        playWithCPUButton.setEnabled(true);
    }

    public static void main(String[] args) {
        GUI dialog = new GUI();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}

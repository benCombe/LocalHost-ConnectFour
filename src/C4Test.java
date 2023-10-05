import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class C4Test {
    ConnectFour C4;
    BufferedReader bRead;
    public C4Test(){

        C4 = new ConnectFour();
        bRead = new BufferedReader(new InputStreamReader(System.in));

        String input;
        boolean win = false;
        while (true) {
            C4.print();
            jout("\n");
            while (true){
                jout("\rSelect a column: ");
                input = jin();
                if (viableOption(input)){
                    if (C4.p1Insert(Integer.parseInt(input))){
                        C4.print();
                        System.out.println("LAST PLACED: [" + C4.getLastXY() / 10 + ", " + C4.getLastXY() % 10 + "]");
                        win = C4.checkForWin(C4.p1Char, C4.getLastXY() / 10, C4.getLastXY() % 10);
                        break;
                    }
                    else jout("\nSELECTED COLUMN IS FULL");
                }
            }
            if (win) {
                C4.print();
                jout("YOU WON!\n");
                break;
            }
            try {
                Thread.sleep(2000);
                while(true) {
                    input = Integer.toString(randomInt(1, 7));
                    if (C4.p2Insert(Integer.parseInt(input))) break;
                }
                System.out.println("LAST PLACED: [" +C4.getLastXY()/10 + ", " + C4.getLastXY()%10 + "]");
                win = C4.checkForWin(C4.p2Char, C4.getLastXY() / 10, C4.getLastXY() % 10);

            } catch (InterruptedException e){

            }

            if (win) {
                C4.print();
                jout("YOU LOST!\n");
                break;
            }


        }

    }

    private int randomInt(int min, int max){
        return (int)((max-min)*Math.random())+min;
    }

    private boolean viableOption(String s){
        String[] options = {"1", "2", "3", "4", "5", "6", "7"};
        for (int i = 0; i < options.length; i++){
            if (s.equals(options[i])) return true;
        }
        return false;
    }


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
        new C4Test();
    }
}

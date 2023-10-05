public class ConnectFour{

    char[][] grid;
    public char p1Char = 'O', p2Char = 'X';
    private char blank = '*';
    int[] xOff = {-1, 0, 1};
    int[] yOff = {-1, 0, 1};
    int lastInX, lastInY;

    ConsoleColours CC = new ConsoleColours();

    public ConnectFour(){
        grid = new char[6][7];
        newGame();
    }

    public void newGame(){
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[i].length; j++){
                grid[i][j] = blank;
            }
        }
    }

    public boolean p1Insert(int col){
        if (grid[0][col-1] != blank) return false;
        lastInX = col-1;

        if (grid[5][col-1] == blank){
            grid[5][col-1] = p1Char;
            lastInY = 5;
            return true;
        }
        for (int i = 0; i < grid.length; i++){
            if (grid[i][col-1] != blank){
                grid[i-1][col-1] = p1Char;
                lastInY = i-1;
                return true;
            }
        }
        return false;
    }

    public boolean p2Insert(int col){
        if (grid[0][col-1] != blank) return false;

        lastInX = col-1;
        //if (col > 0) col--;
        if (grid[5][col-1] == blank){
            grid[5][col-1] = p2Char;
            lastInY = 5;
            return true;
        }
        for (int i = 0; i < grid.length; i++){
            if (grid[i][col-1] != blank){
                grid[i-1][col-1] = p2Char;
                lastInY = i-1;
                return true;
            }
        }
        return false;
    }

    public boolean checkForWin(char playerChar, int x, int y){
        System.out.println("\nChecking For Win...");
        int count = 1;

        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){ // each surrounding space
                int current = (i*3)+j+1;
                //System.out.print("\r\r" +  current + "/9" );
                while (true) {
                    // if off-grid move onto next in previous loop
                    if (!isOnGrid(x + xOff[j], y + yOff[i]) || (x + xOff[j] == x && y + yOff[i] == y)) break;

                    //if there's another piece adjacent...
                    if (grid[y + yOff[i]][x + xOff[j]] == playerChar) {
                        count++;

                        //go to the next three spaces and count the matching chars
                        //if a char doesn't match, stop searching
                        for (int k = 2; k < 4; k++){
                            System.out.println("COUNT: " + count);
                            if (!isOnGrid(x + (xOff[j]*k), y + (yOff[i]*k))) break;
                            else if (grid[y + (yOff[i]*k)][x + (xOff[j]*k)] == playerChar) count++;
                            else break;
                        }
                        //if 4 in a row is found, return true
                        if (count >= 4) return true;
                        //otherwise move onto the next adjacent place
                        else {
                            count = 1;
                            break;
                        }
                    }
                    else break;
                }
            }
        }
        System.out.println("\nNO WIN\n");
        return false;
    }

    private boolean isOnGrid(int x, int y){
        if (x < 0 || x > 6 || y < 0 || y > 5) return false;
        else {
            //System.out.println("[" + x + ", " + y + "] is on the grid");
            return true;
        }
    }

    public int getLastXY(){
        return  (lastInX*10) + lastInY;
    }
    public String getGridString(){
        String result = "";
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[i].length; j++) {
                result += grid[i][j];
            }
        }

        return  result;
    }

    public void print(){
        //clearScreen();
        for (int y = 0; y < 6; y++){
            System.out.print("|\t");
            for(int x = 0; x < 7; x++){
                if (grid[y][x] == p1Char) System.out.print(CC.RED);
                else if (grid[y][x] == p2Char) System.out.print(CC.YELLOW);

                System.out.print(grid[y][x] + "\t" + CC.RESET);
            }
            System.out.print("|\n");
        }
        for (int i = 0; i < 34; i++)System.out.print("-");

        System.out.print("\n \t");
        for (int i = 1; i < 8; i++){
            System.out.print(i);
            if (i < 7) System.out.print("\t");
        }
        System.out.print("\n\n");
    }

    public int randomSelection(){
        return (int)((7-1)*Math.random())+1;
    }

    /*
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    */



}

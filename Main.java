// Version 3.0
// A Simple Minesweeper game implemented using Java and Java Swing for the GUI.

/******* PROJECT MEMBERS *************
* Keivan Shah    (151080029)
* Hemang Gandhi  (151080051)
* Om Modi        (151080027)
* Nisarg Mistry  (151080026)
**************************************/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;

// Cell represents each cell in the board whether its a mine, or still closed, or has flagged,
// how many mines surround it, etc. Each cell has a action listener which performs the task of flagging
// or opening the cell whenever its clicked on via the mouse.

class cell implements ActionListener {
    JPanel p;
    JFrame f;
    JButton b;
    Board board;
    boolean flagged;
    int x,y;
    int row,col;
    int value;
    cell[][] field;
    //Constructor for the cell. Sets its position, value and state to closed.
    public cell(JFrame f,JPanel p, int x, int y, int v, cell[][] field,int Max_x, int Max_y, Board board)
    {
        row=x;
        col=y;
        this.p = p;
        this.f = f;
        this.value = v;
        this.field=field;
        this.x = Max_x;
        this.y = Max_y;
        this.board = board;
        flagged = false;
        b = new JButton();
        b.addActionListener(this);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if(SwingUtilities.isRightMouseButton(mouseEvent) && b.isEnabled())
                {
                    if(!flagged){b.setIcon(UIManager.getIcon("OptionPane.warningIcon"));flagged=true;board.setflag();}
                    else {b.setIcon(null);flagged=false;board.removeflag();}
                }
            }
        });
        p.add(b);
    }
    // Function to open the tile when pressed
    void open()
    {
        b.setEnabled(false);
        b.setIcon(null);
        b.setText(Integer.toString(value));
    }
    // Function to floodfill and open all non-mine tiles around the starting tile.
    void floodfill(cell[][] c, int i, int j, int row, int col)
    {
        // Check if the value is within the bounds i.e. 0<=i<rows and 0<=j<cols
        if(i<row && i>=0 && j<col && j>=0 && c[i][j].b.isEnabled() && c[i][j].value>=0)
        {
            if(!c[i][j].flagged)c[i][j].open();
            if(c[i][j].value==0)// If the value if zero call floodfill on all the neighbouring 8 tiles.
            {
                floodfill(c, i + 1, j + 1, row, col);
                floodfill(c, i + 1, j, row, col);
                floodfill(c, i + 1, j - 1, row, col);
                floodfill(c, i, j + 1, row, col);
                floodfill(c, i, j - 1, row, col);
                floodfill(c, i - 1, j + 1, row, col);
                floodfill(c, i - 1, j, row, col);
                floodfill(c, i - 1, j - 1, row, col);
            }
        }
    }
    // Function to let the game know thats the player has lost.
    void gameover()
    {
        System.out.println("You Lost!");
        board.won=false;
        board.g.GameOver=true;
    }
    //ActionListener that calls gameover if player opens a mine else calls the floodfill to open all
    //the neighbouring tiles.
    public void actionPerformed(ActionEvent e)
    {
        //System.out.println(Integer.toString(row)+"\t"+Integer.toString(col));
        if(flagged){flagged = false;board.removeflag();}
        if(value==-1)gameover();
        else floodfill(field,row,col,x,y);
    }
}
// The Class that represents the complete board. It consists of all the tiles and has functions
// to randomly allot mines, draw the board to the Frame, check if player has won, set and remove flags
// and to show the complete board once the player loses.
class Board
{
    game g;
    JFrame f;
    int rows,cols,mines,flags;
    volatile int[][] field;
    cell[][] c;
    boolean won;

    public Board(JFrame f,int x, int y, int m, game g)
    {
        this.f = f;
        this.g = g;
        rows=x;
        cols=y;
        mines=m;
        flags=0;
        won=true;
        c = new cell[rows][];
        field = new int[rows][cols];
    }
    //The initialization function. It allocates m mines randomly to m tiles and sets their value to -1
    // and initializes value of all other tiles to number of mines around them. Also it sets all tiles as unflagged.
    void init()
    {
        for(int i=0; i<rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                field[i][j] = 0;
            }
        }
        if(mines<rows*cols-1)
        {
            for (int i = 0; i < mines; i++) {
                int x = (int) (Math.random() * rows);
                int y = (int) (Math.random() * cols);
                if (field[x][y] != -1)
                    field[x][y] = -1;
                else i--;
            }
        }
        else // Quit if mines are more that the grid size
        {
            System.out.println("Number of Mines exceed the GridSize");
            System.exit(1);
        }

        for(int i=0; i<rows; i++)
        {
            for (int j = 0; j <cols; j++)
            {
                if(field[i][j]!=-1)
                {
                    int k=0;
                    if(i+1<rows && j+1<cols)if(field[i+1][j+1]==-1)k++;
                    if(i+1<rows && j<cols)if(field[i+1][j]==-1)k++;
                    if(i+1<rows && j-1>=0)if(field[i+1][j-1]==-1)k++;
                    if(i<rows && j+1<cols)if(field[i][j+1]==-1)k++;
                    if(i<rows && j-1>=0)if(field[i][j-1]==-1)k++;
                    if(i-1>=0 && j+1<cols)if(field[i-1][j+1]==-1)k++;
                    if(i-1>=0 && j<cols)if(field[i-1][j]==-1)k++;
                    if(i-1>=0 && j-1>=0)if(field[i-1][j-1]==-1)k++;
                    field[i][j]=k;
                }
            }
        }
    }
    // This finction draws the board to the JFrame. The tiles are made using an Grid of JButtons.
    public void draw()
    {
        JPanel panel = (JPanel) f.getContentPane();
        panel.removeAll();
        GridLayout g = new GridLayout(rows,cols,200,200);
        g.setVgap(0);
        g.setHgap(0);
        JPanel field = new JPanel(g);
        f.getContentPane().add(BorderLayout.CENTER,field);
        f.setSize(1000,1000);
        for(int i=0; i<rows; i++) {
            c[i] = new cell[cols];
            for (int j = 0; j < cols; j++)
                c[i][j] = new cell(f, field, i, j, this.field[i][j], c, rows, cols, this);
        }
    }
    //This sets the flag and checks if the player has set flag==number of mines. If yes then it checks
    // if the player has won or if he has lost.
    void setflag()
    {
        flags++;
        g.score.setflag();
        if(flags==mines)
        {
            if(checkifwon()==true) game_won();
            else gameover();
        }
    }
    // This removes the flag from the tile.
    void removeflag()
    {
        flags--;
        g.score.removeflag();
    }
    //Function to check if the player has won. It iterates through all the tiles and checks if all mines have been
    //flagged. If a non mine tile has been flagged it returns false else return true.
    boolean checkifwon()
    {
        won =true;
        for(int i=0; i<rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (c[i][j].flagged == true && (c[i][j].value==-1)) {
                    continue;
                }
                else if(c[i][j].flagged == true)
                {
                    won = false;
                    break;
                }
            }
        }
        return won;
    }
    //The function shows the whole board to the player. All unflagged mines are shown as red.
    // All flagged mines are blue and all other open tiles show the number of mines around them.
    void showboard()
    {
        for(int i=0; i<rows; i++)
        {
            for(int j=0; j<cols; j++)
            {
                if(c[i][j].value==-1 && !c[i][j].flagged)
                {
                    c[i][j].b.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
                    c[i][j].b.setText(null);
                    c[i][j].b.setEnabled(true);
                }
                else if(c[i][j].value==-1)
                {
                    c[i][j].b.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
                    c[i][j].b.setText(null);
                    c[i][j].b.setEnabled(true);
                }
                else if(!c[i][j].b.isEnabled())
                {
                    c[i][j].b.setIcon(null);
                    c[i][j].b.setText(Integer.toString(c[i][j].value));
                }
            }
        }
        try{
            Thread.sleep(3500);
        }
        catch (Exception e){}
    }
    //Prints you won and sets the gameover flag to true.
    void game_won()
    {
        System.out.println("YOU WON!");
        g.GameOver=true;
    }
    //Prints you lost and sets the gameover flag to false.
    void gameover()
    {
        System.out.println("YOU LOST!");
        g.GameOver=true;
    }
}

// The Actual game class. It consist of the board and scoreboard. It has functions to start the game,
// restart the game and exit the game. It also shows the end screen once the game is over.
class game
{
    int mines,rows,cols,flags;
    Board board; // each game has one board which inturn contains rows*cols number of buttons
    Score score; //  and a score board which shows the time and number of tiles flagged.
    JFrame f;
    volatile boolean GameOver,Exit,atEndScreen,atStartScreen;
    public game()
    {
        flags=0;
        f = new JFrame("Minesweeper");
        GameOver = false;
        atEndScreen = false;
        atStartScreen = true;
        Exit = false;
    }
    public game(int r, int c, int m)
    {
        mines=m;
        rows=r;
        cols=c;
        flags=0;
        f = new JFrame("Minesweeper");
        board = new Board(f,r,c,m,this);
        score = new Score(f,mines,flags);
        GameOver = false;
        atEndScreen = false;
        atStartScreen = false;
        Exit = false;
    }
    // The function to start the game. It initializes the board and draws the board and score.
    void start()
    {
        flags=0;
        GameOver=false;
        board.init();
        board.draw();
        score.starttime();
        score.draw();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    //Shows the endscreen which shows if you have won or lost and time you took. It also has buttons to
    //replay the game or start a new game or exit. Each button has a ActionListener associated with it
    //calls the necessary function when the button is pressed.
    void endscreen()
    {
        atEndScreen = true;
        f.getContentPane().removeAll();

        JPanel pane = new JPanel();
        pane.setLayout(new FlowLayout(FlowLayout.CENTER,30,30));

        JLabel m = new JLabel("Game Over!");
        JLabel s = new JLabel();
        JLabel t = new JLabel();
        m.setFont(new Font("Serif", Font.BOLD, 70));
        s.setFont(new Font("Serif",Font.BOLD,70));
        if(board.won==true){s.setText("You Won!");t.setText("You took "+ Double.toString(score.totaltime()/1000)+" seconds");}
        else s.setText("You Lost!");

        pane.add(m,BorderLayout.NORTH);
        pane.add(s,BorderLayout.NORTH);
        pane.add(t,BorderLayout.CENTER);
        JButton b = new JButton("Replay");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                replay();
            }
        });
        JButton b1 = new JButton("New Game");
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                newgame();
            }
        });
        JButton b2 = new JButton("Exit");
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Exit = true;
            }
        });

        pane.add(b,BorderLayout.CENTER);
        pane.add(b1,BorderLayout.CENTER);
        pane.add(b2,BorderLayout.CENTER);

        f.getContentPane().add(pane);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    //A function to replay the last game. It sets all the tiles to closed and also sets flags and time to zero.
    void replay()
    {
        System.out.println("GAME RESTARTED!");
        GameOver = false;
        flags=0;score.flags=0;board.flags=0;
        atEndScreen = false;
        for(int i=0; i<rows; i++)
        {
            for(int j=0; j<cols; j++)
            {
                board.c[i][j].b.setEnabled(true);
                board.c[i][j].flagged=false;
            }
        }
        board.draw();
        score.starttime();
        score.draw();
    }
    //A function to start a new game. It reinitialize the board and draws it to the Screen.
    void newgame()
    {
        System.out.println("NEW GAME STARTED!");
        GameOver = false;score.flags=0;board.flags=0;
        flags=0;
        atEndScreen = false;
        board.init();
        board.draw();
        score.starttime();
        score.draw();
    }
    //Check if the player has decided to exit the game or not.
    boolean isExitted()
    {
        return Exit;
    }
    void setvalues(int r, int c, int m)
    {
        this.rows=r;
        this.cols=c;
        this.mines=m;
    }
    void startscreen()
    {
        f.getContentPane().removeAll();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JRadioButton easy = new JRadioButton("Easy");
        easy.setMnemonic(KeyEvent.VK_E);
        easy.setActionCommand("Easy");
        easy.setFont(new Font("Serif",Font.PLAIN,30));
        easy.setSelected(true);
        setvalues(10,10,10);
        easy.setToolTipText("10x10 Grid,10 Mines");

        JRadioButton medium = new JRadioButton("Medium");
        medium.setMnemonic(KeyEvent.VK_M);
        medium.setFont(new Font("Serif",Font.PLAIN,30));
        medium.setActionCommand("Medium");
        medium.setToolTipText("15x15 Grid,25 Mines");

        JRadioButton hard = new JRadioButton("Hard");
        hard.setMnemonic(KeyEvent.VK_H);
        hard.setFont(new Font("Serif",Font.PLAIN,30));
        hard.setActionCommand("Hard");
        hard.setToolTipText("20x20 Grid,50 Mines");

        ButtonGroup group = new ButtonGroup();
        group.add(easy);
        group.add(medium);
        group.add(hard);

        easy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setvalues(10,10,10);
            }
        });
        medium.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setvalues(15,15,25);
            }
        });
        hard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setvalues(20,20,50);
            }
        });
        JPanel pane = new JPanel();
        GridLayout g = new GridLayout(1, 1);
        g.setHgap(70);
        g.setVgap(30);
        JPanel radioPanel = new JPanel();
        radioPanel.add(easy);
        radioPanel.add(medium);
        radioPanel.add(hard);
        pane.add(radioPanel, BorderLayout.LINE_START);
        pane.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        JButton s = new JButton("Start");
        s.setMnemonic(KeyEvent.VK_S);
        s.setFont(new Font("Serif",Font.PLAIN,30));
        s.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                board = new Board(f,rows,cols,mines,game.this);
                score = new Score(f,mines,flags);
                start();
                s.removeActionListener(this);
                atStartScreen = false;
            }
        });
        pane.add(s);
        f.getContentPane().add(pane);
        f.setSize(500,200);
    }
}
//A class the measure the score and time of he game and display it on screen.
class Score
{
    JFrame f;
    JLabel m,fl,time;
    int mines,flags;
    volatile double elapsedtime=0;
    long t;
    //constructor
    public Score(JFrame f, int mines, int flags)
    {
        this.mines=mines;
        this.f=f;
        this.flags=flags;
    }
    //start the clock of the scoreboard.
    void starttime()
    {
        t = System.nanoTime();
    }
    //show the total time elapsed from the clock start.
    double totaltime()
    {
        return elapsedtime;
    }
    //set a flag.
    void setflag()
    {
        flags++;
        fl.setText("Flags Set: "+Integer.toString(flags));
    }
    //remove a flag.
    void removeflag()
    {
        flags--;
        fl.setText("Flags Set: "+Integer.toString(flags));
    }
    //Update the time. It subtracts the current time from the start time.
    void updatetime()
    {
        elapsedtime = (System.nanoTime()-t)/1000000;
        time.setText("Time: "+ Double.toString((int)elapsedtime/1000));
    }
    //Draw the score board to the frame.
    void draw()
    {
        JPanel p= new JPanel(new GridLayout());
        starttime();
        m = new JLabel(" Total Mines: "+Integer.toString(mines));
        fl =new JLabel(" Flags Set: "+Integer.toString(flags));
        time = new JLabel(" Time: "+ Double.toString((int)totaltime()));
        p.add(m);
        p.add(fl);
        p.add(time);
        f.getContentPane().add(BorderLayout.NORTH,p);
    }
}

public class Main {
    public static void main(String[] args)
    {
	    game g = new game(); //Initialize a empty game
        g.startscreen();     // Show the startscreen and launch the game as per the entered choice.

        //game g = new game(r,c,m)  // A custom game can also be started with this constructor.
        //g.start();                // Here a game of 'r' rows, 'c' columns and 'm' mines is started.
        while(g.atStartScreen){try{Thread.sleep(100);}catch(Exception e){}}
        do
        {
            if(!g.atEndScreen)     // If the user is not on the endscreen
            {
                while(!g.GameOver) // till the game is not over
                {
                    g.score.updatetime(); // keep updating the time on the scoreboard.
                    try{Thread.sleep(500);}catch(Exception e){}
                }
                // Now that the game is over show the whole board and then show the endscreen
                g.board.showboard();
                g.endscreen();
                // if the player has not exitted again keep updating the time.
            }
        }while(!g.isExitted());
        System.out.println("CLOSING THE GAME!");    //Exit the Game.
        System.exit(0);
    }
}

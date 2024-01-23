import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Random;

public class tictactoe1 extends JFrame
      implements ActionListener {
   JButton b11, b21, b31,
         b12, b22, b32,
         b13, b23, b33;
   static char[][] board = new char[3][3];
   boolean myturn;
   boolean running = true;
   boolean gameOver = false;
   int moveCount = 0;
   int times = 1;
   int runtimes = 1;
   int xwin = 0;
   int owin = 0;
   boolean isHuman = true;
   Timer computerMoveTimer;
   BufferedReader br;
   BufferedWriter bw;
   Thread connection;
   Process prologProcess;
   String prolog;
   String ttt;

   /**
    * Create a tic tac toe game,
    * prolog is the prolog command (e.g. "/opt/homebrew/bin/swipl").
    * ttt is the locator for ttt.pl (e.g.
    * "/Users/xxittysnxx/Desktop/week8lab2/ttt.pl").
    */
   public tictactoe1(String prolog, String ttt) {
      this.prolog = prolog;
      this.ttt = ttt;
      b11 = new JButton("");
      b21 = new JButton("");
      b31 = new JButton("");
      b12 = new JButton("");
      b22 = new JButton("");
      b32 = new JButton("");
      b13 = new JButton("");
      b23 = new JButton("");
      b33 = new JButton("");
      b11.setActionCommand("(1,1)."); // prolog reads pair term
      b21.setActionCommand("(2,1).");
      b31.setActionCommand("(3,1).");
      b12.setActionCommand("(1,2).");
      b22.setActionCommand("(2,2).");
      b32.setActionCommand("(3,2).");
      b13.setActionCommand("(1,3).");
      b23.setActionCommand("(2,3).");
      b33.setActionCommand("(3,3).");
      Font f = new Font("monospaced", Font.PLAIN, 64);
      b11.setFont(f);
      b21.setFont(f);
      b31.setFont(f);
      b12.setFont(f);
      b22.setFont(f);
      b32.setFont(f);
      b13.setFont(f);
      b23.setFont(f);
      b33.setFont(f);
      b11.addActionListener(this);
      b21.addActionListener(this);
      b31.addActionListener(this);
      b12.addActionListener(this);
      b22.addActionListener(this);
      b32.addActionListener(this);
      b13.addActionListener(this);
      b23.addActionListener(this);
      b33.addActionListener(this);
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(3, 3));
      panel.add(b11);
      panel.add(b21);
      panel.add(b31);
      panel.add(b12);
      panel.add(b22);
      panel.add(b32);
      panel.add(b13);
      panel.add(b23);
      panel.add(b33);
      // this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE) ;
      this.setTitle("Tic Tac Toe - kxt230002");
      Border panelborder = BorderFactory.createLoweredBevelBorder();
      panel.setBorder(panelborder);
      this.getContentPane().add(panel);

      // Add button
      JButton switchButton = new JButton("switch player");
      switchButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            isHuman = !isHuman;
         }
      });
      JButton startButton = new JButton("start a game");
      startButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            runtimes = 1;
            times = 1;
            xwin = 0;
            owin = 0;
            startGame();
         }
      });
      JButton startNButton = new JButton("start n games");
      startNButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            runtimes = 1;
            xwin = 0;
            owin = 0;
            startNGames();
         }
      });
      JButton stopButton = new JButton("stop/reset");
      stopButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            stopGame();
         }
      });

      JPanel buttonPanel = new JPanel();
      buttonPanel.add(switchButton);
      buttonPanel.add(startButton);
      buttonPanel.add(startNButton);
      buttonPanel.add(stopButton);
      this.getContentPane().add(buttonPanel, BorderLayout.NORTH);

      this.setSize(300, 300);
      this.setLocation(900, 300);
      this.myturn = true;

      Connect1 connector = new Connect1(54321);
      connector.start();
      Thread shows = new Thread() {
         public void run() {
            setVisible(true);
         }
      };
      EventQueue.invokeLater(shows);
      computerMoveTimer = new Timer(1000, e -> {
         if (myturn && !gameOver && !isHuman) {
            makeComputerMove();
         }
      });

      // Start the timer
      computerMoveTimer.start();
      startGame();
   }

   // /opt/homebrew/bin/swipl /Users/xxittysnxx/Desktop/ttt-kxt230002/ttt.pl
   public static void main(String[] args) {
      String prolog = "/opt/homebrew/bin/swipl";
      String ttt = "/Users/xxittysnxx/Desktop/ttt-kxt230002/ttt.pl";
      boolean noargs = true;
      try {
         prolog = args[0];
         ttt = args[1];
         noargs = false;
      } catch (Exception xx) {
         System.out.println("usage: java tictactoe1  <where prolog>  <where ttt>");
      }
      if (noargs) {
         Object[] message = new Object[4];
         message[0] = new Label("  prolog command");
         message[1] = new JTextField(prolog);
         message[2] = new Label("  where ttt.pl ");
         message[3] = new JTextField(ttt);
         try {
            int I = JOptionPane.showConfirmDialog(null, message, "Where are Prolog and ttt.pl? ",
                  JOptionPane.OK_CANCEL_OPTION);
            if (I == 2 | I == 1)
               System.exit(0);
            System.out.println(I);
            new tictactoe1(((JTextField) message[1]).getText().trim(), ((JTextField) message[3]).getText().trim());
         } catch (Exception yy) {
         }
      } else
         new tictactoe1(prolog, ttt);
   }

   void computer_move(String s, String side) { // " x ## y '
      String[] c = s.split(",");
      int x = Integer.parseInt(c[0].trim()),
            y = Integer.parseInt(c[1].trim());
      System.out.println("Player 2 (O) takes the cell(" + x + "," + y + ").");
      board[x - 1][y - 1] = 'O';
      if (x == 1) {
         if (y == 1)
            b11.setText(side);
         else if (y == 2)
            b12.setText(side);
         else if (y == 3)
            b13.setText(side);
      } else if (x == 2) {
         if (y == 1)
            b21.setText(side);
         else if (y == 2)
            b22.setText(side);
         else if (y == 3)
            b23.setText(side);
      } else if (x == 3) {
         if (y == 1)
            b31.setText(side);
         else if (y == 2)
            b32.setText(side);
         else if (y == 3)
            b33.setText(side);
      }
      if (winner('O')) {
         if (times > runtimes) {
            runtimes++;
            startGame();
         }
      } else
         myturn = true;
   }

   /**
    * Java player
    */
   public void actionPerformed(ActionEvent act) {
      if (!myturn || !isHuman)
         return; // otherwise
      String s = ((JButton) act.getSource()).getText();
      if (!s.equals(""))
         return;
      ((JButton) (act.getSource())).setText("X");
      int x = 0, y = 0;
      if (act.getActionCommand() == "(1,1).") {
         x = 1;
         y = 1;
      } else if (act.getActionCommand() == "(1,2).") {
         x = 1;
         y = 2;
      } else if (act.getActionCommand() == "(1,3).") {
         x = 1;
         y = 3;
      } else if (act.getActionCommand() == "(2,1).") {
         x = 2;
         y = 1;
      } else if (act.getActionCommand() == "(2,2).") {
         x = 2;
         y = 2;
      } else if (act.getActionCommand() == "(2,3).") {
         x = 2;
         y = 3;
      } else if (act.getActionCommand() == "(3,1).") {
         x = 3;
         y = 1;
      } else if (act.getActionCommand() == "(3,2).") {
         x = 3;
         y = 2;
      } else if (act.getActionCommand() == "(3,3).") {
         x = 3;
         y = 3;
      }
      board[x - 1][y - 1] = 'X';
      System.out.println("Player 1 (X) takes the cell" + act.getActionCommand());
      moveCount++;
      try {
         bw.write(act.getActionCommand() + "\n");
         bw.flush();
      } catch (Exception xx) {
         System.out.println(xx);
      }
      myturn = false;
      if (winner('X')) {
         if (times > runtimes) {
            runtimes++;
            startGame();
         }
      } else if (moveCount == 5) { // Check for a full board
         // Handle the case where the board is full (a draw)
         System.out.println("It's a draw");
         stopGame();
         if (times > runtimes) {
            runtimes++;
            startGame();
         }
      }
   }

   /**
    * Do we have a winner?
    */
   boolean winner(char winningPlayer) {
      if (line(b11, b21, b31) ||
            line(b12, b22, b32) ||
            line(b13, b23, b33) ||
            line(b11, b12, b13) ||
            line(b21, b22, b23) ||
            line(b31, b32, b33) ||
            line(b11, b22, b33) ||
            line(b13, b22, b31)) {
         String message;
         if (winningPlayer == 'X') {
            message = "Player 1 (" + winningPlayer + ") wins";
            xwin++;
         } else {
            message = "Player 2 (" + winningPlayer + ") wins";
            owin++;
         }
         System.out.println(message);
         System.out.println("Game ends, for game " + runtimes + " of total N = " + times + " games");
         System.out.println("Player 1 (X) wins " + xwin + " times");
         System.out.println("Player 2 (O) wins " + owin + " times");
         stopGame();
         /*
          * EventQueue.invokeLater(new Runnable() {
          * public void run() {
          * JOptionPane.showMessageDialog(tictactoe1.this, message, "Game Over",
          * JOptionPane.INFORMATION_MESSAGE);
          * }
          * });
          */
         return true;
      }
      return false;
   }

   /**
    * Are three buttons marked with same player?
    * If, so color the line and return true.
    */
   boolean line(JButton b, JButton c, JButton d) {
      if (!b.getText().equals("") && b.getText().equals(c.getText()) &&
            c.getText().equals(d.getText())) {
         if (b.getText().equals("O")) {
            b.setBackground(Color.red);
            c.setBackground(Color.red);
            d.setBackground(Color.red);
         } else {
            b.setBackground(Color.green);
            c.setBackground(Color.green);
            d.setBackground(Color.green);
         }
         return true;
      } else
         return false;
   }

   private void startGame() {
      // Clear the board buttons
      b11.setText("");
      b21.setText("");
      b31.setText("");
      b12.setText("");
      b22.setText("");
      b32.setText("");
      b13.setText("");
      b23.setText("");
      b33.setText("");
      // Reset any other game-related data
      myturn = true;
      System.out.println();
      System.out.print("Check board clear or not: ");
      System.out.print(b11);
      System.out.print(b12);
      System.out.print(b13);
      System.out.print(b21);
      System.out.print(b22);
      System.out.print(b23);
      System.out.print(b31);
      System.out.print(b32);
      System.out.print(b33);
      System.out.println();
      // Reset background color on the buttons if needed.
      b11.setBackground(null);
      b21.setBackground(null);
      b31.setBackground(null);
      b12.setBackground(null);
      b22.setBackground(null);
      b32.setBackground(null);
      b13.setBackground(null);
      b23.setBackground(null);
      b33.setBackground(null);

      // Any other game-specific logic can go here.
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++)
            board[i][j] = ' ';
      }
      if (br == null) {
         Socket sock;
         try {
            sock = new Socket("127.0.0.1", 54321);
            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
         } catch (Exception x) {
            System.out.println(x);
         }
      }
      System.out.println("Game starts, for game " + runtimes + " of total N = " + times + " games");
      gameOver = false; // Reset the game over flag
      running = true;
      if (connection == null || !connection.isAlive()) {
         connection = new Thread() {
            public void run() {
               while (running) {
                  try {
                     String s = br.readLine();
                     if (!gameOver) { // Check if the game is over before processing moves
                        computer_move(s, "O");
                     }
                  } catch (Exception xx) {
                     System.out.println(xx);
                  }
               }
            }
         };
         connection.start();
      }

      try {
         prologProcess = Runtime.getRuntime().exec(prolog + " -f " + ttt);
      } catch (Exception xx) {
         System.out.println(xx);
      }

      // On closing, kill the prolog process first and then exit
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent w) {
            if (prologProcess != null)
               prologProcess.destroy();
            System.exit(0);
         }
      });
   }

   private void startNGames() {
      String input = JOptionPane.showInputDialog("Input N times:");
      times = Integer.parseInt(input);
      startGame();
   }

   private void stopGame() {
      running = false;
      gameOver = true;
      moveCount = 0;
      if (connection != null) {
         connection.interrupt();
      }
   }

   private void makeComputerMove() {
      int[] computerMove = generateComputerMove();
      if (computerMove[0] == 1 && computerMove[1] == 1)
         ((JButton) b11).setText("X");
      else if (computerMove[0] == 1 && computerMove[1] == 2)
         ((JButton) b12).setText("X");
      else if (computerMove[0] == 1 && computerMove[1] == 3)
         ((JButton) b13).setText("X");
      else if (computerMove[0] == 2 && computerMove[1] == 1)
         ((JButton) b21).setText("X");
      else if (computerMove[0] == 2 && computerMove[1] == 2)
         ((JButton) b22).setText("X");
      else if (computerMove[0] == 2 && computerMove[1] == 3)
         ((JButton) b23).setText("X");
      else if (computerMove[0] == 3 && computerMove[1] == 1)
         ((JButton) b31).setText("X");
      else if (computerMove[0] == 3 && computerMove[1] == 2)
         ((JButton) b32).setText("X");
      else if (computerMove[0] == 3 && computerMove[1] == 3)
         ((JButton) b33).setText("X");
      board[computerMove[0] - 1][computerMove[1] - 1] = 'X';
      System.out.println("Player 1 (X) takes the cell(" + computerMove[0] + "," + computerMove[1] + ").");
      moveCount++;
      try {
         bw.write("(" + computerMove[0] + "," + computerMove[1] + ").\n");
         bw.flush();
      } catch (Exception xx) {
         System.out.println(xx);
      }
      myturn = true;
      if (winner('X')) {
         if (times > runtimes) {
            runtimes++;
            startGame();
         }
      } else if (moveCount == 5) {
         System.out.println("It's a draw");
         stopGame();
         if (times > runtimes) {
            runtimes++;
            startGame();
         }
      }
   }

   private int[] generateComputerMove() {
      /*
       * int[] bestMove = { -1, -1 }; // Initialize to an invalid move
       * int bestScore = Integer.MIN_VALUE;
       * System.out.println(bestScore);
       * for (int i = 0; i < 3; i++) {
       * for (int j = 0; j < 3; j++) {
       * if (board[i][j] == ' ') {
       * board[i][j] = 'O'; // Simulate 'O' move
       * int score = minimax(0, false);
       * board[i][j] = ' '; // Undo the move
       * 
       * if (score > bestScore) {
       * bestScore = score;
       * bestMove[0] = i;
       * bestMove[1] = j;
       * }
       * }
       * }
       * }
       */
      int[][] pairs = {
            { 1, 1 },
            { 1, 2 },
            { 1, 3 },
            { 2, 1 },
            { 2, 2 },
            { 2, 3 },
            { 3, 1 },
            { 3, 2 },
            { 3, 3 }
      };

      // Shuffle the array using Fisher-Yates shuffle
      Random random = new Random();
      for (int i = pairs.length - 1; i > 0; i--) {
         int index = random.nextInt(i + 1);
         // Swap array[i] with array[index]
         int[] temp = pairs[i];
         pairs[i] = pairs[index];
         pairs[index] = temp;
      }

      int[] bestMove = { 2, 2 };
      for (int[] pair : pairs) {
         if (board[pair[0] - 1][pair[1] - 1] == ' ') {
            bestMove = pair;
            break;
         }
      }
      return bestMove;
   }

   /*
    * public static int minimax(int depth, boolean isMaximizing) {
    * int result = evaluate();
    * 
    * if (result == 10) {
    * return result; // AI wins
    * }
    * if (result == -10) {
    * return result; // Player wins
    * }
    * if (isBoardFull()) {
    * return 0; // It's a draw
    * }
    * 
    * if (isMaximizing) {
    * int bestScore = Integer.MIN_VALUE;
    * for (int i = 0; i < 3; i++) {
    * for (int j = 0; j < 3; j++) {
    * if (board[i][j] == ' ') {
    * board[i][j] = 'O';
    * int score = minimax(depth + 1, false);
    * board[i][j] = ' '; // Undo the move
    * bestScore = Math.max(bestScore, score);
    * }
    * }
    * }
    * return bestScore;
    * } else {
    * int bestScore = Integer.MAX_VALUE;
    * for (int i = 0; i < 3; i++) {
    * for (int j = 0; j < 3; j++) {
    * if (board[i][j] == ' ') {
    * board[i][j] = 'X';
    * int score = minimax(depth + 1, true);
    * board[i][j] = ' '; // Undo the move
    * bestScore = Math.min(bestScore, score);
    * }
    * }
    * }
    * return bestScore;
    * }
    * }
    * 
    * public static int evaluate() {
    * // Check rows, columns, and diagonals for a win
    * for (int i = 0; i < 3; i++) {
    * if (board[i][0] == 'O' && board[i][1] == 'O' && board[i][2] == 'O') {
    * return 10; // AI wins
    * }
    * if (board[i][0] == 'X' && board[i][1] == 'X' && board[i][2] == 'X') {
    * return -10; // Player wins
    * }
    * if (board[0][i] == 'O' && board[1][i] == 'O' && board[2][i] == 'O') {
    * return 10; // AI wins
    * }
    * if (board[0][i] == 'X' && board[1][i] == 'X' && board[2][i] == 'X') {
    * return -10; // Player wins
    * }
    * }
    * 
    * if (board[0][0] == 'O' && board[1][1] == 'O' && board[2][2] == 'O') {
    * return 10; // AI wins
    * }
    * if (board[0][0] == 'X' && board[1][1] == 'X' && board[2][2] == 'X') {
    * return -10; // Player wins
    * }
    * 
    * if (board[0][2] == 'O' && board[1][1] == 'O' && board[2][0] == 'O') {
    * return 10; // AI wins
    * }
    * if (board[0][2] == 'X' && board[1][1] == 'X' && board[2][0] == 'X') {
    * return -10; // Player wins
    * }
    * 
    * return 0; // No winner
    * }
    * 
    * public static boolean isBoardFull() {
    * for (int i = 0; i < 3; i++) {
    * for (int j = 0; j < 3; j++) {
    * if (board[i][j] == ' ') {
    * return false;
    * }
    * }
    * }
    * return true; // Board is full
    * }
    */
}

/*
 * If Java player closes GUI, then Prolog process is terminated.
 * Java process monitors "win" status of both players, signals a win,
 * and closes the connector and prolog player.
 * Prolog justs plays given position.
 * Write all of this up; it is interesting.
 */

package zemiA;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow {
  public MainWindow(int columns,int rows) {
    JFrame frame=new JFrame();
    frame.setTitle("Lifegame");
    frame.setLocationRelativeTo(null);
    frame.setSize(800,600);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    /*
    JMenuBar menubar =new JMenuBar();
    JMenu menu1=new JMenu("File");
    JMenu menu2=new JMenu("Edit");
    menubar.add(menu1);
    menubar.add(menu2);
    JMenuItem newGame = new JMenuItem("new game");
    JMenuItem boardSize = new JMenuItem("board size");
    menu1.add(newGame);
    menu2.add(boardSize);
    frame.setJMenuBar(menubar);
*/

    JPanel base=new JPanel();
    frame.setContentPane(base);
    base.setLayout(new BorderLayout());

    BoardModel BM =new BoardModel(columns,rows);
    BoardView view =new BoardView(BM);
    base.add(view,BorderLayout.CENTER);
    /*
    BM.addListener(view);
    boardSize.addActionListener(new ChangeBoardSizeListener(BM));
    newGame.addActionListener(new NewGameListener());
    */


    /*
    JPanel buttonPanel=new JPanel();
    JButton btn1=new JButton("Next");
    btn1.addActionListener(new NextListener(BM));
    JButton btn2=new JButton("Undo");
    btn2.setEnabled(false);
    btn2.addActionListener(new UndoListener(BM));
    JButton btn3=new JButton("NewGame");
    btn3.addActionListener(new NewGameListener());
    buttonPanel.add(btn1);
    buttonPanel.add(btn2);
    buttonPanel.add(btn3);
    base.add(buttonPanel,BorderLayout.SOUTH);

    BM.addListener(new ButtonView(btn2));
*/
    frame.setVisible(true);
    }
}

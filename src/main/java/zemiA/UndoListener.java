package zemiA;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UndoListener implements ActionListener{
  BoardModel BM;

  public UndoListener(BoardModel BM) {
    this.BM=BM;
  }

  public void actionPerformed(ActionEvent e) {
    BM.undo();
  }
}
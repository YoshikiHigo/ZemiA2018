package zemiA;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NextListener implements ActionListener{
  BoardModel BM;

  public NextListener(BoardModel BM){
    this.BM=BM;
  }

  public void actionPerformed(ActionEvent e) {
    BM.next();
    }
}
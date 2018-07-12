package zemiA;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangeBoardSizeListener  implements ActionListener{
  BoardModel BM;
  public ChangeBoardSizeListener(BoardModel BM) {
    this.BM=BM;
    }

  @Override
  public void actionPerformed(ActionEvent e) {
    new DialogBoardSize(BM);
  }
}
package zemiA;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGameListener implements ActionListener{
  public NewGameListener() {
  }

  public void actionPerformed(ActionEvent e) {
    new DialogBoardSize();
  }
}

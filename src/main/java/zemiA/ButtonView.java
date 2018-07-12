package zemiA;

import javax.swing.JButton;

public class ButtonView implements BoardListener{
  JButton btn;

  public ButtonView(JButton btn) {
    this.btn=btn;
  }

  @Override
  public void updated(BoardModel m) {
    enableUndo(m);
  }

  public void enableUndo(BoardModel m) {
    if(m.isUndoable())btn.setEnabled(true);
    else   btn.setEnabled(false);
  }
}
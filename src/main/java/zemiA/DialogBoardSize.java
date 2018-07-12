package zemiA;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

class DialogBoardSize extends JDialog implements ActionListener{
  BoardModel BM;
  boolean willChange=false;
  JTextField text1;
  JTextField text2;
  JButton btn;

  public DialogBoardSize(BoardModel BM) {
    this.BM=BM;
    willChange=true;

    ShowDialog();
  }
  public DialogBoardSize() {
    ShowDialog();
  }

  public void ShowDialog() {
    getContentPane().setLayout(new FlowLayout());
    setLocationRelativeTo(null);

     text1=new JTextField("columns",10);
     text2=new JTextField("rows",10);

    btn=new JButton("����");
    btn.addActionListener(this);

    add(text2);
    add(text1);
    add(btn,BorderLayout.SOUTH);

    setTitle("BoardSize");
    setSize(400,300);
    setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if(isProperNumber(text1.getText())&isProperNumber(text2.getText())) {
      if(willChange)BM.changeBoardSize(Integer.parseInt(text1.getText()),Integer.parseInt(text2.getText()));
      else new MainWindow(Integer.parseInt(text1.getText()),Integer.parseInt(text2.getText()));
      dispose();
    }else {
        JOptionPane.showMessageDialog(this, "Input proper number (1<number<1000) in the textboxes.", "error",JOptionPane.INFORMATION_MESSAGE);
    }
  }

  public boolean isProperNumber(String num) {
      try {
          if(0<Integer.parseInt(num)&Integer.parseInt(num)<1000)return true;
          return false;
      } catch (NumberFormatException e) {
          return false;
      }
  }
}
package zemiA;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

public class BoardView  extends JPanel {

  List<ClassInformation> CIS;
  private String nameEvent;
  private int pointX=0;
  private int pointY=0;

  public BoardView(List<ClassInformation> CIS){
	  this.CIS=CIS;
    //this.addMouseListener(this);
    //this.addMouseMotionListener(this);
  }

  /*
  public void updated(BoardModel m) {
    repaint();
  }
  */
/*
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {
    if(point2cell(e.getX(),e.getY())[0]!=-1)BM.changeCellState(point2cell(e.getX(),e.getY())[0],point2cell(e.getX(),e.getY())[1]);

    nameEvent="mousePressed";
    pointX=e.getX();
    pointY=e.getY();
  }
*/

  /*
  public void mouseReleased(MouseEvent e) {}
  public void mouseDragged(MouseEvent e) {
    if(!(nameEvent.equals("mousePressed")&Arrays.equals(point2cell(pointX,pointY),point2cell(e.getX(),e.getY()))))
      if(!(nameEvent.equals("mouseDragged")&Arrays.equals(point2cell(pointX,pointY),point2cell(e.getX(),e.getY())))) {
        if(point2cell(e.getX(),e.getY())[0]!=-1)BM.changeCellState(point2cell(e.getX(), e.getY())[0],point2cell(e.getX(),e.getY())[1]);
      }

    nameEvent="mouseDragged";
    pointX=e.getX();
    pointY=e.getY();
  }
  public void mouseMoved(MouseEvent e) {}
*/

/*
  public int[] point2cell(int x,int y) {
    int xMargin=calcPrefferedPlace()[0];
    int yMargin=calcPrefferedPlace()[1];
    int lengthCell=calcPrefferedPlace()[2];

    for(int c=1;c<=BM.getCols();c++)
      for(int r=1;r<=BM.getRows();r++)
        if(xMargin+(c-1)*lengthCell<x&x<xMargin+(c)*lengthCell&yMargin+(r-1)*lengthCell<y&y<yMargin+(r)*lengthCell)return new int[] {c,r};
    return new int[] {-1,-1};
  }
*/

    @Override
    public void paint(Graphics g) {


    super.paint(g);
    int xMargin=calcPrefferedPlace()[0];
    int yMargin=calcPrefferedPlace()[1];
    int lengthCell=calcPrefferedPlace()[2];

    Font font1=new Font("ＭＳ　Ｐゴシック",Font.PLAIN,20);
    g.setFont(font1);


    for(int c=0;c<=1;c++)
    	for(int r=0;r<=CIS.size();r++) {
    	   g.drawLine(xMargin+lengthCell*c,yMargin,xMargin+lengthCell*c,yMargin+lengthCell*CIS.size());
           g.drawLine(xMargin,yMargin+lengthCell*r,xMargin+lengthCell*1,yMargin+lengthCell*r);

           if(0<r)g.drawString(CIS.get(r-1).getName(), (int)(xMargin+1.2*lengthCell), (int)(yMargin+lengthCell*r-lengthCell/2.8));
    	}
    for(int c=1;c<=1;c++)
      for(int r=1;r<=CIS.size();r++)
       //if(そのクラスが不調和を持っていれば)
      if(CIS.get(r-1).isRPB()|CIS.get(r-1).isTB())g.fillRect(xMargin+(c-1)*lengthCell+1,yMargin+(r-1)*lengthCell+1,lengthCell,lengthCell);
  }
  //各マスの下に名前を表示する。
  //マスの間には間隔を開ける
  //各マスはその不調和の数に比例して濃淡表示される。
  //将来的にはそのブロックをクリックすることで不調和のコードを開く機能を追加できると良い的なこと言ってたらOK

  public int[] calcPrefferedPlace() {
    int height=this.getHeight();
    int width=this.getWidth();

    float windowRatio=(float)height/(float)width;
    float boardRatio=(float)CIS.size()/(float)1;
    int lengthCell;
    if(windowRatio>boardRatio)lengthCell=this.getWidth()/1;
    else lengthCell=this.getHeight()/CIS.size();

    int xMargin=windowRatio>boardRatio?0:10;//(width-(1*lengthCell))/2;
    int yMargin=windowRatio>boardRatio?(height-(CIS.size()*lengthCell))/2:5;

    return new int[] {xMargin,yMargin,lengthCell};
  }
}

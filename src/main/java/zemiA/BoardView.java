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
    int length=calcPrefferedPlace()[2];


    int height=this.getHeight();
    int width=this.getWidth();

    int number=0;
    //セルの単位長さを求める
   for(int i=0;i<CIS.size();i++) {
     if(CIS.get(i).getMethodsList().size()>10)number+=3;
     else if(CIS.get(i).getMethodsList().size()>5)number+=2;
     else number+=1;
   }
   length=height/number;

    int sumOflengthCell=0;
    for(int c=0;c<1;c++)
    	for(int r=0;r<CIS.size();r++) {
    	    int howManyMethods=0;
    		if(CIS.get(r).getMethodsList().size()>10)howManyMethods=3;
    		else if(CIS.get(r).getMethodsList().size()>5)howManyMethods=2;
    		else howManyMethods=1;

    		int lengthCell=length;
    		xMargin=calcPrefferedPlace()[0];
    		//ウィンドウサイズに合わせてlengthCellを決定
    		if(howManyMethods==1) {
    			lengthCell=length*1;
    			xMargin=xMargin+(int) (length*3-lengthCell)/2;
    		}
    		if(howManyMethods==2) {
    			lengthCell=length*2;
    			xMargin=xMargin+(int)(length*3-lengthCell)/2;
    		}
    	    if(howManyMethods==3) lengthCell=length*3;
    		//マス左線
			g.drawLine(xMargin,sumOflengthCell,xMargin,sumOflengthCell+lengthCell);
    	   //マス右線
    	   g.drawLine(xMargin+lengthCell,sumOflengthCell,xMargin+lengthCell,sumOflengthCell+lengthCell);
    	   //マス上線
           g.drawLine(xMargin,sumOflengthCell,xMargin+lengthCell,sumOflengthCell);
           //マス下線
           g.drawLine(xMargin,sumOflengthCell+lengthCell,xMargin+lengthCell,sumOflengthCell+lengthCell);
           //クラス名ラベル
           int fontSize=length;
           if(howManyMethods==2)fontSize=(int)(length*1.5);
           if(howManyMethods==3)fontSize=length*2;
           Font font1=new Font("ＭＳ　Ｐゴシック",Font.PLAIN,(int)(fontSize));
           g.setFont(font1);
           g.drawString(CIS.get(r).getName(), (int)(10+length*3), (int)(sumOflengthCell+lengthCell/2+8));
           if(CIS.get(r).isRPB()|CIS.get(r).isTB())g.fillRect(xMargin+(c)*lengthCell,yMargin+(r)*lengthCell+1,lengthCell,lengthCell);
           sumOflengthCell+=lengthCell;
    	}
    }


  public int[] calcPrefferedPlace() {
    int height=this.getHeight();
    int width=this.getWidth();

    float windowRatio=(float)height/(float)width;
    float boardRatio=(float)CIS.size()/(float)1;
    int lengthCell;
    if(windowRatio>boardRatio)lengthCell=this.getWidth()/1;
    else lengthCell=this.getHeight()/(CIS.size()+10);

    int xMargin=10;
    int yMargin=windowRatio>boardRatio?(height-(CIS.size()*lengthCell))/2:5;

    return new int[] {xMargin,yMargin,lengthCell};
  }
}











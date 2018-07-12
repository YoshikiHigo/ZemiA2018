package zemiA;
import java.util.ArrayList;

public class BoardModel {
private int cols;
private int rows;
private boolean[][] cellsNow;
private ArrayList<BoardListener> listeners;
private ArrayList<boolean[][]> boards;


//1�n�܂�c�I���̂���
public BoardModel(int c ,int r) {
  cols=c;
  rows=r;
  cellsNow=new boolean[rows+2][cols+2];
  listeners=new ArrayList<BoardListener>();
  boards=new ArrayList<boolean[][]>();
  fireUpdate();
}

private boolean[][] createNewCells(int c,int r){
  boolean[][] newCells=new boolean[r+2][c+2];
  for(int i=0;i<r+2;i++)
    for(int j=0;j<c+2;j++)
      newCells[i][j]=false;
    return newCells;
}

public int getCols() {  return cols;}
public int getRows() {return rows;}

public void changeBoardSize(int c,int r) {
  boards.clear();
  cellsNow=createNewCells(c,r);
  this.cols=c;
  this.rows=r;
  fireUpdate();
}

public void addListener(BoardListener listener) {listeners.add(listener);}

public void next() {
  if(boards.size()==32) {
    boards.remove(0);
    boards.add(cellsNow);
  }else {
    boards.add(cellsNow);
  }
  boolean[][] cellsNew=new boolean[rows+2][cols+2];
  for(int r=1;r<=rows;r++) {
    for(int c=1;c<=cols;c++) {
      if(cellsNow[r][c]==true) {
        if(lookAround(c,r)==2|lookAround(c,r)==3)cellsNew[r][c]=true;
        else cellsNew[r][c]=false;
      }
      else {
        if(lookAround(c,r)==3)cellsNew[r][c]=true;
        else cellsNew[r][c]=false;
      }
    }
  }
  cellsNow=cellsNew;
  fireUpdate();
}
  private int lookAround(int c,int r) {
    int howManyAliveCells=0;
    for(int i=r-1;i<=r+1;i++) {
      for(int j=c-1;j<=c+1;j++) {
        if(cellsNow[i][j]==true)if(i!=r|j!=c)howManyAliveCells++;
      }
    }
  return howManyAliveCells;
  }

  public void changeCellState(int c,int r) {
    if(boards.size()==32) {
    boards.remove(0);
    boards.add(cellsNow);
  }else {
    boards.add(cellsNow);
  }
  boolean[][] cellsNew=new boolean[rows+2][cols+2];
    for(int i=0;i<=rows;i++)
      for(int j=0;j<=cols;j++)
        {
        if(cellsNow[i][j]==true)cellsNew[i][j]=true;
        else cellsNew[i][j]=false;
        }
    if(cellsNew[r][c]==false)cellsNew[r][c]=true;
    else cellsNew[r][c]=false;
    cellsNow=cellsNew;
    fireUpdate();
  }


  public boolean isUndoable() {return !boards.isEmpty();}

  public void undo() {
  if(isUndoable()) {
      cellsNow=boards.get(boards.size()-1);
      boards.remove(boards.size()-1);
      fireUpdate();
    }
  }

  public void fireUpdate() {
    for(BoardListener listener:listeners) {
      listener.updated(this);
    }
  }

  public boolean isAlive(int c,int r) {
    if(cellsNow[r][c]==false)return false;
    else return true;
  }
}
package zemiA;

public class  Visualizer implements Runnable{

	int NOC;

	public void visualize(int NOC) {
      NOC=this.NOC;
		run();
	}

  public void run() {
    new MainWindow(4,1);
  }
}
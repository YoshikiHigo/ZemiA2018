package zemiA;

import java.util.List;

public class  Visualizer implements Runnable{

	List<ClassInformation> CIS;

	public void visualize(List<ClassInformation> CIS) {
      this.CIS=CIS;
		run();
	}

  public void run() {
    new MainWindow(CIS);
  }
}
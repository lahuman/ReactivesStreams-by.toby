package lahuman.utils;

public enum Logs {

  INSTANCE;
  
  public void sysOut(Object o){
    System.out.println(Thread.currentThread().getName() + "::"+ o);
  }
}

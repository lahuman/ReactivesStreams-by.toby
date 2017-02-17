package lahuman.toby01;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// www.reactive-streams.org
// reactivex.io
public class Ob {

  // Iterable <---------> Observable (duality) // Source -> Event/Data -> Observer
  // Pull                  Push
  
  
  public static void IterableMain() {
    Iterable<Integer> iter = () -> new Iterator<Integer>() {
      int i=0;
      final static int MAX = 10;
      @Override
      public boolean hasNext() {
        return i < MAX;
      }
      @Override
      public Integer next() {
        return ++i;
      }
    };
    
    for(Integer i :iter){
      System.out.println(i);
    }
    
    for(Iterator<Integer> it = iter.iterator(); it.hasNext();){
      System.out.println(it.next());
    }
  }
  
  
  
  // Observable
  
  static class IntObserble extends Observable implements Runnable {

    @Override
    public void run() {
      for(int i=1; i<=10; i++){
        setChanged();            
        notifyObservers(i);      // push
      }
    }
    
  }

  public static void main(String[] args) {
    Observer ob = new Observer(){
      @Override
      public void update(Observable o, Object arg) {
        System.out.println(Thread.currentThread().getName() + " " + arg);
      }
    };
    
    IntObserble io = new IntObserble();
    io.addObserver(ob);
    
    ExecutorService es = Executors.newSingleThreadExecutor();
    es.execute(io);

    System.out.println(Thread.currentThread().getName() + "   EXIT");
    es.shutdown();
  }
}

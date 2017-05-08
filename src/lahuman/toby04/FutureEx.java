package lahuman.toby04;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import lahuman.utils.Logs;

public class FutureEx {
  interface SuccessCallback {
    void onSuccess(String result);
  }
  interface ExceptionCallback {
    void onError(Throwable t);
  }
  public static class CallbackFutureTask extends FutureTask<String>{
    SuccessCallback sc;
    ExceptionCallback ec;
    
    
    public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec){
      super(callable);
      this.sc = Objects.requireNonNull(sc);
      this.ec = Objects.requireNonNull(ec);
    }
    @Override
    protected void done() {
        try {
          sc.onSuccess(get());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
          ec.onError(e.getCause());
        }
    }
  }
  
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    ExecutorService es = Executors.newCachedThreadPool();
    
    FutureTask<String> f = new CallbackFutureTask(() ->{
      Thread.sleep(2000);
      if(1==1) throw new RuntimeException("Async ERROR");
      Logs.INSTANCE.sysOut("Async");
      return "Hello";
    }, Logs.INSTANCE::sysOut
     , Logs.INSTANCE::sysOut);
    es.execute(f);
    es.shutdown();    
        
//    Future<String> f = es.submit(() -> {
//        Thread.sleep(2000);
//        Logs.INSTANCE.sysOut("Async");
//        return "Hello";
//    });
//    Logs.INSTANCE.sysOut(f.isDone());
//    Thread.sleep(2000);
//    Logs.INSTANCE.sysOut("Exit");
//    Logs.INSTANCE.sysOut(f.isDone());
//    Logs.INSTANCE.sysOut(f.get());
  }
  
}

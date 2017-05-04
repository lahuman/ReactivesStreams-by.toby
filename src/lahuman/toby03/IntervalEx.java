package lahuman.toby03;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class IntervalEx {

  public static void main(String[] args) {
    Publisher<Integer> pub = sub ->{
      sub.onSubscribe(new Subscription(){
        int no = 0;
        boolean cancelled = false;
        @Override
        public void cancel() {
          cancelled = true;
        }

        @Override
        public void request(long arg0) {
          ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
          exec.scheduleAtFixedRate(()->{
            if(cancelled){
              exec.shutdown();
              return;
            }
            sub.onNext(no++);
          }, 0, 300, TimeUnit.MILLISECONDS);
        }
        
      });
    };
    
    Publisher<Integer> takePub = sub ->{
      pub.subscribe(new Subscriber<Integer>(){
        int count = 0;
        Subscription subsc;
        @Override
        public void onComplete() {
          
        }

        @Override
        public void onError(Throwable arg0) {
          
        }

        @Override
        public void onNext(Integer arg0) {
          sub.onNext(arg0);
          if(count++ > 10){
            subsc.cancel();
          }
        }

        @Override
        public void onSubscribe(Subscription s) {
          subsc = s;
          sub.onSubscribe(s);
        }
        
      });
    };
    
    takePub.subscribe(new Subscriber<Integer>(){

      @Override
      public void onComplete() {
        System.out.println(Thread.currentThread().getName() + ":: onComplete");
      }

      @Override
      public void onError(Throwable arg0) {
        System.out.println(Thread.currentThread().getName() + ":: onError");
      }

      @Override
      public void onNext(Integer integer) {
        System.out.println(Thread.currentThread().getName() + ":: onNext :"+integer);
      }

      @Override
      public void onSubscribe(Subscription s) {
        System.out.println(Thread.currentThread().getName() + ":: onSubscribe");
        s.request(Long.MAX_VALUE);
      }
      
    });
  }
}

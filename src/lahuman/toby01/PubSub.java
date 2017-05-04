package lahuman.toby01;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PubSub {

  public static void main(String[] args) throws InterruptedException {
    Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);
    ExecutorService es = Executors.newCachedThreadPool(); 

    Publisher<Integer> p = new Publisher<Integer>() {
      Iterator<Integer> it = itr.iterator();
     
      @Override
      public void subscribe(Subscriber subscriber) {
        subscriber.onSubscribe(new Subscription() {
          
          @Override
          public void request(long n) {
            es.execute(()->{
              int i = 0;
              try {
                while (i++ < n) {
                  if (it.hasNext()) {
                    subscriber.onNext(it.next());
                  } else {
                    subscriber.onComplete();
                    break;
                  }
                }
              } catch (RuntimeException e) {
                subscriber.onError(e);
              }
            });
          }

          @Override
          public void cancel() {

          }
        });
      }

    };

    Subscriber<Integer> s = new Subscriber<Integer>() {
      Subscription subscription;
      final int SIZE = 2; 
      int bufferSize = SIZE;
      @Override
      public void onComplete() {
        System.out.println("onComplete");
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("onError : " + throwable.getMessage());
      }

      @Override
      public void onNext(Integer item) {
        System.out.println(Thread.currentThread().getName() + " onNext " + item);
        if(--bufferSize <=0 ){
          bufferSize = SIZE;
          this.subscription.request(SIZE);
        }
      }

      @Override
      public void onSubscribe(Subscription subscription) {
        System.out.println("onSubscription");
        this.subscription =  subscription;
        this.subscription.request(SIZE);
      }
    };
    
    p.subscribe(s);
    
    es.awaitTermination(10, TimeUnit.SECONDS);
    es.shutdown();
  }
}

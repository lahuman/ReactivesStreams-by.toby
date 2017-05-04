package lahuman.toby03;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class SchedulerEx {

  public static void main(String[] args) {
    Publisher<Integer> pub = sub -> {
        sub.onSubscribe(new Subscription() {
          
          @Override
          public void request(long arg0) {
              System.out.println(Thread.currentThread().getName() + ":: Request");
              sub.onNext(1);
              sub.onNext(2);
              sub.onNext(3);
              sub.onNext(4);
              sub.onNext(5);
              sub.onComplete();
          }
          
          @Override
          public void cancel() {
            
          }
        });
    };
   
    //pub 
    Publisher<Integer> subOnPub = sub -> {
      ExecutorService es = Executors.newSingleThreadExecutor();
      es.execute(()->pub.subscribe(sub));
    };
    
//    //sub
//    subOnPub.subscribe(new Subscriber<Integer>(){
//
//      @Override
//      public void onComplete() {
//        System.out.println(Thread.currentThread().getName() + ":: onComplete");
//      }
//
//      @Override
//      public void onError(Throwable arg0) {
//        System.out.println(Thread.currentThread().getName() + ":: onError");
//      }
//
//      @Override
//      public void onNext(Integer integer) {
//        System.out.println(Thread.currentThread().getName() + ":: onNext :"+integer);
//      }
//
//      @Override
//      public void onSubscribe(Subscription s) {
//        System.out.println(Thread.currentThread().getName() + ":: onSubscribe");
//        s.request(Long.MAX_VALUE);
//      }
//      
//    });
    
    
    Publisher<Integer> pubOnPub = sub -> {
      pub.subscribe(new Subscriber<Integer>(){
        ExecutorService es = Executors.newSingleThreadExecutor();
        
        @Override
        public void onComplete() {
          es.execute(()->sub.onComplete());
          es.shutdown();
        }

        @Override
        public void onError(Throwable arg0) {
          es.execute(()->sub.onError(arg0));
          es.shutdown();
        }

        @Override
        public void onNext(Integer integer) {
          es.execute(()->sub.onNext(integer));
        }

        @Override
        public void onSubscribe(Subscription s) {
          es.execute(()->sub.onSubscribe(s));
        }
        
      });
    };
    
    
    

    pubOnPub.subscribe(new Subscriber<Integer>(){

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
    System.out.println("Exit");
  }
}

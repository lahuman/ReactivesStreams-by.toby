package lahuman.toby02;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * 
 * Reactive Streams - Operators
 * 
 * Publisher -> [Data1] -> mapPub -> [Data2] -> logSub
 *                      <- subscribe(logSub)
 *                      -> onSubscribe(s)
 *                      -> onNext
 *                      -> onNext
 *                      -> onComplete
 * 1. map(d1 -> f -d2)
 * 2. sum
 */
public class PubSub {

  public static void main(String[] args) {
    Publisher<Integer> pub = iterPub(Stream.iterate(1,  a->a+1).limit(10).collect(Collectors.toList()));
    
//    Publisher<Integer> mapPub = mapPub(pub, s -> s*10);
//    Publisher<Integer> map2Pub = mapPub(mapPub, s -> -s);
//    map2Pub.subscribe(logSub());
    
    
//    Publisher<Integer> sumPub = sumPub(pub);
//    sumPub.subscribe(logSub());
   

    /*
     * 1,2,3,4,5
     * 0 -> (0,1) -> 0 + 1 = 1
     * 1 -> (1,2) -> 1 + 1 = 2
     * 3 -> (3,3) -> 3 + 3 = 6
     * 6 -> ...
     *  
     */
//    Publisher<Integer> reducePub = reducePub(pub, 0, (a,b)->a+b);
//    reducePub.subscribe(logSub());
    
    
//    Publisher<Integer> gMapPub = gMapPub(pub, s -> s*10);
//    gMapPub.subscribe(logSub());
    
//    Publisher<String> t2rMapPub = t2rMapPub(pub, s -> "[" + s +"]");
//    t2rMapPub.subscribe(logSub());
    
  Publisher<StringBuilder> t2rReducePub = t2rReducePub(pub, new StringBuilder(), (a,b)->a.append(b+","));
  t2rReducePub.subscribe(logSub());
  }

  private static <T,R> Publisher<R> t2rReducePub(Publisher<T>pub, R init,
      BiFunction<R, T, R> bf) {
    return new Publisher<R>(){

      @Override
      public void subscribe(Subscriber<? super R> sub) {
        pub.subscribe(new DelegateSub<T, R>(sub){
          R result = init;
          @Override
          public void onNext(T i){
            result = bf.apply(result, i);
          }
          @Override
          public void onComplete() {
            sub.onNext(result);
            sub.onComplete();
          }
          
        });
      }};    
  }
  
//  T -> R
  private static <T,R> Publisher<R> t2rMapPub(Publisher<T> pub,
      Function<T, R> f) {
    return new Publisher<R>(){

      @Override
      public void subscribe(Subscriber<? super R> sub) {
        pub.subscribe(new DelegateSub<T, R>(sub){
          @Override
          public void onNext(T i){
            sub.onNext(f.apply(i));
          }
        });
      }};
  }
  
  private static <T> Publisher<T> gMapPub(Publisher<T> pub,
      Function<T, T> f) {
    return new Publisher<T>(){

      @Override
      public void subscribe(Subscriber<? super T> sub) {
        pub.subscribe(new DelegateSub<T, T>(sub){
          @Override
          public void onNext(T i){
            sub.onNext(f.apply(i));
          }
        });
      }};
  }
  
  
  private static Publisher<Integer> reducePub(Publisher<Integer> pub, int init,
      BiFunction<Integer, Integer, Integer> bf) {
    return new Publisher<Integer>(){

      @Override
      public void subscribe(Subscriber<? super Integer> sub) {
        pub.subscribe(new DelegateSub<Integer, Integer>(sub){
          int result = init;
          @Override
          public void onNext(Integer i){
            result = bf.apply(result, i);
          }
          @Override
          public void onComplete() {
            sub.onNext(result);
            sub.onComplete();
          }
          
        });
      }};    
  }

  private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
    return new Publisher<Integer>() {

      @Override
      public void subscribe(Subscriber<? super Integer> sub) {
        pub.subscribe(new DelegateSub<Integer, Integer>(sub){
          int sum = 0;
          @Override
          public void onNext(Integer i){
            sum +=i;
          }
          @Override
          public void onComplete() {
            sub.onNext(sum);
            sub.onComplete();
          }
        });
      }};
  }

  private static Publisher<Integer> mapPub(Publisher<Integer> pub,
      Function<Integer, Integer> f) {
    return new Publisher<Integer>(){

      @Override
      public void subscribe(Subscriber<? super Integer> sub) {
        pub.subscribe(new DelegateSub<Integer, Integer>(sub){
          @Override
          public void onNext(Integer i){
            sub.onNext(f.apply(i));
          }
        });
      }};
  }

  private static <T> Subscriber<T> logSub() {
    Subscriber<T> sub = new Subscriber<T>() {

      @Override
      public void onComplete() {
        System.out.println("onComplete");
      }

      @Override
      public void onError(Throwable t) {
        System.out.println("onError:"+t);
      }

      @Override
      public void onNext(T integer) {
        System.out.println("onNext:"+integer);
      }

      @Override
      public void onSubscribe(Subscription s) {
        System.out.println("onSubscrible:");
        s.request(Long.MAX_VALUE);
      }
    };
    return sub;
  }

  private static Publisher<Integer> iterPub(List<Integer> iter) {
    Publisher<Integer> pub = new Publisher<Integer>() {

      @Override
      public void subscribe(Subscriber<? super Integer> sub) {
        
        sub.onSubscribe(new Subscription(){

          @Override
          public void cancel() {
            
          }

          @Override
          public void request(long n) {
            try{
            iter.forEach(s->sub.onNext(s));
            sub.onComplete();
            }catch(Throwable t){
              sub.onError(t);
            }
          }});
      }
      
    };
    return pub;
  }
  
}

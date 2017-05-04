package toby03;

import java.time.Duration;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxScEx {

  public static void main(String[] args) {
    Flux.range(1, 10)
      .publishOn(Schedulers.newSingle("pub"))
      .log()
//      .subscribeOn(Schedulers.newSingle("sub"))
      .subscribe(System.out::println);
    
    
    
    Flux.interval(Duration.ofMillis(500))
      .take(10)
      .subscribe(System.out::println);
    
    System.out.println("Exit");
    
  }
}

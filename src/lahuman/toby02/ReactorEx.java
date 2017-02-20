package lahuman.toby02;

import reactor.core.publisher.Flux;

public class ReactorEx {
  public static void main(String[] args) {
    Flux.<Integer>create(s->{
      s.next(1);
      s.next(2);
      s.next(3);
      s.complete();
      
    })
    .log() // see the operation
    .map(s->s*10)
    .reduce(0, (a,b)->a+b)
    .log()
    .subscribe(System.out::println);
  }
}

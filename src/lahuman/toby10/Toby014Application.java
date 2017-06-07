package lahuman.toby10;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
@RestController
public class Toby014Application {

  @GetMapping("/event/{id}")
  Mono<List<Event>> hello(@PathVariable long id) {
    List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2"));
    return Mono.just(list);
  }
//  Mono<Event> hello(@PathVariable long id) { // # 0
//    return Mono.just(new Event(id, "event + id"));
//  }
  
  @GetMapping(value="/events", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<Event> events(){
    
    Flux<String> es = Flux.generate(sink -> sink.next("Value"));
    Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
    
    return Flux.zip(es,  interval).map(tu->new Event(tu.getT2()+1, tu.getT1())).take(10);
      
//    Flux<Event> es = Flux.<Event, Long>generate(()->1L, (id, sink)->{  // # 6
//          sink.next(new Event(id, "value" + id));
//          return id+1;
//        });
//    Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
//    
//    return Flux.zip(es,  interval).map(tu->tu.getT1());
    
//    return Flux // # 5
//        .<Event, Long>generate(()->1L, (id, sink)->{
//          sink.next(new Event(id, "value" + id));
//          return id+1;
//        })
//        .delayElements(Duration.ofSeconds(1))
//        .take(10);
    
//    return Flux // # 4
//        .<Event>generate(sink->sink.next(new Event(System.currentTimeMillis(), "value")))
//        .delayElements(Duration.ofSeconds(1))
//        .take(10);
    
    
//    Stream<Event> s = Stream.generate(() ->new Event(System.currentTimeMillis(), "value")); // # 3
//    return Flux
//        .fromStream(s)
//        .delayElements(Duration.ofSeconds(1))
//        .take(10);
    
//  List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "event2")); // # 2
//  return Flux.fromIterable(list);
    
//    return Flux.just(new Event(1L, "event1"), new Event(2L, "event2")); // # 1
    
  }
  
  public static void main(String[] args) {
    SpringApplication.run(Toby014Application.class, args);
  }
  
  @Data @AllArgsConstructor
  public static class Event {
    long id;
    String value;
  }

}

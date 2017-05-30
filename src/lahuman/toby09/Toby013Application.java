package lahuman.toby09;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
@Slf4j
public class Toby013Application {


  @GetMapping("/")
  public Mono<String> hello() {
    log.info("pos1");
    
    // Publisher -> (Publisher) -> (Publisher) -> subscribe
//    Mono m = Mono.fromSupplier(()->generateHello()).doOnNext(c->log.info(c)).log(); 
    final String msg = generateHello();
    Mono<String> m = Mono.just(msg).doOnNext(c->log.info(c)).log();
    String msg2 = m.block(); // 가능한 사용하지 말아야 함! 
    log.info("pos2: "+ msg2);
    return m;
  }

  private String generateHello() {
    log.info("mehod generateHello()");
    return "Hello Mono";
  }

  public static void main(String[] args) {
    SpringApplication.run(Toby013Application.class, args);
  }

}

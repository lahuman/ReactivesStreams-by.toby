package lahuman.toby08;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * CompletableFuture
 * 
 * @author daniel
 *
 */
@RestController
@SpringBootApplication
@Slf4j
@EnableAsync
public class Tobytv012Application {

  private static final String URL2 = "http://localhost:8081/service2?req={req}";
  private static final String URL1 = "http://localhost:8081/service?req={req}";

  @Autowired
  MyService myService;

  WebClient client = WebClient.create();
  
  @GetMapping("/rest")
  public Mono<String> rest(int idx) {
    
    //선언 하는 것만으로는 호출이 안됨!
    Mono<ClientResponse> res = client.get().uri(URL1, idx).exchange(); // Mono<ClientResponse>
    
    
    // 사용을 해야 호출이 일어남
    Mono<String> body = res.flatMap(c->c.bodyToMono(String.class)) // Mono<String>
                  .doOnNext(c->log.info(c.toString()));  // ADD log
    
    return body
          .flatMap(res1-> client.get().uri(URL2, res1).exchange()) // Mono<ClientResponse
          .flatMap(c -> c.bodyToMono(String.class)) // Mono<String>
          .flatMap(res2->Mono.fromCompletionStage(myService.work(res2))); // Mono<String>
                            // CompletableFuture<String> -> Mono<String>
  }

  @Service
  public static class MyService {
    @Async
    public CompletableFuture<String> work(String req) {
      return CompletableFuture.completedFuture(req + "/asyncwork");
    }
  }
  
  @Bean
  NettyReactiveWebServerFactory nettyReactiveWebServerFactory(){
    return new NettyReactiveWebServerFactory();
  }

  public static void main(String[] args) {
    System.setProperty("reactor.ipc.netty.workCount", "2");
    System.setProperty("reactor.ipc.netty.pool.maxConnections", "2000");
    SpringApplication.run(Tobytv012Application.class, args);
  }

}

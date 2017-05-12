package lahuman.toby05;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;

/**
 *  비동기  RestTemplate과 비동기 MVC의 결합  
 *
 */
@SpringBootApplication
public class Tobytv009Application {
  @RestController
  public static class MyController {
    
    @Autowired
    MyService myService;
    
    // use netty Nio EventLoopGroup
    AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

    @GetMapping("/rest")
    public DeferredResult<String> rest(int idx){
      DeferredResult<String> dr = new DeferredResult<>();
      ListenableFuture<ResponseEntity<String>>  f1 = rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" +idx);
      
      f1.addCallback(s->{
        ListenableFuture<ResponseEntity<String>>  f2 = rt.getForEntity("http://localhost:8081/service2?req={req}", String.class, s.getBody() +idx);
        f2.addCallback(s2->{
//          dr.setResult(s2.getBody());
          ListenableFuture<String> f3 = myService.work(s2.getBody());
          f3.addCallback(s3->{
            dr.setResult(s3);
          }, e->{
            dr.setErrorResult(e.getMessage());  
          });
        }, e->{
          dr.setErrorResult(e.getMessage());
        });
        
      }, e->{
        dr.setErrorResult(e.getMessage());
      });
      
      return dr;
    }
    
    
    @Service
    public static class MyService{
      @Async
      public ListenableFuture<String> work(String req){
        return new AsyncResult<String>(req + "/asyncwork");
      }
    }
    
    @Bean
    public ThreadPoolTaskExecutor myThreadPool(){
      ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
      te.setCorePoolSize(1);
      te.setMaxPoolSize(1);
      te.initialize();
      return te;
      
    }
    
    //background make Thread
//  AsyncRestTemplate rt = new AsyncRestTemplate();
//  @GetMapping("/rest")
//  public ListenableFuture<ResponseEntity<String>> rest(int idx){
//    return rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" +idx);
//  }
    
    
    //background make Thread
//    AsyncRestTemplate rt = new AsyncRestTemplate();
//    
//    @GetMapping("/rest")
//    public ListenableFuture<ResponseEntity<String>> rest(int idx){
//      return rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" +idx);
//    }
    // Block
//    RestTemplate rt = new RestTemplate();
//    @GetMapping("/rest")
//    public String rest(int idx){
//      String res = rt.getForObject("http://localhost:8081/service?req={req}", String.class, "hello" +idx);
//      return res;
//    }
  }
  public static void main(String[] args) {
    SpringApplication.run(Tobytv009Application.class, args);
  }

}

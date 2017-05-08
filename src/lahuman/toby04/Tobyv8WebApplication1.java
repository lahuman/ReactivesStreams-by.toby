package lahuman.toby04;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

//1        ServletThread1 - req -> blocking IO(DB, API) => res(html)
//2    NIO ST2 - req -> WorkThread => res(html)
//3        ST3
//4        ST4

@SpringBootApplication
@Slf4j
@EnableAsync
public class Tobyv8WebApplication1 {
@RestController
  public static class myController{
//    @Autowired MyService myService;
    
    @GetMapping("callable")
    public Callable<String> async() throws InterruptedException{
      log.info("Callble");
      return ()->{
        log.info("Async");
        Thread.sleep(2000);
        return "hello";
      };
    }
//    public String async() throws InterruptedException{
//      log.info("Async");
//      Thread.sleep(2000);
//      return "hello";
//    }
    
  }
  
  @Component
  public static class Myservice {
    @Async
    public ListenableFuture<String> hello() throws InterruptedException {
      log.info("Hello()");
      Thread.sleep(2000);
      return new AsyncResult<>("Hello");
    }
  }
  
  
  
  public static void main(String[] args) {
    SpringApplication.run(Tobyv8WebApplication1.class, args);
  }

}

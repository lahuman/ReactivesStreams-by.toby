package lahuman.toby04;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@EnableAsync
public class Tobyv8WebApplicationDeferred {
  @RestController
  public static class myController{
    Queue<DeferredResult<String>> results = new ConcurrentLinkedQueue<>();
    
    @GetMapping("/dr")
    public DeferredResult<String> async() throws InterruptedException{
      log.info("dr");
      DeferredResult<String> dr = new DeferredResult<>(6000000L);
      results.add(dr);
      return dr;
    }
    
    @GetMapping("/dr/count")
    public String drcount() {
      return String.valueOf(results.size());
    }
    
    @GetMapping("/dr/event")
    public String drevent(String msg) {
      for(DeferredResult<String> dr : results){
        dr.setResult("hello " + msg);
        results.remove(dr);
      }
      return "OK";
    }
}




public static void main(String[] args) {
  SpringApplication.run(Tobyv8WebApplicationDeferred.class, args);
}


}

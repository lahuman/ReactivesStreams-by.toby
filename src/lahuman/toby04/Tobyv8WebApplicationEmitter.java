package lahuman.toby04;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

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
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
@EnableAsync
public class Tobyv8WebApplicationEmitter {
  @RestController
  public static class myController{
    Queue<DeferredResult<String>> results = new ConcurrentLinkedQueue<>();
    
    @GetMapping("/emitter")
    public ResponseBodyEmitter emitter() {
      log.info("dr");
      ResponseBodyEmitter emitter = new ResponseBodyEmitter();
      
      Executors.newSingleThreadExecutor().submit(()-> {
        try {
          for(int i=0; i<=50; i++){
              emitter.send("<p>Stream "+i +"</p>");
            Thread.sleep(100);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      return emitter;
    }
    
}

public static void main(String[] args) {
  SpringApplication.run(Tobyv8WebApplicationEmitter.class, args);
}


}

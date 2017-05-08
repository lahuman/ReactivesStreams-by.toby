package lahuman.toby04;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import lahuman.utils.Logs;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableAsync
@Slf4j
public class Tobytv8Application {

  //Future <- JAVA 8
  //ListenableFuture <- Spring 4.0 +
  
  @Component
  public static class Myservice {
    @Async("tp")
    public ListenableFuture<String> hello() throws InterruptedException {
      log.info("Hello()");
      Thread.sleep(2000);
      return new AsyncResult<>("Hello");
    }
  }
  
  public static void main(String[] args) {
    try(ConfigurableApplicationContext c = SpringApplication.run(Tobytv8Application.class, args)){
      
    }
  }
  @Autowired
  Myservice myService;
  // 기본은 SimpleThread 인데 이것을 운영에서 사용하는것은 상당히 위험 하다!!!
  @Bean
  ThreadPoolTaskExecutor tp(){
    ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
    te.setCorePoolSize(10);
    te.setMaxPoolSize(100);
    te.setQueueCapacity(200);
    te.setThreadNamePrefix("mythread");
    te.initialize();
    return te;
  }
  
  
  @Bean
  ApplicationRunner run() {
    return args -> {
      log.info("run()");
      ListenableFuture<String> res = myService.hello();
      res.addCallback(s-> log.info(s), e->log.info(e.getMessage()));
//      res.cancel(true); //if want cancel. 
      log.info("exit: ");
    };
  }
  

}

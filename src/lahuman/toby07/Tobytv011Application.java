package lahuman.toby07;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

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
import org.springframework.web.context.request.async.DeferredResult;

import io.netty.channel.nio.NioEventLoopGroup;
/**
 * CompletableFuture
 * @author daniel
 *
 */
@SpringBootApplication
public class Tobytv011Application {
  @RestController
  public static class MyController {

    private static final String URL2 = "http://localhost:8081/service2?req={req}";

    private static final String URL1 = "http://localhost:8081/service?req={req}";

    @Autowired
    MyService myService;

    // use netty Nio EventLoopGroup
    AsyncRestTemplate rt =
        new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

    @GetMapping("/rest")
    public DeferredResult<String> rest(int idx) {
      DeferredResult<String> dr = new DeferredResult<>();
      
      toCF(rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" +idx))
        .thenCompose(s -> {
//          if(1==1) throw new RuntimeException("ERROR");
          return toCF(rt.getForEntity(URL2,String.class, s.getBody()));
        })
//        .thenCompose(s2 -> toCF(myService.work(s2.getBody())))
        .thenApplyAsync(s2 -> myService.work(s2.getBody()))
        .thenAccept(s3-> dr.setResult(s3))
        .exceptionally(e->{dr.setErrorResult(e.getMessage()); return (Void)null;});
      return dr;
    }
  }
  
  static <T> CompletableFuture<T> toCF(ListenableFuture<T> lf){
    CompletableFuture<T> cf = new CompletableFuture<T>();
    lf.addCallback(s->cf.complete(s), e->cf.completeExceptionally(e));
    
    return cf;
  }
  
  public static class AcceptCompletion<S> extends Completion<S, Void>{
    Consumer<S> con;
    public AcceptCompletion(Consumer<S> con){
      this.con = con;
    }
    
    @Override
    void run(S value) {
      con.accept(value);
    }
  }
  
  public static class ErrorCompletion<T> extends Completion<T, T>{
    Consumer<Throwable> econ;
    public ErrorCompletion(Consumer<Throwable> econ){
      this.econ = econ;
    }
    
    @Override
    void run(T value) {
      if (next != null) next.run(value);
    }
    
    @Override
    void error(Throwable e){
      econ.accept(e);
    }
  }
  
  public static class ApplyCompletion<S, T> extends Completion<S, T>{
    Function<S, ListenableFuture<T>> fn;
    public ApplyCompletion(Function<S, ListenableFuture<T>> fn){
      this.fn = fn;
    }    
    
    @Override
    void run(S value){
        ListenableFuture<T> lf = fn.apply(value);
        lf.addCallback(s->complets(s), e->error(e));
    }
  }
  
  public static class Completion<S, T>{
    Completion next;
    
    public <V> Completion<T, V> andApply(Function<T, ListenableFuture<V>> fn) {
      Completion<T, V> c = new ApplyCompletion<>(fn);
      this.next = c;
      return c;
    }
    
    public Completion<T, T> andError(Consumer<Throwable> econ) {
      Completion<T, T> c = new ErrorCompletion<>(econ);
      this.next = c;
      return c;
    }

    public void andAccept(Consumer<T> con){
      Completion<T, Void> c = new AcceptCompletion<>(con);
      this.next = c;
    }
    
    public static <S, T> Completion<S, T> from(ListenableFuture<T> lf) {
      Completion<S, T> c = new Completion();
      lf.addCallback(s->{
        c.complets(s);
      }, e->{
        c.error(e);
      });
      return c;
      
    }

    void error(Throwable e) {
      if(next != null) next.error(e);
    }
    
    void complets(T s) {
      if(next != null) next.run(s);
    }
    void run(S value) {
      
    }
  }


  @Service
  public static class MyService {
//    @Async
//    public ListenableFuture<String> work(String req) {
//      return new AsyncResult<String>(req + "/asyncwork");
//    }
    
    public String work(String req) {
      return req + "/asyncwork";
    }
  }

  @Bean
  public ThreadPoolTaskExecutor myThreadPool() {
    ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
    te.setCorePoolSize(1);
    te.setMaxPoolSize(1);
    te.initialize();
    return te;

  }


  public static void main(String[] args) {
    SpringApplication.run(Tobytv011Application.class, args);
  }

}

package lahuman.toby07;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CFuture {

  public static void main(String[] args) throws InterruptedException, ExecutionException {
//    CompletableFuture<Integer> f = CompletableFuture.completedFuture(1); // 이미 완료된 CompletableFuture를 만든다.
    // f.get() 하는 순간 오류 발생 
//    CompletableFuture<Integer> f = new CompletableFuture<>();
//    f.completeExceptionally(new RuntimeException());
//    System.out.println(f.get());
    
//    CompletableFuture
//      .runAsync(()-> log.info("runAsync"))
//      .thenRun(()-> log.info("thenRun"))
//      .thenRun(()-> log.info("thenRun"));

//    CompletableFuture
//        .supplyAsync(() -> {
//          log.info("runAsync");
//          return 1;
//        }).thenApply(s -> {
//          log.info("thenApply {}", s);
//          return s + 1;
//        }).thenApply(s2 -> {
//          log.info("thenApply {}", s2);
//          return s2 * 3;
//        })
//          .thenAccept(s3 -> log.info("thenAccept {}", s3));

// 리턴을 CompletableFuture 형식을 사용 할 경우 
//    CompletableFuture
//        .supplyAsync(() -> {
//          log.info("runAsync");
//          return 1;
//        })
//        .thenCompose(s -> {
//          log.info("thenApply {}", s);
//          return CompletableFuture.completedFuture(s + 1);
//        })
//        .thenApply(s2 -> {
//          log.info("thenApply {}", s2);
//          return s2 * 3;
//        })
//        .thenAccept(s3 -> log.info("thenAccept {}", s3));


//    예외 처리 방법 
//    CompletableFuture
//      .supplyAsync(() -> {
//        log.info("runAsync");
//        if(1==1) throw new RuntimeException();
//        return 1;
//      })
//      .thenCompose(s -> {
//        log.info("thenApply {}", s);
//        return CompletableFuture.completedFuture(s + 1);
//      })
//      .thenApply(s2 -> {
//        log.info("thenApply {}", s2);
//        return s2 * 3;
//      })
//      .exceptionally(e->-10)
//      .thenAccept(s3 -> log.info("thenAccept {}", s3));

    // 실행 Thread 지정 
   ExecutorService es = Executors.newFixedThreadPool(10);
    CompletableFuture
      .supplyAsync(() -> {
        log.info("runAsync");
        return 1;
      }, es)
      .thenCompose(s -> {
        log.info("thenApply {}", s);
        return CompletableFuture.completedFuture(s + 1);
      })
      .thenApplyAsync(s2 -> {
        log.info("thenApply {}", s2);
        return s2 * 3;
      }, es)
      .exceptionally(e->-10)
      .thenAcceptAsync(s3 -> log.info("thenAccept {}", s3), es);
    es.awaitTermination(10, TimeUnit.SECONDS);
    es.shutdown();
    
    log.info("exit");
    
    ForkJoinPool.commonPool().shutdown();
    ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
  }

}

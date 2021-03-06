package lahuman.toby05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RemoteService {

  @RestController
  public static class MyController {
    @GetMapping("/service")
    public String service(String req) throws InterruptedException{
      Thread.sleep(2000);
      return req + "/service";
    }
    
    @GetMapping("/service2")
    public String servic2(String req) throws InterruptedException{
      Thread.sleep(2000);
      return req + "/service2";
    }
  }
  public static void main(String[] args) {
    System.setProperty("SERVER_PORT", "8081");
    System.setProperty("server.tomcat.max-threads", "1000");
    SpringApplication.run(RemoteService.class, args);
  }
}

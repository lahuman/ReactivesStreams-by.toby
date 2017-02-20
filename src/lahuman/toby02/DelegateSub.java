package lahuman.toby02;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DelegateSub<T, R> implements Subscriber<T> {
  Subscriber sub;

  public DelegateSub(Subscriber sub) {
    this.sub = sub;
  }

  @Override
  public void onComplete() {
    sub.onComplete();
  }

  @Override
  public void onError(Throwable t) {
    sub.onError(t);
  }

  @Override
  public void onNext(T n) {
    sub.onNext(n);
  }

  @Override
  public void onSubscribe(Subscription s) {
    sub.onSubscribe(s);
  }
}

# algebra
Generic ADTs in Java.

## Using

To use `algebra` in your project simply include it in your POM:

```xml
    <dependency>
      <groupId>com.hubspot</groupId>
      <artifactId>algebra</artifactId>
      <version>1.0</version>
    </dependency>
```

## Provided Types

The main type provided by `algebra` is the `Result<T, E>`. `T` and `E` can be any classes you want, but it is generally advisable that `E` be an enum or another ADT so you can correctly match on error conditions. Lets take a look at an example.

```java
enum Error {
  BROKEN,
  REALLY_BROKEN
}

public Result<String, Error> doWork() {
  if (sucess) {
    return Result.ok(result);
  } else if (thing1Broke) {
    return Result.err(Error.BROKEN);
  }
  
  return Result.err(Error.REALLY_BROKEN);
}
```

If we then want to use `doWork` in a library, but our library only has one error type, we can simply map the error and return a new result:

```java

enum LibError {
  ERROR
}

public Result<String, LibError> doMoreWork() {
  return doWork().mapErr(err -> LibError.ERROR);
}
```

Of course when it gets to our client and they actually want to handle the error they can do so using `match`:

```java
client.doMoreWork().match(
  err -> LOGGER.error("Got error: {}", err),
  ok -> LOGGER.info("Got successful result: {}", ok)
);
```

## Testing

To test code with ADTs, we provide a fluent AssertJ API in `algebra-testing`.

To use `algebra-testing` in your project simply include it in your POM:

```xml
    <dependency>
      <groupId>com.hubspot</groupId>
      <artifactId>algebra--public-testing</artifactId>
      <version>1.0</version>
      <scope>test</scope>
    </dependency>
```

Add a static import and use the assertions:

```java
import static com.hubspot.assertj.algebra.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
```

```java
  @Test
  public void itWorks() {
    assertThat(Result.ok("Ok"))
        .isOk()
        .containsOk("Ok");
  }
```

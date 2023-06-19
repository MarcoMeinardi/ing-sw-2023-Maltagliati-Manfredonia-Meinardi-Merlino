package network;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.util.Optional;

public class FunctionTest extends TestCase {

    public void testCheckResult() {
        final Boolean True = true;
        Function<String,Boolean> function = new Function<String,Boolean>("test", Service.Login);
        assertEquals(Optional.empty(), function.checkResult());
        function.setResult(Result.ok(True, function.id()));
        assertEquals(Optional.of(Result.ok(True, function.id())), function.checkResult());
    }

    public void testGetCall() {
        Function<String,Boolean> function = new Function<String,Boolean>("test", Service.Login);
        assertEquals(new Call<String>("test", Service.Login, function.id()), function.getCall());
    }

    public void testGetParams() {
        Function<String,Boolean> function = new Function<String,Boolean>("test", Service.Login);
        assertEquals("test", function.getParams());
    }

    public void testWaitResult() {
        Function<String,Boolean> function = new Function<String,Boolean>("test", Service.Login);
        Result<Boolean> result = Result.ok(true, function.id());
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                function.setResult(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try{
            assertEquals(result, function.waitResult());
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testWaitResultTimer() {
        Function<String,Boolean> function = new Function<String,Boolean>("test", Service.Login);
        Result<Boolean> result = Result.ok(true, function.id());
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                function.setResult(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try{
            assertEquals(result, function.waitResult());
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        Function<String,Boolean> function2 = new Function<String,Boolean>("test", Service.Login);
        Result<Boolean> result2 = Result.ok(true, function.id());
        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
                function2.setResult(result2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread2.start();
        try{
            assertEquals(result2, function2.waitResult(100));
            throw new AssertionFailedError();
        }catch (Exception e){
            assertEquals(Optional.empty(), function2.checkResult());
        }
    }

    public void testSetResult() {
        Function<String,Boolean> function = new Function<String,Boolean>("test", Service.Login);
        Result<Boolean> result = Result.ok(true, function.id());
        function.setResult(result);
        assertEquals(result, function.checkResult().get());
    }

    public void testId() {
        Function<String,Boolean> function = new Function<String,Boolean>("test", Service.Login);
        assertEquals(function.id(), function.getCall().id());
    }
}
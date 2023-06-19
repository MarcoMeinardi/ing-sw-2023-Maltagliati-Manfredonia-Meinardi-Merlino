package network;

import junit.framework.TestCase;

import java.io.Serializable;
import java.util.UUID;

public class ResultTest extends TestCase {
    public void testOk() {
        UUID caller_id = UUID.randomUUID();
        Result<String> result = Result.ok("test", caller_id);
        assert(result.isOk());
        assert(!result.isErr());
        assert(!result.isEvent());
        assertEquals(result.get().get(), "test");
    }

    public void testErr() {
        UUID caller_id = UUID.randomUUID();
        Exception exception = new Exception("test");
        Result<String> result = Result.err( exception, caller_id);
        assert(!result.isOk());
        assert(result.isErr());
        assert(!result.isEvent());
        assertEquals(result.getException().get(), exception);
    }

    public void testServerPush() {
        ServerEvent<Serializable> event = ServerEvent.Join("test");
        Result<Serializable> result = Result.serverPush(event);
        assert(result.isOk());
        assert(!result.isErr());
        assert(result.isEvent());
        assertEquals(result.get().get(), event);
    }

    public void testEmpty() {
        UUID caller_id = UUID.randomUUID();
        Result<Serializable> result = Result.empty(caller_id);
        assert(result.isOk());
        assert(!result.isErr());
        assert(!result.isEvent());
        assertEquals(result.get().get(), true);
    }

    public void testIsOk() {
        UUID caller_id = UUID.randomUUID();
        Result<String> result = Result.ok("test", caller_id);
        assert(result.isOk());
    }

    public void testIsErr() {
        UUID caller_id = UUID.randomUUID();
        Exception exception = new Exception("test");
        Result<String> result = Result.err( exception, caller_id);
        assert(result.isErr());
    }

    public void testGetException() {
        UUID caller_id = UUID.randomUUID();
        Exception exception = new Exception("test");
        Result<String> result = Result.err( exception, caller_id);
        assertEquals(result.getException().get(), exception);
    }

    public void testUnwrap() {
        try{
            UUID caller_id = UUID.randomUUID();
            Result<String> result = Result.ok("test", caller_id);
            assertEquals(result.unwrap(), "test");
        }catch (Exception e){
            fail();
        }
    }

    public void testGet() {
        UUID caller_id = UUID.randomUUID();
        Result<String> result = Result.ok("test", caller_id);
        assertEquals(result.get().get(), "test");
    }

    public void testUnwrapOrElse() {
        UUID caller_id = UUID.randomUUID();
        Exception exception = new Exception("test");
        Result<String> result = Result.err( exception, caller_id);
        assertEquals(result.unwrapOrElse("test"), "test");
        Result<String> result2 = Result.ok("test2", caller_id);
        assertEquals(result2.unwrapOrElse("test"), "test2");
    }

    public void testUnwrapOrElseThrow() {
        UUID caller_id = UUID.randomUUID();
        Exception exception = new Exception("test");
        Exception exception2 = new Exception("true exception");
        Result<String> result = Result.err( exception, caller_id);
        Result<String> result2 = Result.ok("test", caller_id);
        try{
            assertEquals(result2.unwrapOrElseThrow(exception2), "test");
            result.unwrapOrElseThrow(exception2);
            fail();
        }catch (Exception e){
            assertEquals(e, exception2);
        }
    }

    public void testIsEvent() {
        ServerEvent<Serializable> event = ServerEvent.Join("test");
        Result<Serializable> result = Result.serverPush(event);
        assert(result.isEvent());
    }

    public void testId() {
        UUID caller_id = UUID.randomUUID();
        Result<String> result = Result.ok("test", caller_id);
        assertEquals(result.id(), caller_id);
    }

    public void testTestEquals() {
        UUID caller_id = UUID.randomUUID();
        UUID caller_id2 = UUID.randomUUID();
        Result<String> result = Result.ok("test", caller_id);
        Result<String> result2 = Result.ok("test", caller_id);
        Result<String> result3 = Result.ok("test2", caller_id);
        Result<String> result4 = Result.ok("test", caller_id2);
        assertEquals(result, result2);
        assertNotSame(result, result3);
        assertNotSame(result, result4);
    }
}
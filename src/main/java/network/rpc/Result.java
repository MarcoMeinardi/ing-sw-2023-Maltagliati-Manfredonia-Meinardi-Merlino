package network.rpc;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class Result<T extends Serializable> implements Serializable{
    private T value;
    private Exception exception;

    private UUID id;
    private Result(Exception exception, T value, UUID caller_id){
        this.id = caller_id;
        this.value = value;
        this.exception = exception;
    }

    public static <T extends Serializable> Result<T> ok(T value, UUID caller_id){
        return new Result<T>(null, value, caller_id);
    }

    public static <T extends Serializable> Result<T> err(Exception exception, UUID caller_id){
        return new Result<T>(exception, null, caller_id);
    }

    public static Result<Serializable> serverPush(ServerEvent event){
        return new Result<ServerEvent>(null, event, null);
    }

    public boolean isOk(){
        return exception == null;
    }

    public boolean isErr(){
        return exception != null;
    }

    public Optional<Exception> getException(){
        return Optional.ofNullable(exception);
    }

    public T getOrThrow() throws Exception{
        if(exception != null){
            throw exception;
        }
        return value;
    }

    public Optional<T> get(){
        return Optional.ofNullable(value);
    }

    public T getOrElse(T defaultValue){
        return value == null ? defaultValue : value;
    }

    public T getOrElseThrow(Exception exception) throws Exception{
        if(value == null){
            throw exception;
        }
        return value;
    }

    public UUID id(){
        return id;
    }
}

package network;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Result of a remote call
 * @param <T> the type of the result
 */
public class Result<T extends Serializable> implements Serializable{
    private final T value;
    private final Exception exception;

    private final UUID id;

    /**
     * Create a new result
     * @param exception the exception that occurred
     * @param value the value that was returned
     * @param caller_id the id of the caller
     */
    private Result(Exception exception, T value, UUID caller_id){
        this.id = caller_id;
        this.value = value;
        this.exception = exception;
    }

    /**
     * Create a new result with a value
     * @param value the value
     * @param caller_id the id of the caller
     * @return the result
     * @param <T> the type of the value
     */
    public static <T extends Serializable> Result<T> ok(T value, UUID caller_id){
        return new Result<T>(null, value, caller_id);
    }

    /**
     * Create a new result with an exception
     * @param exception the exception
     * @param caller_id the id of the caller
     * @return the result
     * @param <T> the type of the value that was supposed to be returned
     */
    public static <T extends Serializable> Result<T> err(Exception exception, UUID caller_id){
        return new Result<T>(exception, null, caller_id);
    }

    /**
     * Create a new result with an event
     * @param event the event
     * @return the result
     */
    public static Result<Serializable> serverPush(ServerEvent event){
        return new Result<Serializable>(null, event, null);
    }

    /**
     * Create a new empty result
     * @param caller_id the id of the caller
     * @return the result
     */
    public static Result<Serializable> empty(UUID caller_id){
        return new Result<Serializable>(null, Boolean.TRUE, caller_id);
    }

    /**
     * Check if the result is ok
     * @return true if the result is ok (no exception occurred) false otherwise
     */
    public boolean isOk(){
        return exception == null;
    }

    /**
     * Check if the result is an error
     * @return true if the result is an error (an exception occurred) false otherwise
     */
    public boolean isErr(){
        return exception != null;
    }

    /**
     * Get the exception if it exists
     * @return the exception if it exists, an empty optional otherwise
     */
    public Optional<Exception> getException(){
        return Optional.ofNullable(exception);
    }

    /**
     * Get the value if it exists
     * @return The inner value.
     * @throws Exception if an exception occurred
     */
    public T unwrap() throws Exception{
        if(exception != null){
            throw exception;
        }
        return value;
    }

    /**
     * Get the value if it exists
     * @return the value if it exists, an empty optional otherwise
     */
    public Optional<T> get(){
        return Optional.ofNullable(value);
    }

    /**
     * Get the value if it exists, or a default value otherwise
     * @param defaultValue the default value to return if the result is an error
     * @return the value if it exists, the default value otherwise
     */
    public T unwrapOrElse(T defaultValue){
        return value == null ? defaultValue : value;
    }

    /**
     * Get the value if it exists, or throw an exception otherwise
     * @param exception the exception to throw
     * @return the value if it exists
     * @throws Exception if an exception occurred
     */
    public T unwrapOrElseThrow(Exception exception) throws Exception{
        if(value == null){
            throw exception;
        }
        return value;
    }

    /**
     * Check if the result is an event
     * @return true if the result is an event, false otherwise
     */
    public boolean isEvent(){
        return id == null;
    }

    /**
     * Get the result id
     * @return the id
     */
    public UUID id(){
        return id;
    }

    /**
     * Override the `equals` method
     * Two results are equal if they have the same value, the same exception and the same id
     * @param obj the result to compare with
     * @return if the two results are equal
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Result<?> other){
            boolean equals_value = Objects.equals(this.value, other.value);
            boolean equals_exception = Objects.equals(this.exception, other.exception);
            boolean equals_id = Objects.equals(this.id, other.id);
            return equals_value && equals_exception && equals_id;
        }
        return false;
    }
}

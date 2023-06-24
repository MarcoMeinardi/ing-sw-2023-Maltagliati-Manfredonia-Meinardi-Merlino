package network;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

/**
 * Function class used in the client to represent a remote function call.
 * @param <P> the type of the parameters of the function.
 * @param <R> the type of the result of the function.
 */
public class Function<P extends Serializable,R extends Serializable>{
    private P params;
    private Optional<Result<R>> result;
    private final Object resultLock = new Object();
    private UUID id;
    private Service service;

    /**
     * Constructor for the Function class.
     * @param params the parameters of the function.
     * @param service the service of the function.
     */
    public Function(P params, Service service){
        this.params = params;
        this.service = service;
        this.result = Optional.empty();
        this.id = UUID.randomUUID();
    }

    /**
     * Call the remote function.
     * @param stream the stream to send the call to.
     * @return the function itself.
     * @throws Exception if an error occurs.
     */
    public Function call(ObjectOutputStream stream) throws Exception{
        synchronized (stream){
            stream.writeObject(getCall());
        }
        return this;
    }

    /**
     * Check if the result of the function is ready.
     * @return the result of the function if it is ready, empty otherwise.
     */
    public Optional<Result<R>> checkResult(){
        synchronized (resultLock) {
            return result;
        }
    }

    /**
     * Get the Call object of the function.
     * @return the Call object of the function.
     */
    public Call<P> getCall(){
        return new Call<>(params, service, id);
    }

    /**
     * Get the parameters of the function.
     * @return the parameters of the function.
     */
    public P getParams() {
        return params;
    }

    /**
     * Block the caller until the result of the function is ready.
     * @return the result of the function.
     * @throws Exception if an error occurs while waiting for the result.
     */
    public Result<R> waitResult() throws Exception{
        synchronized (resultLock) {
            while(result.isEmpty()){
                resultLock.wait();
            }
            return result.get();
        }
    }

    /**
     * Block the caller until the result of the function is ready or the timeout is reached.
     * @param timeout the timeout in milliseconds.
     * @return the result of the function.
     * @throws Exception if an error occurs while waiting for the result.
     */
    public Result<R> waitResult(long timeout) throws Exception{
        synchronized (resultLock) {
            while(result.isEmpty()){
                result.wait(timeout);
            }
            return result.get();
        }
    }

    /**
     * Set the result of the function.
     * @param result the result of the function.
     */
    public void setResult(Result<R> result){
        synchronized (resultLock) {
            this.result = Optional.of(result);
            resultLock.notifyAll();
        }
    }

    /**
     * Get the id of the function.
     * @return the id of the function.
     */
    public UUID id(){
        return id;
    }
}

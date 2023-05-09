package network;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class Function<P extends Serializable,R extends Serializable>{
    private P params;
    private Optional<Result<R>> result;
    private final Object resultLock = new Object();
    private UUID id;
    private Service service;

    public Function(P params, Service service){
        this.params = params;
        this.service = service;
        this.result = Optional.empty();
        this.id = UUID.randomUUID();
    }

    public Function call(ObjectOutputStream stream) throws Exception{
        synchronized (stream){
            stream.writeObject(getCall());
        }
        return this;
    }

    public Optional<Result<R>> checkResult(){
        synchronized (resultLock) {
            return result;
        }
    }

    public Call<P> getCall(){
        return new Call(params, service, id);
    }

    public P getParams() {
        return params;
    }

    public Result<R> waitResult() throws Exception{
        synchronized (resultLock) {
            while(result.isEmpty()){
                resultLock.wait();
            }
            return result.get();
        }
    }

    public Result<R> waitResult(long timeout) throws Exception{
        synchronized (resultLock) {
            while(result.isEmpty()){
                result.wait(timeout);
            }
            return result.get();
        }
    }

    public void setResult(Result<R> result){
        synchronized (resultLock) {
            this.result = Optional.of(result);
            resultLock.notifyAll();
        }
    }
    public UUID id(){
        return id;
    }
}

package network.rpc.client;

import network.Call;
import network.RemoteFunctionInterface;
import network.Result;
import network.Service;
import network.parameters.Login;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class Function<P extends Serializable,R extends Serializable> implements RemoteFunctionInterface<P,R> {
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

    protected Function call(ObjectOutputStream stream) throws Exception{
        Call<P> call = new Call(params, service, id);
        synchronized (stream){
            stream.writeObject(call);
        }
        return this;
    }

    public Optional<Result<R>> checkResult(){
        synchronized (resultLock) {
            return result;
        }
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

    protected void setResult(Result<R> result){
        synchronized (resultLock) {
            this.result = Optional.of(result);
            resultLock.notifyAll();
        }
    }
    public UUID id(){
        return id;
    }
}

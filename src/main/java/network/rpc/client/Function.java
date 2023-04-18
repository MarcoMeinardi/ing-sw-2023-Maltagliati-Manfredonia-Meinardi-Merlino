package network.rpc.client;

import network.rpc.Call;
import network.rpc.Result;
import network.rpc.Service;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class Function<P extends Serializable,R extends Serializable> {
    private P params;
    private Optional<Result<R>> result;
    private UUID id;

    private Service service;

    public Function(P params, Service service){
        this.params = params;
        this.result = Optional.empty();
        this.id = UUID.randomUUID();
    }

    protected void call(ObjectOutputStream stream) throws Exception{
        Call<P> call = new Call(params, service, id);
        stream.writeObject(call);
    }

    public Optional<Result<R>> checkResult(){
        synchronized (result){
            return result;
        }
    }

    public Result<R> waitResult() throws Exception{
        synchronized (result){
            while(result.isEmpty()){
                result.wait();
            }
            return result.get();
        }
    }

    protected void setResult(Result<R> result){
        synchronized (result){
            this.result = Optional.of(result);
            result.notifyAll();
        }
    }
}

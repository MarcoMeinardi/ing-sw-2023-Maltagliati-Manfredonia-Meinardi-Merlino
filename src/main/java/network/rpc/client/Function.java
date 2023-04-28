package network.rpc.client;

import network.rpc.Call;
import network.rpc.Result;
import network.rpc.Service;
import network.rpc.parameters.Login;

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
        synchronized (result){
            return result;
        }
    }

    public P getParams() {
        return params;
    }

    public Result<R> waitResult() throws Exception{
        synchronized (result){
            while(result.isEmpty()){
                result.wait();
            }
            return result.get();
        }
    }

    public Result<R> waitResult(long timeout) throws Exception{
        synchronized (result){
            while(result.isEmpty()){
                result.wait(timeout);
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

    public static Function<Login,Boolean> Login(Login param){
        return new Function<Login, Boolean>(param, Service.Login);
    }
    public static Function<Boolean,Boolean> Ping(){
        return new Function<Boolean,Boolean>(null, Service.Ping);
    }

    public UUID id(){
        return id;
    }
}

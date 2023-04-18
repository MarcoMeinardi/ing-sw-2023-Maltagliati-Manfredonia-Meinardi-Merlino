package network.rpc;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class Call<P extends Serializable> implements Serializable{
    private P params;
    private UUID id;
    private Service service;

    public Call(P params, Service service, UUID id){
        this.params = params;
        this.id = id;
        this.service = service;
    }

    public P getParams(){
        return params;
    }

    public UUID getId(){
        return id;
    }

    public Service getService(){
        return service;
    }
}

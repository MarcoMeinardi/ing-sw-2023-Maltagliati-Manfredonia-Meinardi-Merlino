package network;

import network.parameters.Login;
import network.rpc.client.Function;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public interface RemoteFunctionInterface<P extends Serializable,R extends Serializable> {
    public Optional<Result<R>> checkResult();
    public P getParams();
    public Result<R> waitResult() throws Exception;
    public Result<R> waitResult(long timeout) throws Exception;
    public UUID id();
}

/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.generallycloud.baseio.balance.router;

import com.generallycloud.baseio.balance.HashedBalanceFuture;
import com.generallycloud.baseio.balance.facade.FacadeSocketChannel;
import com.generallycloud.baseio.balance.reverse.ReverseSocketChannel;
import com.generallycloud.baseio.protocol.Future;

public class HashedBalanceRouter extends AbstractBalanceRouter {

    public HashedBalanceRouter(int maxNode) {
        this.virtualNodes = new VirtualNodes<>(maxNode);
    }

    private VirtualNodes<ReverseSocketChannel> virtualNodes;

    @Override
    public void addRouterChannel(ReverseSocketChannel channel) {
        virtualNodes.addMachine(channel);
    }

    @Override
    public void removeRouterChannel(ReverseSocketChannel channel) {
        virtualNodes.removeMachine(channel);
    }

    @Override
    public ReverseSocketChannel getRouterChannel(FacadeSocketChannel channel, Future future) {

        HashedBalanceFuture f = (HashedBalanceFuture) future;

        return virtualNodes.getMachine(f.getHashCode());
    }

    @Override
    public ReverseSocketChannel getRouterChannel(FacadeSocketChannel channel) {
        return null;
    }

}

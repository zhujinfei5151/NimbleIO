package com.gifisan.nio.extend.implementation;

import com.gifisan.nio.component.Session;
import com.gifisan.nio.component.protocol.nio.future.NIOReadFuture;
import com.gifisan.nio.extend.ApplicationContext;
import com.gifisan.nio.extend.RESMessage;
import com.gifisan.nio.extend.service.NIOFutureAcceptorService;

public class SYSTEMRedeployServlet extends NIOFutureAcceptorService {
	
	public static final String SERVICE_NAME = SYSTEMRedeployServlet.class.getSimpleName();

	protected void doAccept(Session session, NIOReadFuture future) throws Exception {
		
		ApplicationContext context = ApplicationContext.getInstance();
		
		if (context.getLoginCenter().isValidate(future.getParameters())) {
			RESMessage message =  context.redeploy()  ? RESMessage.SUCCESS : RESMessage.SYSTEM_ERROR;
			future.write(message.toString());
		} else {
			future.write(RESMessage.UNAUTH.toString());
		}
		session.flush(future);
	}

}

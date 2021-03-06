package test.jms;

import java.io.IOException;

import test.ClientUtil;

import com.gifisan.nio.connector.TCPConnector;
import com.gifisan.nio.extend.FixedSession;
import com.gifisan.nio.extend.SimpleIOEventHandle;
import com.gifisan.nio.extend.plugin.jms.MQException;
import com.gifisan.nio.extend.plugin.jms.Message;
import com.gifisan.nio.extend.plugin.jms.client.MessageConsumer;
import com.gifisan.nio.extend.plugin.jms.client.OnMessage;
import com.gifisan.nio.extend.plugin.jms.client.impl.DefaultMessageConsumer;

public class TestTransaction {

	public static void main(String[] args) throws IOException, MQException {
		
		SimpleIOEventHandle eventHandle = new SimpleIOEventHandle();

		TCPConnector connector = ClientUtil.getTCPConnector(eventHandle);

		FixedSession session = eventHandle.getFixedSession();

		connector.connect();
		
		session.login("admin", "admin100");
		
		MessageConsumer consumer = new DefaultMessageConsumer(session);
		
		rollback(consumer);
		
//		commit(consumer);

		connector.close();

	}
	
	
	static void commit(MessageConsumer consumer) throws MQException{
		long old = System.currentTimeMillis();

		consumer.beginTransaction();
		
		consumer.receive(new OnMessage() {
			
			public void onReceive(Message message) {
				System.out.println(message);
			}
		});

		consumer.commit();
		
		System.out.println("Time:" + (System.currentTimeMillis() - old));
		
	}
	
	static void rollback(MessageConsumer consumer) throws MQException{
		long old = System.currentTimeMillis();

		consumer.beginTransaction();
		
		consumer.receive(new OnMessage() {
			
			public void onReceive(Message message) {
				
				System.out.println(message);
			}
		});

		consumer.rollback();
		
		System.out.println("Time:" + (System.currentTimeMillis() - old));
	}

}

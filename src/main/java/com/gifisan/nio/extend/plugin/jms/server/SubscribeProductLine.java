package com.gifisan.nio.extend.plugin.jms.server;

import java.util.List;

import com.gifisan.nio.extend.plugin.jms.Message;

public class SubscribeProductLine extends AbstractProductLine implements MessageQueue, Runnable {

	public SubscribeProductLine(MQContext context) {
		super(context);
	}

	protected ConsumerQueue createConsumerQueue() {

		return new SUBConsumerQueue();
	}

	//FIXME 完善消息匹配机制
	public void run() {

		for (; running;) {

			Message message = storage.poll(16);

			if (message == null) {
				continue;
			}

			String queueName = message.getQueueName();

			ConsumerQueue consumerQueue = getConsumerQueue(queueName);

			List<Consumer> consumers = consumerQueue.getSnapshot();

			if (consumers.size() == 0) {

				continue;
			}

			for(Consumer consumer:consumers){
				consumer.push(message);
			}

			context.consumerMessage(message);
		}
	}
}

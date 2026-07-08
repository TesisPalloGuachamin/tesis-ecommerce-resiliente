package com.tesis.ecommerce.checkoutservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.listener.default-requeue-rejected:true}")
    private boolean defaultRequeueRejected;

    // Exchange
    public static final String ECOMMERCE_CHECKOUT_EXCHANGE = "ecommerce.checkout.exchange";
    public static final String CHECKOUT_DLX_EXCHANGE = "checkout.dlx";

    // Queues
    public static final String CHECKOUT_REQUESTED_QUEUE = "checkout-service.checkout-requested.q";
    public static final String CHECKOUT_ACCEPTED_QUEUE = "checkout-service.checkout-accepted.q";
    public static final String PAYMENT_PROCESSED_QUEUE = "checkout-service.payment-processed.q";
    public static final String CHECKOUT_COMPLETED_QUEUE = "checkout-service.checkout-completed.q";
    public static final String CHECKOUT_FAILED_QUEUE = "checkout-service.checkout-failed.q";
    public static final String CHECKOUT_DLQ = "checkout-service.dlq";

    // Routing keys
    public static final String CHECKOUT_REQUESTED_RK = "checkout.requested";
    public static final String CHECKOUT_ACCEPTED_RK = "checkout.accepted";
    public static final String PAYMENT_PROCESSED_RK = "payment.processed";
    public static final String CHECKOUT_COMPLETED_RK = "checkout.completed";
    public static final String CHECKOUT_FAILED_RK = "checkout.failed";

    @Bean
    public TopicExchange checkoutExchange() {
        return ExchangeBuilder.topicExchange(ECOMMERCE_CHECKOUT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange checkoutDlxExchange() {
        return ExchangeBuilder.directExchange(CHECKOUT_DLX_EXCHANGE)
                .durable(true)
                .build();
    }

    // Input queue from core-api
    @Bean
    public Queue checkoutRequestedQueue() {
        return QueueBuilder.durable(CHECKOUT_REQUESTED_QUEUE)
                .withArgument("x-dead-letter-exchange", CHECKOUT_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "checkout.requested.dlq")
                .withArgument("x-message-ttl", 86400000)
                .build();
    }

    @Bean
    public Binding checkoutRequestedBinding(Queue checkoutRequestedQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(checkoutRequestedQueue)
                .to(checkoutExchange)
                .with(CHECKOUT_REQUESTED_RK);
    }

    // Output queues
    @Bean
    public Queue checkoutAcceptedQueue() {
        return QueueBuilder.durable(CHECKOUT_ACCEPTED_QUEUE).build();
    }

    @Bean
    public Binding checkoutAcceptedBinding(Queue checkoutAcceptedQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(checkoutAcceptedQueue)
                .to(checkoutExchange)
                .with(CHECKOUT_ACCEPTED_RK);
    }

    @Bean
    public Queue paymentProcessedQueue() {
        return QueueBuilder.durable(PAYMENT_PROCESSED_QUEUE).build();
    }

    @Bean
    public Binding paymentProcessedBinding(Queue paymentProcessedQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(paymentProcessedQueue)
                .to(checkoutExchange)
                .with(PAYMENT_PROCESSED_RK);
    }

    @Bean
    public Queue checkoutCompletedQueue() {
        return QueueBuilder.durable(CHECKOUT_COMPLETED_QUEUE).build();
    }

    @Bean
    public Binding checkoutCompletedBinding(Queue checkoutCompletedQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(checkoutCompletedQueue)
                .to(checkoutExchange)
                .with(CHECKOUT_COMPLETED_RK);
    }

    @Bean
    public Queue checkoutFailedQueue() {
        return QueueBuilder.durable(CHECKOUT_FAILED_QUEUE).build();
    }

    @Bean
    public Binding checkoutFailedBinding(Queue checkoutFailedQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(checkoutFailedQueue)
                .to(checkoutExchange)
                .with(CHECKOUT_FAILED_RK);
    }

    // DLQ
    @Bean
    public Queue checkoutDlq() {
        return QueueBuilder.durable(CHECKOUT_DLQ).build();
    }

    @Bean
    public Binding checkoutDlqBinding(Queue checkoutDlq, DirectExchange checkoutDlxExchange) {
        return BindingBuilder.bind(checkoutDlq)
                .to(checkoutDlxExchange)
                .with("checkout.requested.dlq");
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setDefaultRequeueRejected(defaultRequeueRejected);
        return factory;
    }

}

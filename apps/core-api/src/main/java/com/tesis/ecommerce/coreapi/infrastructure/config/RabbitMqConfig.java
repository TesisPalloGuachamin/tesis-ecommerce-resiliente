package com.tesis.ecommerce.coreapi.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMqConfig {

    // Exchange
    public static final String ECOMMERCE_CHECKOUT_EXCHANGE = "ecommerce.checkout.exchange";
    public static final String CORE_API_DLX_EXCHANGE = "core-api.dlx";

    // Queues
    public static final String CHECKOUT_COMPLETED_QUEUE = "core-api.checkout-result.q";
    public static final String CHECKOUT_FAILED_QUEUE = "core-api.checkout-result.q";
    public static final String CORE_API_DLQ = "core-api.dlq";

    // Routing keys
    public static final String CHECKOUT_COMPLETED_RK = "checkout.completed";
    public static final String CHECKOUT_FAILED_RK = "checkout.failed";

    @Bean
    public TopicExchange checkoutExchange() {
        return ExchangeBuilder.topicExchange(ECOMMERCE_CHECKOUT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange coreApiDlxExchange() {
        return ExchangeBuilder.directExchange(CORE_API_DLX_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue checkoutCompletedQueue() {
        return QueueBuilder.durable(CHECKOUT_COMPLETED_QUEUE)
                .withArgument("x-dead-letter-exchange", CORE_API_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "checkout.result.dlq")
                .withArgument("x-message-ttl", 86400000)
                .build();
    }

    @Bean
    public Binding checkoutCompletedBinding(Queue checkoutCompletedQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(checkoutCompletedQueue)
                .to(checkoutExchange)
                .with(CHECKOUT_COMPLETED_RK);
    }

    @Bean
    public Binding checkoutFailedBinding(Queue checkoutCompletedQueue, TopicExchange checkoutExchange) {
        return BindingBuilder.bind(checkoutCompletedQueue)
                .to(checkoutExchange)
                .with(CHECKOUT_FAILED_RK);
    }

    @Bean
    public Queue coreApiDlq() {
        return QueueBuilder.durable(CORE_API_DLQ).build();
    }

    @Bean
    public Binding coreApiDlqBinding(Queue coreApiDlq, DirectExchange coreApiDlxExchange) {
        return BindingBuilder.bind(coreApiDlq)
                .to(coreApiDlxExchange)
                .with("checkout.result.dlq");
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

}


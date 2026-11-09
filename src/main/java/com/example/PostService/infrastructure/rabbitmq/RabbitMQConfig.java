package com.example.PostService.infrastructure.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import com.example.PostService.api.model.TextProcessingOutput;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public final String QUEUE = "text-processor-service.post-processing.v1.q";

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }
    //criar a exchange
    @Bean
    public FanoutExchange fanoutExchange(){
        return ExchangeBuilder.fanoutExchange("text-processor-service.post-processing.v1.e").build();
    }
    //criar a queue
    @Bean
    public Queue queue(){
        return QueueBuilder.durable(QUEUE).build();
    }
    //criar o binding
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(fanoutExchange());
    }
    @Bean
    public MessageConverter messageConverter(JsonMapper jsonMapper){
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(jsonMapper);
        DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();
        typeMapper.setIdClassMapping(Map.of(
            "com.conti.TextProcessorService.api.model.TextProcessingOutput", TextProcessingOutput.class
        ));
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

}

package com.alexbych.messageimportapp;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class Listener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(Listener.class);

    @Autowired
    public AmqpTemplate amqpTemplate;

    private List<Object> books;

    @Value("${format}")
    String format;

    @RabbitListener(bindings =
    @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.queue}", durable = "true"),
            exchange = @Exchange(value = "${spring.rabbitmq.exchange}", type = "classic")
    )
    )
    @Override
    public void onMessage(Message message) {
            writeToOwnFile(new String(message.getBody(), StandardCharsets.UTF_8));
        String path = System.getProperty("user.dir").replaceAll("\\\\", "/");
        log.info(path);
    }

    public void write(String string, String filename){
        String path = System.getProperty("user.dir").replaceAll("\\\\", "/");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/" + filename))) {
            writer.write(string);
        } catch (IOException e) {
            log.error(String.valueOf(e));
        }
    }

    public void writeToOwnFile(String messRegister){
        if (messRegister.startsWith("books")){
            String mess = messRegister.replaceFirst("books", "");
            if(format.equals("input/json")) write(mess, "input/json/books.json");
            else if(format.equals("input/xml")) {
                JSONObject jsonObject = new JSONObject(mess);
                write(XML.toString(jsonObject), "input/xml/books.xml");
            }
            log.info("books writing");
        } else if (messRegister.startsWith("orders")){
            String mess = messRegister.replaceFirst("orders", "");
            if(format.equals("input/json")) write(mess, "input/json/orders.json");
            else if(format.equals("input/xml")) {
                JSONObject jsonObject = new JSONObject(mess);
                write(XML.toString(jsonObject), "input/xml/orders.xml");
            }
            log.info("orders writing");
        } else if (messRegister.startsWith("clients")){
            String mess = messRegister.replaceFirst("clients", "");
            if(format.equals("input/json")) write(mess, "input/json/clients.json");
            else if(format.equals("input/xml")) {
                JSONObject jsonObject = new JSONObject(mess);
                write(XML.toString(jsonObject), "input/xml/clients.xml");
            }
            log.info("clients writing");
        } else if (messRegister.startsWith("requests")){
            String mess = messRegister.replaceFirst("requests", "");
            if(format.equals("input/json")) write(mess, "input/json/requests.json");
            else if(format.equals("input/xml")) {
                JSONObject jsonObject = new JSONObject(mess);
                write(XML.toString(jsonObject), "input/xml/requests.xml");
            }
            log.info("requests writing");
        } else {
            log.info("writing nothing");
        }
    }
}

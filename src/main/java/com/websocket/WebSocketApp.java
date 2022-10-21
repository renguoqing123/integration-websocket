package com.websocket;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class WebSocketApp 
{
    public static void main( String[] args )
    {
    	new SpringApplicationBuilder(WebSocketApp.class).web(WebApplicationType.SERVLET).run(args);
    }
}

/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import java.util.HashMap;


@SpringBootApplication
@LineMessageHandler
public class EchoApplication {
	boolean modeBoss;
	HashMap<String, String> hm = new HashMap<String, String>();

    public static void main(String[] args) {
        SpringApplication.run(EchoApplication.class, args);
    }

    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
        System.out.println("event: " + event);
        String originalMessageText = event.getMessage().getText();
		String replyText = "";
        if(originalMessageText.equalsIgnoreCase("boss")){
			modeBoss=true;
			replyText="random text s";
        }
		else if(originalMessageText.equalsIgnoreCase("noboss")){
			modeBoss=false;
			replyText="OK";
		}
		else {
			if(modeBoss==false){
				String inputs[]=originalMessageText.split(" ");
				if(inputs[0].equalsIgnoreCase("save")){
						hm.put(inputs[1],inputs[2]);
						replyText="OK";
				}
				else if(inputs[0].equalsIgnoreCase("load")){
					if(hm.get(inputs[1])!=null){
						replyText=hm.get(inputs[1]);
					}
				}
			}
		}
        return new TextMessage(replyText);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
}

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
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootApplication
@LineMessageHandler
public class EchoApplication {
	static String url = "jdbc:postgresql://ec2-23-21-201-12.compute-1.amazonaws.com:5432/d30c1p51b5n66d?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
    static String username = "dgeubljqajtwts";
    static String password = "e0a0cdda3f83ef1c2c4af51786f2c636f4253640dac0681e786ad8c2a3031c75";
	
	boolean modeBoss;
	HashMap<String, String> hm = new HashMap<String, String>();

    public static void main(String[] args){
        SpringApplication.run(EchoApplication.class, args);
    }

    @EventMapping
    public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event){
        System.out.println("event: " + event);
        String originalMessageText = event.getMessage().getText();
		String replyText = "";
        if(originalMessageText.equalsIgnoreCase("boss")){
			modeBoss=true;
			replyText= randomText();
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
						saveData(inputs[1],inputs[2]);
						replyText="OK";
				}
				else if(inputs[0].equalsIgnoreCase("load")){
					if(hm.get(inputs[1])!=null){
						
						//replyText=hm.get(inputs[1]);
						replyText = getData(inputs[1]);
					}
				}
			}
		}
		Connection conn = null;
		try {
            conn = DriverManager.getConnection(url, username, password);
			replyText+=" koneksi berhasil";
			conn.close();
        } catch (SQLException e) {
            replyText+=" koneksi gagal";
        }
		
		//buat dapetin ID user 
	    String userId = event.getSource().getUserId();
		replyText+=" ";
		replyText+=userId;
        return new TextMessage(replyText);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("event: " + event);
    }
	public String randomText(){
		return "What is Lorem Ipsum?Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.Why do we use it?It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).";
	}
	 public static void saveChat(String key, String value) {
        Connection con = getConnection();
        String query = "INSERT INTO DATA(KEY,VALUE) VALUES(?, ?) ";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, key);
            pst.setString(2, value);
            pst.executeUpdate();
            //System.out.println("berhasil");
        } catch (SQLException ex) {

            System.out.println("gagal");

        }

    }

    public static String getData(String key) {
        Connection con = getConnection();
        String value = "";
        String query = "select valuetext from DATA where KEY= '" + key + "' order by id desc limit 1;";
        try {
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                    value = rs.getString(1);
                  
                }
            }catch (SQLException ex) {

            System.out.println("gagal");

        }
        return value;
    }

	private static Connection getConnection() throws URISyntaxException, SQLException {
		URI dbUri = new URI(System.getenv(" postgres://dgeubljqajtwts:e0a0cdda3f83ef1c2c4af51786f2c636f4253640dac0681e786ad8c2a3031c75@ec2-23-21-201-12.compute-1.amazonaws.com:5432/d30c1p51b5n66d"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

		return DriverManager.getConnection(dbUrl, username, password);
	}
	//buat save data
//	public void saveData(String key,String value, String userID){
//		Connection conn = null;
//		try {
//          conn = DriverManager.getConnection(url, username, password);
//			String query = "INSERT INTO [NAMA TABEl] VALUES(?,?,?)";
//			PreparedStatement pst = con.prepareStatement(query)) {
            
 //         pst.setString(1, key);
 //         pst.setString(2, value);
//			pst.setString(3, userID);
//          pst.executeUpdate();
			
//			conn.close();
//        } catch (SQLException e) {
            
//        }
	//}
}

package com.casmack.model;

import java.io.Serializable;

import org.jivesoftware.smack.packet.Message;

public class ChatMessage implements Serializable {
		
		private static final long serialVersionUID = -3359911823393072186L;
		
		private String from;
		private String body; 
		
		public ChatMessage(Message message) {
			from = message.getFrom();
			body = message.getBody();
		}
		
		public ChatMessage() {
		}
		
		public String getFrom() {
			return from;
		}
		public void setFrom(String from) {
			this.from = from;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		
	}
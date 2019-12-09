package io.github.nortthon.chatbot.controllers;

import io.github.nortthon.chatbot.domains.ChatMessage;
import io.github.nortthon.chatbot.domains.ChatMessage.MessageType;
import io.github.nortthon.chatbot.domains.DialogIntentOutput;
import io.github.nortthon.chatbot.domains.Intent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

	private final RabbitTemplate rabbitTemplate;

	private final SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/chat.register")
	@SendTo("/topic/public")
	public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		return chatMessage;
	}

	@MessageMapping("/chat.send")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		final Intent intent = Intent.builder()
				.channel("chatbot")
				.languageCode("pt-BR")
				.text(chatMessage.getContent())
				.sessionId("test")
				.build();

		rabbitTemplate.convertAndSend("dialog.intent.input", "dialog.intent.input.rk", intent);
		return chatMessage;
	}

	@RabbitListener(queues = "dialog.intent.output.chatbot.queue")
	public void listen(@Payload DialogIntentOutput dialogIntentOutput) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setType(MessageType.CHAT);
		chatMessage.setSender("Chatbot");
		chatMessage.setContent(dialogIntentOutput.getMessageAction().isCaptured() ?
				dialogIntentOutput.getMessageAction().getMessage() :
				"Desculpa mas ainda não sei te responder a action: " + dialogIntentOutput.getAction());
		simpMessagingTemplate.convertAndSend("/topic/public", chatMessage);
	}
}

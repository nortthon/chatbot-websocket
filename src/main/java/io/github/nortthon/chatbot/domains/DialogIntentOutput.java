package io.github.nortthon.chatbot.domains;

import java.util.Map;
import lombok.Data;

@Data
public class DialogIntentOutput {

  private String sessionId;
  private String channel;
  private String action;
  private Float confidence;
  private MessageAction messageAction;
  private Map<String, String> parameters;
  private String sourceMessage;
}

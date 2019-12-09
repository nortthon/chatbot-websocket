package io.github.nortthon.chatbot.domains;

import lombok.Data;

@Data
public class MessageAction {

  private boolean captured;
  private String message;
}

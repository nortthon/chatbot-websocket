package io.github.nortthon.chatbot.domains;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Intent {

  private final String sessionId;
  private final String channel;
  private final String text;
  private final String languageCode;
}

package com.github.z3on.telegram.bots;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.z3on.telegram.util.LambdaUtils.not;
import static java.util.stream.Collectors.toList;

public abstract class AbstractPollingBot extends TelegramLongPollingBot {

  private static final String[] CHARS_TO_ESCAPE = {"*", "_", "`", "["};
  private static final String ESCAPING_STRING = "\\";

  private Map<String, ActionHandler> actionsMapping = new HashMap<>();

  protected void registerAction(String action, ActionHandler actionHandler) {
    actionsMapping.put(action, actionHandler);
  }

  protected void handleNonTextMessage(Message message) {
    // skip it by default
    // override to add business logic
  }

  protected void handleNonActionMessage(Message message) {
    // skip it by default
    // override to add business logic
  }

  @Override
  public void onUpdateReceived(Update update) {
    Optional.ofNullable(update)
        .map(Update::getMessage)
        .ifPresent(this::handleMessage);
  }

  private void handleMessage(Message message) {
    if (message.hasText()) {
      String messageText = message.getText();
      String[] textTokens = messageText.trim().split(" ");
      String action = textTokens[0];
      if (actionsMapping.containsKey(action)) {
        List<String> arguments = Stream.of(textTokens)
            .skip(1)
            .filter(not(String::isEmpty))
            .map(this::escapeMarkdown)
            .collect(toList());
        actionsMapping.get(action).accept(message, arguments);
      } else {
        handleNonActionMessage(message);
      }
    } else {
      handleNonTextMessage(message);
    }
  }

  private String escapeMarkdown(String text) {
    return Stream.of(CHARS_TO_ESCAPE)
        .reduce(text, (source, unescapedChar) -> source.replace(unescapedChar, ESCAPING_STRING + unescapedChar));
  }
}

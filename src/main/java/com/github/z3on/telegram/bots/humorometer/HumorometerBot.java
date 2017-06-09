package com.github.z3on.telegram.bots.humorometer;

import com.github.z3on.telegram.bots.AbstractPollingBot;
import com.github.z3on.telegram.config.BotName;
import com.github.z3on.telegram.config.BotToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;

public class HumorometerBot extends AbstractPollingBot {

  private static final String PARSE_MODE = "Markdown";
  private static final Logger LOGGER = LoggerFactory.getLogger(HumorometerBot.class);

  private HumorStatistics humorStatistics = new HumorStatistics();

  @BotName("humorometer")
  private String botName;

  @BotToken("humorometer")
  private String botToken;

  public HumorometerBot() {
    registerAction("/up", this::upvote);
    registerAction("/down", this::downvote);
    registerAction("/stats", this::getStats);
    registerAction("/reset", this::resetStats);
  }

  @Override
  public String getBotUsername() {
    return botName;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  private void upvote(Message message, List<String> args) {
    Long chatId = message.getChatId();
    if (!args.isEmpty()) {
      args.stream().distinct().forEach(name -> humorStatistics.increment(chatId, name));
      sendMessage(chatId, "_Upvote registered_");
    } else {
      sendMessage(chatId, "You should specify name (or multiple) for upvote:\n`/up <name>`\nor\n`/up <name> <anotherName> ...`");
    }
  }

  private void downvote(Message message, List<String> args) {
    Long chatId = message.getChatId();
    if (!args.isEmpty()) {
      args.stream().distinct().forEach(name -> humorStatistics.decrement(chatId, name));
      sendMessage(chatId, "_Downvote registered_");
    } else {
      sendMessage(chatId, "You should specify name (or multiple) for downvote:\n`/down <name>`\nor\n`/down <name> <anotherName> ...`");
    }
  }

  private void getStats(Message message, List<String> args) {
    Long chatId = message.getChatId();
    String stats = humorStatistics.getStats(message.getChatId(), args);
    sendMessage(chatId, stats);
  }

  private void resetStats(Message message, List<String> args) {
    Long chatId = message.getChatId();
    humorStatistics.resetStats(message.getChatId(), args);
    sendMessage(chatId, "_Stats were reset_");
  }

  private void sendMessage(Long chatId, String text) {
    try {
      sendMessage(new SendMessage().setChatId(chatId).setText(text).setParseMode(PARSE_MODE));
    } catch (TelegramApiException ex) {
      LOGGER.error("Unable to send message to chat " + chatId, ex);
    }
  }
}

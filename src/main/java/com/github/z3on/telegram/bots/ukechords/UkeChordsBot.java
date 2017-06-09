package com.github.z3on.telegram.bots.ukechords;

import com.github.z3on.telegram.config.BotName;
import com.github.z3on.telegram.config.BotToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.z3on.telegram.util.LambdaUtils.not;

public class UkeChordsBot extends TelegramLongPollingBot {

  private static final Logger LOGGER = LoggerFactory.getLogger(UkeChordsBot.class);

  @BotName("uke-chords")
  private String botName;

  @BotToken("uke-chords")
  private String botToken;

  @Override
  public String getBotUsername() {
    return botName;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  @Override
  public void onUpdateReceived(Update update) {
    Optional.ofNullable(update.getMessage())
        .filter(Message::hasText)
        .ifPresent(this::handleTextRequest);
  }

  private void handleTextRequest(Message message) {
    String text = message.getText().trim();
    Stream.of(text.split(" "))
        .filter(not(String::isEmpty))
        .forEach(chord -> sendChordPicture(message.getChatId(), chord));
  }

  private void sendChordPicture(Long chatId, String chordName) {
    try {
      URL resourceUrl = getClass().getResource(chordName.toLowerCase() + ".jpg");
      if (resourceUrl != null) {
        File chordFile = new File(resourceUrl.toURI());
        sendPhoto(new SendPhoto()
            .setChatId(chatId)
            .setCaption(chordName)
            .setNewPhoto(chordFile));
      } else {
        sendMessage(new SendMessage()
            .setChatId(chatId)
            .setText("No chord found with name " + chordName));
      }
    } catch (TelegramApiException | URISyntaxException ex) {
      LOGGER.error("Telegram API error", ex);
    }
  }
}

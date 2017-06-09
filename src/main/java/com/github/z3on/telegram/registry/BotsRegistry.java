package com.github.z3on.telegram.registry;

import com.github.z3on.telegram.config.BotConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.LongPollingBot;

public class BotsRegistry {

  private static final Logger LOGGER = LoggerFactory.getLogger(BotsRegistry.class);
  private static final BotsRegistry BOTS_REGISTRY = new BotsRegistry();

  private final TelegramBotsApi telegramApi;

  private BotsRegistry() {
    ApiContextInitializer.init();
    telegramApi = new TelegramBotsApi();
  }

  public static BotsRegistry getInstance() {
    return BOTS_REGISTRY;
  }

  public BotsRegistry registerBot(Class<? extends LongPollingBot> botClass) {
    try {
      LongPollingBot bot = instantiateBot(botClass);
      telegramApi.registerBot(bot);
    } catch (ReflectiveOperationException ex) {
      LOGGER.error("Can't instantiate and register {} bot. Bot should have public default constructor.", botClass.getSimpleName());
    } catch (TelegramApiException ex) {
      LOGGER.error("Can't register bot " + botClass.getSimpleName(), ex);
    }
    return this;
  }

  private LongPollingBot instantiateBot(Class<? extends LongPollingBot> botClass) throws ReflectiveOperationException {
    LongPollingBot bot = botClass.getConstructor().newInstance();
    BotConfigurer.initializeBotConfiguration(bot);
    return bot;
  }
}

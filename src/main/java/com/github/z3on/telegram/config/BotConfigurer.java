package com.github.z3on.telegram.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.stream.Stream;

public class BotConfigurer {

  private static final Logger LOGGER = LoggerFactory.getLogger(BotConfigurer.class);

  private static final String BOT_NAME_PROPERTY_PATTERN = "bot.%s.name";
  private static final String BOT_TOKEN_PROPERTY_PATTERN = "bot.%s.token";

  public static void initializeBotConfiguration(Object bot) {
    initializeBotName(bot);
    initializeBotToken(bot);
  }

  private static void initializeBotName(Object bot) {
    getStreamOfAnnotatedFields(bot, BotName.class)
        .forEach(field -> setFieldValue(bot, field, getBotNameProperty(field)));
  }

  private static void initializeBotToken(Object bot) {
    getStreamOfAnnotatedFields(bot, BotToken.class)
        .forEach(field -> setFieldValue(bot, field, getBotTokenProperty(field)));
  }

  private static Stream<Field> getStreamOfAnnotatedFields(Object bot, Class<? extends Annotation> annotationClass) {
    return Stream.of(bot.getClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(annotationClass));
  }

  private static String getBotNameProperty(Field field) {
    return PropertiesResolver.getProperty(String.format(BOT_NAME_PROPERTY_PATTERN, field.getAnnotation(BotName.class).value()));
  }

  private static String getBotTokenProperty(Field field) {
    return PropertiesResolver.getProperty(String.format(BOT_TOKEN_PROPERTY_PATTERN, field.getAnnotation(BotToken.class).value()));
  }

  private static void setFieldValue(Object instance, Field field, Object value) {
    field.setAccessible(true);
    try {
      field.set(instance, value);
    } catch (IllegalAccessException e) {
      LOGGER.error("Can't set property field {} for {} bot", field.getName(), instance.getClass().getSimpleName());
    }
  }
}

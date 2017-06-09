package com.github.z3on.telegram.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public final class PropertiesResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesResolver.class);
  private static final String PROPERTIES_FILENAME = "/bot.properties";
  private static final Properties appProperties = loadAppProperties();

  private PropertiesResolver() {
    loadAppProperties();
  }

  private static Properties loadAppProperties() {
    Properties properties = new Properties();
    InputStream propertiesInputStream = PropertiesResolver.class.getResourceAsStream(PROPERTIES_FILENAME);
    try {
      properties.load(propertiesInputStream);
    } catch (IOException ex) {
      LOGGER.error("Failed to load application properties from file " + PROPERTIES_FILENAME, ex);
    }
    return properties;
  }

  public static String getProperty(String name) {
    return Optional.ofNullable(appProperties.getProperty(name))
        .orElseGet(() -> System.getProperty(name));
  }
}

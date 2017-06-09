package com.github.z3on.telegram;

import com.github.z3on.telegram.bots.humorometer.HumorometerBot;
import com.github.z3on.telegram.registry.BotsRegistry;
import com.github.z3on.telegram.bots.ukechords.UkeChordsBot;

public class Runner {

  public static void main(String[] args) throws Exception {
    BotsRegistry.getInstance()
        .registerBot(UkeChordsBot.class)
        .registerBot(HumorometerBot.class);
  }
}

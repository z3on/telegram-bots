package com.github.z3on.telegram.bots;

import org.telegram.telegrambots.api.objects.Message;

import java.util.List;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface ActionHandler extends BiConsumer<Message, List<String>> {
}

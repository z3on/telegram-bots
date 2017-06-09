package com.github.z3on.telegram.bots.humorometer;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.github.z3on.telegram.util.LambdaUtils.not;

public class HumorStatistics {

  private static final String HUMOR_STATS_HEADER = "*Humor stats:*\n";
  private static final String NO_STATS_MESSAGE = "No stats found";

  private Map<Long, Map<String, AtomicLong>> stats;

  public HumorStatistics() {
    stats = new HashMap<>();
  }

  public Long increment(Long chatId, String name) {
    return getStatsAtomicLong(chatId, name).incrementAndGet();
  }

  public Long decrement(Long chatId, String name) {
    return getStatsAtomicLong(chatId, name).decrementAndGet();
  }

  public String getStats(Long chatId, List<String> names) {
    return Optional.ofNullable(stats.get(chatId))
        .map(chatStats -> generateStatsReport(chatStats, names))
        .filter(not(String::isEmpty))
        .map(HUMOR_STATS_HEADER::concat)
        .orElse(NO_STATS_MESSAGE);
  }

  public void resetStats(Long chatId, List<String> names) {
    if (names.isEmpty()) {
      stats.remove(chatId);
    } else {
      Optional.ofNullable(stats.get(chatId))
          .ifPresent(chatStats -> names.forEach(chatStats::remove));
    }
  }

  private AtomicLong getStatsAtomicLong(Long chatId, String name) {
    stats.putIfAbsent(chatId, new HashMap<>());
    Map<String, AtomicLong> chatStats = stats.get(chatId);
    chatStats.putIfAbsent(name, new AtomicLong());
    return chatStats.get(name);
  }

  private String generateStatsReport(Map<String, AtomicLong> chatStats, List<String> reportEntries) {
    return chatStats.entrySet()
        .stream()
        .filter(entry -> reportEntries.isEmpty() || reportEntries.contains(entry.getKey()))
        .sorted(Comparator.comparingLong(entry -> entry.getValue().get()))
        .map(entry -> entry.getKey() + ": " + entry.getValue().get())
        .collect(Collectors.joining("\n"));
  }
}

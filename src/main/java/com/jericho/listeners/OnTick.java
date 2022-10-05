package com.jericho.listeners;

import com.jericho.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.jericho.Main.settings;
import static com.jericho.Utilities.FileIO.*;
import static com.jericho.Utilities.Servers.getServerPaths;

public class OnTick {

    public static final ArrayList<String> serverPaths = getServerPaths();
    private static final int time = settings.getInt("update_check_interval");
    private static final String channel_id = settings.getString("channel_id");
    public static JSONObject stats;
    static ShardManager shardManager = Main.shardManager;

    static {
        try {
            stats = loadJSONObject("stats.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Tick() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                for (String path : serverPaths) {
                    try {

                        boolean write_stats = false;
                        JSONObject server = loadJSONObjectFromDir(path);
                        for (String username : server.keySet()) {

                            int credits = server.getJSONObject(username).getInt("credits");
                            int grade = server.getJSONObject(username).getInt("grade");
                            int prestige = server.getJSONObject(username).getInt("prestige");
                            String name = server.getJSONObject(username).getString("name");
                            String rank = server.getJSONObject(username).getString("rank");
                            String password = server.getJSONObject(username).getString("password");

                            boolean updated = false;
                            if (stats.has(username)) {
                                if (stats.getJSONObject(username).getInt("credits") != credits) {
                                    stats.getJSONObject(username).put("credits", credits);
                                    updated = true;
                                }
                                if (stats.getJSONObject(username).getInt("grade") != grade) {
                                    stats.getJSONObject(username).put("grade", grade);
                                    updated = true;
                                }
                                if (stats.getJSONObject(username).getInt("prestige") != prestige) {
                                    stats.getJSONObject(username).put("prestige", prestige);
                                    updated = true;
                                }
                                if (!stats.getJSONObject(username).getString("name").equals(name)) {
                                    stats.getJSONObject(username).put("name", name);
                                    updated = true;
                                }
                                if (!stats.getJSONObject(username).getString("rank").equals(rank)) {
                                    stats.getJSONObject(username).put("rank", rank);
                                    updated = true;
                                }
                                if (!stats.getJSONObject(username).getString("password").equals(password)) {
                                    stats.getJSONObject(username).put("password", password);
                                    updated = true;
                                }
                            } else {
                                updated = true;
                                stats.put(username, new JSONObject());
                                stats.getJSONObject(username).put("credits", credits);
                                stats.getJSONObject(username).put("grade", grade);
                                stats.getJSONObject(username).put("prestige", prestige);
                                stats.getJSONObject(username).put("name", name);
                                stats.getJSONObject(username).put("rank", rank);
                                stats.getJSONObject(username).put("password", password);
                            }

                            if (updated && stats.getJSONObject(username).has("message_id")) {

                                String message_id = stats.getJSONObject(username).getString("message_id");

                                TextChannel channel = shardManager.getTextChannelById(channel_id);
                                Message message = channel.retrieveMessageById(message_id).complete();

                                String discord_id = stats.getJSONObject(username).getString("discord_id");
                                String mention = shardManager.getUserById(discord_id).getAsMention();

                                EmbedBuilder embed = new EmbedBuilder();
                                embed.setTitle("**RANK SYSTEM**");
                                embed.setDescription(mention);
                                embed.addField("Rank", rank, true);
                                embed.addField("Grade", String.valueOf(grade), true);
                                embed.addField("Credits", String.valueOf(credits), true);
                                embed.addField("Prestige", String.valueOf(prestige), true);
                                embed.setFooter("Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                                message.editMessageEmbeds(embed.build()).queue();
                            }
                            write_stats = updated;
                        }
                        if (write_stats) {
                            writeJSONObject(stats, "stats.json");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 1, time * 1000L);
    }
}

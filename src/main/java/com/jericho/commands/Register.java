// Copyright (c) 2022, Jericho Crosby <jericho.crosby227@gmail.com>

package com.jericho.commands;

import com.jericho.listeners.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.jericho.Main.settings;
import static com.jericho.listeners.OnTick.stats;

public class Register implements CommandInterface {

    public static String channel_id = settings.getString("channel_id");

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "Register your ranked account to Discord.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> data = new ArrayList<>();
        data.add(new OptionData(OptionType.STRING, "username", "The username for your ranked account").setRequired(true));
        data.add(new OptionData(OptionType.STRING, "password", "The password for your ranked account.").setRequired(true));
        return data;
    }

    public void sendEphemeralMsg(SlashCommandInteractionEvent e, String str) {
        e.reply(str).setEphemeral(true).queue();
    }

    @Override
    public void execute(SlashCommandInteractionEvent e) {

        String username = e.getOption("username").getAsString();
        String password = e.getOption("password").getAsString();

        if (stats.has(username)) {
            if (stats.getJSONObject(username).getString("password").equals(password)) {

                stats.getJSONObject(username).put("discord_id", e.getUser().getId());
                sendEphemeralMsg(e, "You have been registered!");

                String rank = stats.getJSONObject(username).getString("rank");
                int grade = stats.getJSONObject(username).getInt("grade");
                int credits = stats.getJSONObject(username).getInt("credits");
                int prestige = stats.getJSONObject(username).getInt("prestige");

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("**RANK SYSTEM**");
                embed.setDescription(e.getUser().getAsMention());
                embed.addField("Rank", rank, true);
                embed.addField("Grade", String.valueOf(grade), true);
                embed.addField("Credits", String.valueOf(credits), true);
                embed.addField("Prestige", String.valueOf(prestige), true);

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formatDateTime = now.format(formatter);
                embed.setFooter("Last updated: " + formatDateTime);

                e.getJDA().getTextChannelById(channel_id).sendMessageEmbeds(embed.build()).queue();

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        e.getJDA().getTextChannelById(channel_id).retrieveMessageById(e.getJDA().getTextChannelById(channel_id).getLatestMessageId()).queue(message -> {
                            stats.getJSONObject(username).put("message_id", message.getId());
                            System.out.println("Message ID: " + message.getId());
                        });
                        this.cancel();
                    }
                }, 2000);

            } else {
                sendEphemeralMsg(e, "Incorrect password!");
            }
        } else {
            sendEphemeralMsg(e, "Username not found!");
        }
    }
}

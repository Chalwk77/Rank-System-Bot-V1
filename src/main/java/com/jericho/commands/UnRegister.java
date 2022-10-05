// Copyright (c) 2022, Jericho Crosby <jericho.crosby227@gmail.com>

package com.jericho.commands;

import com.jericho.listeners.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.jericho.Main.settings;
import static com.jericho.Main.shardManager;
import static com.jericho.Utilities.FileIO.writeJSONObject;
import static com.jericho.listeners.OnTick.stats;

public class UnRegister implements CommandInterface {

    public static String channel_id = settings.getString("channel_id");

    @Override
    public String getName() {
        return "unregister";
    }

    @Override
    public String getDescription() {
        return "Un register your ranked account from Discord.";
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

                String message_id = stats.getJSONObject(username).getString("message_id");
                stats.remove(username);

                TextChannel channel = shardManager.getTextChannelById(channel_id);
                Message message = channel.retrieveMessageById(message_id).complete();
                message.delete().queue();

                try {
                    writeJSONObject(stats, "stats.json");
                    sendEphemeralMsg(e, "You have been unregistered!");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                sendEphemeralMsg(e, "Incorrect password!");
            }
        } else {
            sendEphemeralMsg(e, "Username not found!");
        }
    }
}

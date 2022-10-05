// Copyright (c) 2022, Jericho Crosby <jericho.crosby227@gmail.com>

package com.jericho.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.jericho.Main.cprint;

public class EventListeners extends ListenerAdapter {

    @Override
    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        cprint("++++++++++++++++++++++++++++++++++++++++++++++++");
        cprint("Guild ready: " + event.getGuild().getName());
        cprint("Bot name: " + event.getJDA().getSelfUser().getName());
        cprint("++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        User author = event.getAuthor();
        String name = author.getName();
        String message = event.getMessage().getContentRaw();

        // Ignore messages from bots
        if (!author.isBot()) {

        }
    }

}
// Copyright (c) 2022, Jericho Crosby <jericho.crosby227@gmail.com>

// todo: embed messages must tag the user to whom the account belongs:

package com.jericho;

import com.jericho.commands.Register;
import com.jericho.commands.UnRegister;
import com.jericho.listeners.CommandManager;
import com.jericho.listeners.EventListeners;
import com.jericho.listeners.OnTick;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.IOException;

import static com.jericho.Utilities.FileIO.loadJSONObject;

public class Main {

    public static JSONObject auth;

    public static JSONObject settings;
    public static ShardManager shardManager;

    // Retrieves the settings from the settings.json file.
    static {
        try {
            auth = loadJSONObject("auth.token");
            settings = loadJSONObject("settings.json");
        } catch (IOException e) {
            cprint("Error loading settings.json file.");
            cprint("Please make sure the file exists and is in the correct location -> Parent Directory/auth.token");
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads environment variables and builds the bot shard manager:
     *
     * @throws LoginException if the bot token is invalid.
     */
    public Main() throws LoginException {

        String token = getToken();
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Halo"));
        builder.enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.MESSAGE_CONTENT
        );

        shardManager = builder.build();
        shardManager.addEventListener(new EventListeners());

        CommandManager manager = new CommandManager();
        manager.add(new Register());
        manager.add(new UnRegister());

        shardManager.addEventListener(manager);

        OnTick.Tick();
    }

    /**
     * Convenience method for printing to console:
     *
     * @param str The message to print to console.
     */
    public static void cprint(String str) {
        System.out.println(str);
    }

    /**
     * Returns the bot token:
     */
    public static String getToken() {
        return String.valueOf(auth.getString("token"));
    }

    /**
     * Main static method:
     *
     * @param args The arguments passed to the program.
     */
    public static void main(String[] args) {
        try {
            new Main();
        } catch (LoginException e) {
            cprint("ERROR: Provided bot token is invalid");
        }
    }

    /**
     * Retrieves the shard manager:
     *
     * @return The shardManager instance for the bot.
     */
    public ShardManager getShardManager() {
        return shardManager;
    }
}

package com.jericho.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.jericho.Main.settings;

public class Servers {

    public static ArrayList<String> getServerPaths() {

        ArrayList<String> serverPaths = new ArrayList<>();
        JSONObject servers = settings.getJSONObject("servers");

        for (String server : servers.keySet()) {
            serverPaths.add(servers.getString(server));
        }

        return serverPaths;
    }
}

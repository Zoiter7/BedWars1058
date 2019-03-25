package com.andrei1058.bedwars.database;

import com.andrei1058.bedwars.Main;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class None implements Database {

    private Connection connection;

    /**
     * Check if database is connected.
     */
    public boolean isConnected() {
        if (connection == null) return false;
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void init() {
        File folder = new File(Main.plugin.getDataFolder() + "/Cache");
        if (!folder.exists()) folder.mkdir();
        File dataFolder = new File(folder.getPath() + "/shop.db");
        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            ClassLoader cl = Bukkit.getServer().getClass().getClassLoader();
            Driver d = (Driver) cl.loadClass("org.sqlite.JDBC").newInstance();
            DriverManager.registerDriver(d);
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            if (e instanceof ClassNotFoundException)
                Main.plugin.getLogger().severe("Could Not Found SQLite Driver on your system!");
            e.printStackTrace();
            return;
        }

        try {
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS quick_buy (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid VARCHAR(200), " +
                    "slot_19 VARCHAR(200), slot_20 VARCHAR(200), slot_21 VARCHAR(200), slot_22 VARCHAR(200), slot_23 VARCHAR(200), slot_24 VARCHAR(200), slot_25 VARCHAR(200)," +
                    "slot_28 VARCHAR(200), slot_29 VARCHAR(200), slot_30 VARCHAR(200), slot_31 VARCHAR(200), slot_32 VARCHAR(200), slot_33 VARCHAR(200), slot_34 VARCHAR(200)," +
                    "slot_37 VARCHAR(200), slot_38 VARCHAR(200), slot_39 VARCHAR(200), slot_40 VARCHAR(200), slot_41 VARCHAR(200), slot_42 VARCHAR(200), slot_43 VARCHAR(200));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveStats(UUID uuid, String username, Timestamp firstPlay, Timestamp lastPlay, int wins, int kills, int finalKills, int losses, int deaths, int finalDeaths, int bedsDestroyed, int gamesPlayed) {

    }

    @Override
    public void updateLocalCache(UUID uuid) {

    }

    @Override
    public void close() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setQuickBuySlot(UUID p, String shopPath, int slot) {
        if (!isConnected()) init();
        try {
            ResultSet rs = connection.prepareStatement("SELECT id FROM quick_buy WHERE uuid = '" + p.toString() + "';").executeQuery();
            if (!rs.next()) {
                connection.prepareStatement("INSERT INTO quick_buy VALUES(0,'" + p.toString() + "',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ');").executeUpdate();
            }
            Main.debug("UPDATE SET SLOT " + slot + " identifier " + shopPath);
            connection.prepareStatement("UPDATE quick_buy SET slot_" + slot + " = '" + shopPath + "' WHERE uuid = '" + p.toString() + "';").executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getQuickBuySlots(UUID p, int slot) {
        String result = "";
        if (!isConnected()) init();
        try {
            ResultSet rs = connection.prepareStatement("SELECT slot_" + slot + " FROM quick_buy WHERE uuid = '" + p.toString() + "';").executeQuery();
            if (rs.next()) result = rs.getString("slot_" + slot);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean hasQuickBuy(UUID uuid) {
        if (!isConnected()) init();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT id FROM quick_buy WHERE uuid = '" + uuid.toString() + "';");
            if (rs.next()) {
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

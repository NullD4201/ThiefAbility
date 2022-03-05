package jinu.nulld;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    FileConfiguration config = YamlConfiguration.loadConfiguration(new File(ThiefAB.getPlugin(ThiefAB.class).getDataFolder(), "dbconfig.yml"));

    private final String host = config.getString("database.host");
    private final int port = config.getInt("database.port");
    private final String database = config.getString("database.database");
    private final String username = config.getString("database.user");
    private final String password = config.getString("database.password");

    private Connection connection;

    public boolean isConnected() {
        return (connection != null);
    }

    public void connect() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

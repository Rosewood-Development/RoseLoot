package dev.rosewood.roseloot.database.migrations;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class _1_Add_Table_Cooldowns extends DataMigration {

    public _1_Add_Table_Cooldowns() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tablePrefix + "cooldowns (id VARCHAR(36) NOT NULL, player VARCHAR(36) NOT NULL, expiration BIGINT NOT NULL, PRIMARY KEY (id, player))")) {
            statement.executeUpdate();
        }
    }

}

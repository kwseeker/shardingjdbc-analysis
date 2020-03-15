package top.kwseeker.shardingjdbc.executor;

import lombok.Getter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Getter
public class SqlExecutor {

    private final DataSource dataSource;

    public SqlExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute(String sql) throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement();
        statement.execute(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement();
        return statement.executeQuery(sql);
    }

    public int executeUpdate(String sql) throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement statement = conn.createStatement();
        return statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
    }
}

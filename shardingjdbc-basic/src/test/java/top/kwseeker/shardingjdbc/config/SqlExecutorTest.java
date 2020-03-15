package top.kwseeker.shardingjdbc.config;

import org.junit.Before;
import org.junit.Test;
import top.kwseeker.shardingjdbc.executor.SqlExecutor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlExecutorTest {

    private SqlExecutor sqlExecutor;

    @Before
    public void init() {
        DataSourceMapConfiguration configuration = new DataSourceMapConfiguration();
        this.sqlExecutor = new SqlExecutor(configuration.getDataSource());
    }

    @Test
    public void testCreateTable() throws SQLException {
        sqlExecutor.execute("CREATE TABLE IF NOT EXISTS t_order (" +
                "order_id BIGINT AUTO_INCREMENT, " +
                "user_id INT NOT NULL, " +
                "status VARCHAR(50), " +
                "PRIMARY KEY (order_id ));");
        sqlExecutor.execute("CREATE TABLE IF NOT EXISTS t_order_item (" +
                "order_item_id BIGINT AUTO_INCREMENT, " +
                "order_id BIGINT, " +
                "user_id INT NOT NULL, " +
                "status VARCHAR(50), " +
                "PRIMARY KEY (order_item_id));");
    }

    @Test
    public void testTruncateTable() throws SQLException {
        sqlExecutor.execute("TRUNCATE TABLE t_order");
        sqlExecutor.execute("TRUNCATE TABLE t_order_item");
    }

    @Test
    public void testDropTable() throws SQLException {
        sqlExecutor.execute("DROP TABLE IF EXISTS t_order");
        sqlExecutor.execute("DROP TABLE IF EXISTS t_order_item");
    }

    @Test
    public void testInsertOneData() throws SQLException {
        Connection conn = sqlExecutor.getDataSource().getConnection();
        Statement statement = conn.createStatement();

        int rowCount = statement.executeUpdate("INSERT INTO t_order (user_id, status) VALUES (10, 'INIT')", Statement.RETURN_GENERATED_KEYS);
        System.out.println(rowCount);
        ResultSet resultSet = statement.getGeneratedKeys();
        Long orderId = resultSet.next()? resultSet.getLong(1) : -1;
        System.out.println(orderId);
        sqlExecutor.executeUpdate(String.format("INSERT INTO t_order_item (order_id, user_id) VALUES (%d, 10)", orderId));
    }

    @Test
    public void testInsertData() throws SQLException {
        Connection conn = null;
        Statement statement = null;
        try {
            conn = sqlExecutor.getDataSource().getConnection();
            statement = conn.createStatement();
            for (int i = 1; i <= 10; i++) {
                int rowCount = statement.executeUpdate("INSERT INTO t_order (user_id, status) VALUES (10, 'INIT')", Statement.RETURN_GENERATED_KEYS);
                System.out.println("Loop " + i + ":" + rowCount);
                ResultSet resultSet = statement.getGeneratedKeys();
                Long orderId = resultSet.next()? resultSet.getLong(1) : -1;
                System.out.println("Loop " + i + ":" + orderId);
                statement.executeUpdate(String.format("INSERT INTO t_order_item (order_id, user_id) VALUES (%d, 10)", orderId));

                rowCount = statement.executeUpdate("INSERT INTO t_order (user_id, status) VALUES (11, 'INIT')", Statement.RETURN_GENERATED_KEYS);
                System.out.println("Loop " + i + ":" + rowCount);
                resultSet = statement.getGeneratedKeys();
                orderId = resultSet.next()? resultSet.getLong(1) : -1;
                System.out.println("Loop " + i + ":" + orderId);
                sqlExecutor.execute(String.format("INSERT INTO t_order_item (order_id, user_id) VALUES (%d, 11)", orderId));
            }
        } finally {
            if(statement != null) {
                statement.close();
            }
        }
    }

    @Test
    public void testQuery() throws SQLException {
        ResultSet resultSet = sqlExecutor.executeQuery("SELECT * FROM t_order WHERE order_id=1");
        while (resultSet.next()) {
            System.out.println(resultSet.getLong("order_id") + ","
                    + resultSet.getLong("user_id") + ","
                    + resultSet.getString("status"));
        }
    }
}
package top.kwseeker.shardingjdbc.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DataSourceMapConfiguration {

    private DataSource dataSource = null;

    public DataSource getDataSource() {
        if(dataSource == null) {
            throw new NullPointerException("dataSource can not be null");
        }
        return this.dataSource;
    }

    public DataSourceMapConfiguration() {
        try {
            //1) 配置数据源
            Map<String, DataSource> dataSourceMap = new HashMap<>();
            //第一个数据源
            //TODO：各种DataSource实现类的区别
            BasicDataSource ds0 = new BasicDataSource();
            ds0.setDriverClassName("com.mysql.cj.jdbc.Driver");
            ds0.setUrl("jdbc:mysql://localhost:3307/orders_0?useSSL=false");
            ds0.setUsername("root");
            ds0.setPassword("123456");
            dataSourceMap.put("orders_0",ds0);
            //第二个数据源
            BasicDataSource ds1 = new BasicDataSource();
            ds1.setDriverClassName("com.mysql.cj.jdbc.Driver");
            ds1.setUrl("jdbc:mysql://localhost:3308/orders_1?useSSL=false");
            ds1.setUsername("root");
            ds1.setPassword("123456");
            dataSourceMap.put("orders_1",ds1);

            //2) 设置分库分表的规则
            //  指定逻辑表
            //  指定物理表
            //  设置主键生成配置（默认： TODO）
            //  设置数据库分片策略（默认：TODO）
            //  设置表分片策略配置（默认：TODO）

            // t_order ********************************************
            // 表规则：逻辑表，物理表
            TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration(
                    "t_order",                               //逻辑表
                    "orders_${0..1}.t_order_${0..1}"   //物理表
            );
            // 指定主键生成策略
            orderTableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE", "order_id"));
            // 分库分表（分片）策略
            orderTableRuleConfig.setDatabaseShardingStrategyConfig(
                    new InlineShardingStrategyConfiguration("user_id", "orders_${user_id % 2}"));
            orderTableRuleConfig.setTableShardingStrategyConfig(
                    new InlineShardingStrategyConfiguration("order_id", "t_order_${order_id % 2}"));

            // t_order_item ****************************************
            // 表规则
            TableRuleConfiguration orderItemTableRuleConfig = new TableRuleConfiguration(
                    "t_order_item",
                    "orders_${0..1}.t_order_item_${[0, 1]}"
            );
            orderItemTableRuleConfig.setDatabaseShardingStrategyConfig(
                    new InlineShardingStrategyConfiguration("user_id", "orders_${user_id % 2}"));
            orderItemTableRuleConfig.setTableShardingStrategyConfig(
                    new InlineShardingStrategyConfiguration("order_id", "t_order_item_${order_id % 2}"));

            // 设置分片规则（分片规则包括分表规则）
            ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
            shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);
            shardingRuleConfig.getTableRuleConfigs().add(orderItemTableRuleConfig);
            // 设置绑定表
            shardingRuleConfig.getBindingTableGroups().add("t_order,t_order_item");

            //3）获取数据源对象
            Properties properties = new Properties();
            properties.setProperty("sql.show", "true");     //日志打印sql语句
            dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, properties);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

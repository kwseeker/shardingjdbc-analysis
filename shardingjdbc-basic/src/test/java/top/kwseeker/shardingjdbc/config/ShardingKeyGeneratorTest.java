package top.kwseeker.shardingjdbc.config;

import com.google.common.base.Strings;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.apache.shardingsphere.spi.algorithm.keygen.ShardingKeyGeneratorServiceLoader;
import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;
import org.junit.Test;

public class ShardingKeyGeneratorTest {

    @Test
    public void testGenerateKey() {
        //配置阶段
        KeyGeneratorConfiguration keyGeneratorConfig = new KeyGeneratorConfiguration("SNOWFLAKE", "order_id");
        //KeyGeneratorConfiguration keyGeneratorConfig = new KeyGeneratorConfiguration("UUID", "order_id");
        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration("yourLogicTable", "yourActualDataNodes");
        tableRuleConfiguration.setKeyGeneratorConfig(keyGeneratorConfig);
        //解析阶段
        ShardingKeyGenerator shardingKeyGenerator;  //这个是TableRule的成员变量shardingKeyGenerator
        if(null != tableRuleConfiguration.getKeyGeneratorConfig()
                && !Strings.isNullOrEmpty(tableRuleConfiguration.getKeyGeneratorConfig().getType())) {
            shardingKeyGenerator = (new ShardingKeyGeneratorServiceLoader()).newService(
                    tableRuleConfiguration.getKeyGeneratorConfig().getType(),
                    tableRuleConfiguration.getKeyGeneratorConfig().getProperties());
        } else {
            shardingKeyGenerator = null;
        }
        //路由阶段,生成主键唯一ID
        Long keyID = null;
        if(shardingKeyGenerator != null) {
            keyID = (Long)shardingKeyGenerator.generateKey();

        } else {
            //使用默认的ShardingKeyGenerator，即SPI文件org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator中指定的第一个实现类
            //即默认就是 org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator
            try {
                keyID = (Long)SnowflakeShardingKeyGenerator.class.newInstance().generateKey();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        System.out.println(keyID);
    }
}

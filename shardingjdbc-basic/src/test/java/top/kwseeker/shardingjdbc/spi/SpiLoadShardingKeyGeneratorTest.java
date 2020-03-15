package top.kwseeker.shardingjdbc.spi;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;
import org.junit.Test;

import java.util.*;


public class SpiLoadShardingKeyGeneratorTest {

    @Test
    public void testSpiLoadShardingKeyGenerator() throws InstantiationException, IllegalAccessException {
        Class<?> classType = ShardingKeyGenerator.class;
        //系统启动阶段装载SPI指定的类
        ServiceLoader<?> keyGeneratorServiceLoader = ServiceLoader.load(classType);     //TODO
        Iterator var1 = keyGeneratorServiceLoader.iterator();
        Map<Class, Collection<Class<?>>> SERVICE_MAP = new HashMap<>();
        while (var1.hasNext()) {
            ShardingKeyGenerator each = (ShardingKeyGenerator) var1.next();
            Collection<Class<?>> serviceClasses = SERVICE_MAP.get(classType);
            if(null == serviceClasses){
                serviceClasses = new LinkedHashSet<>();
            }
            serviceClasses.add(each.getClass());
            SERVICE_MAP.put(classType, serviceClasses);
        }

        //往数据库插入新数据时实例化加载的指定的类（这里时ShardingKeyGenerator）
        List objects = new LinkedList<>();
        Iterator var2 = SERVICE_MAP.get(classType).iterator();
        while (var2.hasNext()) {
            Class<?> each = (Class) var2.next();
            objects.add(each.newInstance());
        }

        //根据指定的type筛选获取具体类的实例
        //String type = "SNOWFLAKE";
        String type = "UUID";
        Collection<ShardingKeyGenerator> instances = Collections2.filter(objects, new Predicate<ShardingKeyGenerator>() {
            @Override
            public boolean apply(ShardingKeyGenerator input) {
                return type.equalsIgnoreCase(input.getType());
            }
        });
        ShardingKeyGenerator result = instances.iterator().next();

        //使用实例创建主键ID
        System.out.println(result.generateKey());
    }
}

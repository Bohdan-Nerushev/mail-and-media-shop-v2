import org.springframework.data.redis.serializer.RedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestRedis {
    public void test() {
        ObjectMapper m = new ObjectMapper();
        RedisSerializer<Object> s = RedisSerializer.json(m);
    }
}

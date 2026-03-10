package dev.mam.buizsol.mamshop;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MaMShopApplicationTest {

    @Test
    void contextLoads() {}

    @Test
    void mainMethodTest() {
        assertDoesNotThrow(() -> MaMShopApplication.main(new String[] {"--server.port=0"}));
    }
}

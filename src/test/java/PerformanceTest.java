import com.mikhailsazhin.deadlockexample.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceTest {

    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        account1 = new Account();
        account2 = new Account();
    }

    @Test
    @DisplayName("Measure throughput with different thread counts")
    void testThroughput() throws InterruptedException {
        int[] threadCounts = {2, 4, 8, 16, 32};
        int operationsPerThread = 10000;

        for (int threadCount : threadCounts) {
            long startTime = System.nanoTime();
            runConcurrentTransfers(threadCount, operationsPerThread);
            long endTime = System.nanoTime();

            double durationMs = (endTime - startTime) / 1_000_000.0;
            long totalOperations = (long) threadCount * operationsPerThread;
            double throughput = totalOperations / (durationMs / 1000.0);

            System.out.printf("Threads: %d, Duration: %.2f ms, Throughput: %.2f ops/sec%n",
                    threadCount, durationMs, throughput);

            assertTrue(durationMs < 30000, "Performance test should complete within 30 seconds");
        }
    }

    private void runConcurrentTransfers(int threadCount, int operationsPerThread)
            throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        if (threadId % 2 == 0) {
                            Account.transfer(account1, account2, 1);
                        } else {
                            Account.transfer(account2, account1, 1);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }
}
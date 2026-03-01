import com.mikhailsazhin.deadlockexample.Runner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RunnerTest {

    private Runner runner;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        runner = new Runner();
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Test single thread execution")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSingleThread() throws InterruptedException {
        Thread thread1 = new Thread(runner::firstThread);

        thread1.start();
        thread1.join();

        runner.finished();

        String output = outputStream.toString();
        assertTrue(output.contains("Total balance: 20000"));
    }

    @Test
    @DisplayName("Test both threads without deadlock")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testBothThreadsNoDeadlock() throws InterruptedException {
        Thread thread1 = new Thread(runner::firstThread);
        Thread thread2 = new Thread(runner::secondThread);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        runner.finished();

        String output = outputStream.toString();
        assertTrue(output.contains("Total balance: 20000"));
    }

    @Test
    @DisplayName("Test multiple runs to ensure consistency")
    void testMultipleRuns() {
        for (int i = 0; i < 10; i++) {
            runner = new Runner();
            try {
                testBothThreadsNoDeadlock();
            } catch (InterruptedException e) {
                fail("Test interrupted: " + e.getMessage());
            }
        }
    }
}
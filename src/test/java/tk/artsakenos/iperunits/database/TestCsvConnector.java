package tk.artsakenos.iperunits.database;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;

@Disabled
@Log
public class TestCsvConnector {

    @Test
    void testCsv() {
        CSVConnector csv = new CSVConnector();

        csv.add("gino", 2, "pippo");
        csv.add("gino", 2, "pappo");
        csv.add("gino", 3, "lello");
        csv.add("pino", 3, "lallo");

        log.info(csv.toString());

        LinkedHashSet<Object[]> get = csv.get("gino", 2);
        log.info(csv.toString(get));
    }


}

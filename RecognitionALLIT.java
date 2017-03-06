
package net.sf.javaanpr.test;

import net.sf.javaanpr.imageanalysis.CarSnapshot;
import net.sf.javaanpr.intelligence.Intelligence;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

// Parameterized - running the same test over and over again using different values.
// Five steps to create a parameterized test
//      1 - Annotate test class with @RunWith(Parameterized.class)
//      2 - Create a pubic static method annotated with @Parameters that
//          return a Collection of Objects (as Array) as test data set.
//      3 - Create a public constructor that takes in what is equivalent
//          to one "row" of test data.
//      4 - Creata an instance variable for each "column" of test data
//      5 - Create your test case using the instance variables as the
//          source of the test data.

// Step 1 - Annotation @RunWith(Parameterized.class)
@RunWith(Parameterized.class)
public class RecognitionALLIT {

    // Step 4 - Instance variables for each "column" of test data
    private File plate;
    private String expectedPlate;
    private CarSnapshot carSnap;
    private Intelligence intel;

    // Step 3 - Constructor, that takes in one row of test data
    public RecognitionALLIT(File plate, String expectedPlate){
        this.plate = plate;
        this.expectedPlate = expectedPlate;
    }

    @Before
    public void setup() throws IOException, ParserConfigurationException, SAXException{
        carSnap = new CarSnapshot(new FileInputStream(plate));
        intel = new Intelligence();
    }

    // Step 2 - Annotation @Parameters, return a Collection of Objects (as Array)
    @Parameterized.Parameters
    public static Collection<Object[]> testData() throws IOException {
        String snapshotDirPath = "src/test/resources/snapshots";
        String resultsPath = "src/test/resources/results.properties";
        InputStream resultsStream = new FileInputStream(new File(resultsPath));

        Properties properties = new Properties();
        properties.load(resultsStream);
        resultsStream.close();

        File snapshotDir = new File(snapshotDirPath);
        File[] snapshots = snapshotDir.listFiles();

        Collection<Object[]> imageData = new ArrayList();
        for(File file : snapshots){
            String name = file.getName();
            String expectedPlate = properties.getProperty(name);
            imageData.add(new Object[]{
                file, expectedPlate
            });
        }
        return imageData;
    }

    // Step 5 - Test case with test data
    @Test
    public void testAll() throws IOException, SAXException, ParserConfigurationException {
        // The test case will be invoked once for each row of data.
        String plate = intel.recognize(carSnap, false);
        assertThat(plate, equalTo(expectedPlate));  // assertThat - test value as first parameter
    }

}

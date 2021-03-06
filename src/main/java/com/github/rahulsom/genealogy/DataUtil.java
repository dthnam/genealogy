package com.github.rahulsom.genealogy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton that stores data so multiple instances of NameDbUsa can be created.
 */
public class DataUtil {

    static class Holder {
        private static final DataUtil instance = new DataUtil();
    }

    static DataUtil getInstance() {
        return Holder.instance;
    }

    private List<Name> maleNames = new ArrayList<Name>();
    private List<Name> femaleNames = new ArrayList<Name>();
    private List<LastName> lastNames = new ArrayList<LastName>();

    public List<Name> getMaleNames() {
        return maleNames;
    }

    public List<Name> getFemaleNames() {
        return femaleNames;
    }

    public List<LastName> getLastNames() {
        return lastNames;
    }

    private DataUtil() {
        processResource("last.csv", new AbstractProcessor() {
            @Override
            public void processLine(String line, long index) {
                if (index > 0) {
                    String[] parts = line.split(",");
                    lastNames.add(new LastName(
                            parts[0],
                            getDouble(parts[4]),
                            getDouble(parts[5], 100.0),
                            getDouble(parts[6], 100.0),
                            getDouble(parts[7], 100.0),
                            getDouble(parts[8], 100.0),
                            getDouble(parts[9], 100.0),
                            getDouble(parts[10], 100.0)
                    ));
                }
            }
        });

        processResource("dist.female.first", new AbstractProcessor() {
            @Override
            public void processLine(String line, long index) {
                String[] parts = line.split(" +");
                femaleNames.add(new Name(parts[0], Double.parseDouble(parts[2])));
            }
        });

        processResource("dist.male.first", new AbstractProcessor() {
            @Override
            public void processLine(String line, long index) {
                String[] parts = line.split(" +");
                maleNames.add(new Name(parts[0], Double.parseDouble(parts[2])));
            }
        });
    }

    private static Double getDouble(String string, double divisor) {
        return string.matches("[0-9\\.]+") ? Double.valueOf(string) / divisor : null;
    }

    private static Double getDouble(String string) {
        return getDouble(string, 1.0d);
    }

    /**
     * Think of eachLineWithIndex()
     */
    private static abstract class AbstractProcessor {
        public abstract void processLine(String line, long index);
    }

    /**
     * Processes a given resource using provided closure
     *
     * @param resourceName resource to fetch from classpath
     * @param processor    how to process the resource
     */
    private static void processResource(String resourceName, AbstractProcessor processor) {
        InputStream lastNameStream = NameDbUsa.class.getClassLoader().getResourceAsStream(resourceName);
        BufferedReader lastNameReader = new BufferedReader(new InputStreamReader(lastNameStream));
        try {
            int index = 0;
            while (lastNameReader.ready()) {
                String line = lastNameReader.readLine();
                processor.processLine(line, index++);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

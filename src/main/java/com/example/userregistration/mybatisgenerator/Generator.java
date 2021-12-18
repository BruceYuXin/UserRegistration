package com.example.userregistration.mybatisgenerator;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Generator {
    private static final String CLASSNAME = Generator.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASSNAME);
    private static final String GENERATOR_CONFIG_FILE = "src/main/resources/generatorConfig.xml";

    public static void generateTables() throws IOException, XMLParserException, InvalidConfigurationException, InterruptedException, SQLException {
        final String METHOD_NAME = "generateTables";
        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(new File(GENERATOR_CONFIG_FILE));
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        LOGGER.info(CLASSNAME+" "+METHOD_NAME+" finish");
    }

    public static void main(String[] args) {
        try {
            Generator.generateTables();
        } catch (Exception e) {
            LOGGER.info("******* "+e.getMessage());
        }
    }
}

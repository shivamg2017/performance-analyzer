/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer;


import java.io.File;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.opensearch.performanceanalyzer.util.Utils;

@Ignore
public class AbstractTests {

    // The TemporaryFolder Rule allows creation of files and folders that are
    // guaranteed to be deleted when the test method finishes (whether it passes
    // or fails)
    // But it is possible the deletion won't happen when you debug the code if
    // you quit early
    @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

    protected String rootLocation;

    public AbstractTests() {
        super();
        this.rootLocation = null;
    }

    @BeforeClass
    public static void setupLogging() {
        ConfigurationBuilder<BuiltConfiguration> configurationBuilder =
                ConfigurationBuilderFactory.newConfigurationBuilder();
        configurationBuilder.setStatusLevel(Level.INFO);
        configurationBuilder.setConfigurationName("DefaultConfig");
        configurationBuilder.add(
                configurationBuilder
                        .newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL)
                        .addAttribute("level", Level.DEBUG));
        AppenderComponentBuilder appenderBuilder =
                configurationBuilder
                        .newAppender("Stdout", "CONSOLE")
                        .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderBuilder.add(
                configurationBuilder
                        .newLayout("PatternLayout")
                        .addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"));
        appenderBuilder.add(
                configurationBuilder
                        .newFilter("MarkerFilter", Filter.Result.DENY, Filter.Result.NEUTRAL)
                        .addAttribute("marker", "FLOW"));
        configurationBuilder.add(appenderBuilder);
        configurationBuilder.add(
                configurationBuilder
                        .newLogger("org.apache.logging.log4j", Level.DEBUG)
                        .add(configurationBuilder.newAppenderRef("Stdout"))
                        .addAttribute("additivity", false));
        configurationBuilder.add(
                configurationBuilder
                        .newRootLogger(Level.DEBUG)
                        .add(configurationBuilder.newAppenderRef("Stdout")));
        Configurator.initialize(configurationBuilder.build());
    }

    @Before
    public void setUp() throws Exception {
        rootLocation = temporaryFolder.getRoot().getCanonicalPath() + File.separator;
        Utils.configureMetrics();
        System.setProperty("performanceanalyzer.metrics.log.enabled", "False");
    }
}

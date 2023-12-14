// spring-projects/spring-boot/blob/v2.6.4/spring-boot-project/spring-boot-cli/src/main/java/org/springframework/boot/cli/command/run/SpringApplicationRunner.java
SpringApplicationRunner(SpringApplicationRunnerConfiguration configuration, String[] sources, String... args) {
        this.configuration = configuration;
        this.sources = sources.clone();
        this.args = args.clone();
        this.compiler = new GroovyCompiler(configuration);
        int level = configuration.getLogLevel().intValue();
        if (level <= Level.FINER.intValue()) {
        System.setProperty("org.springframework.boot.cli.compiler.grape.ProgressReporter", "detail");
        System.setProperty("trace", "true");
        }
        else if (level <= Level.FINE.intValue()) {
        System.setProperty("debug", "true");
        }
        else if (level == Level.OFF.intValue()) {
        System.setProperty("spring.main.banner-mode", "OFF");
        System.setProperty("logging.level.ROOT", "OFF");
        System.setProperty("org.springframework.boot.cli.compiler.grape.ProgressReporter", "none");
        }
        }

//spring-projects/spring-boot/blob/v2.6.4/spring-boot-project/spring-boot-cli/src/main/java/org/springframework/boot/cli/command/run/RunCommand.java
private class SpringApplicationRunnerConfigurationAdapter extends OptionSetGroovyCompilerConfiguration
        implements SpringApplicationRunnerConfiguration {

    SpringApplicationRunnerConfigurationAdapter(OptionSet options, CompilerOptionHandler optionHandler,
                                                List<RepositoryConfiguration> repositoryConfiguration) {
        super(options, optionHandler, repositoryConfiguration);
    }

    @Override
    public GroovyCompilerScope getScope() {
        return GroovyCompilerScope.DEFAULT;
    }

    @Override
    public boolean isWatchForFileChanges() {
        return getOptions().has(RunOptionHandler.this.watchOption);
    }

    @Override
    public Level getLogLevel() {
        if (isQuiet()) {
            return Level.OFF;
        }
        if (getOptions().has(RunOptionHandler.this.verboseOption)) {
            return Level.FINEST;
        }
        return Level.INFO;
    }

    @Override
    public boolean isQuiet() {
        return getOptions().has(RunOptionHandler.this.quietOption);
    }

}

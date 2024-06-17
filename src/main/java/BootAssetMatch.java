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


                @Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		if (this.beanFactory instanceof ListableBeanFactory && getClass() == TypeExcludeFilter.class) {
			for (TypeExcludeFilter delegate : getDelegates()) {
				if (delegate.match(metadataReader, metadataReaderFactory)) {
					return true;
				}
			}
		}
		return false;
	}

	private Collection<TypeExcludeFilter> getDelegates() {
		Collection<TypeExcludeFilter> delegates = this.delegates;
		if (delegates == null) {
			delegates = ((ListableBeanFactory) this.beanFactory).getBeansOfType(TypeExcludeFilter.class).values();
			this.delegates = delegates;
		}
		return delegates;
	}



                private String extractDescription(String uri) {
		uri = uri.substring(JAR_SCHEME.length());
		int firstDotJar = uri.indexOf(JAR_EXTENSION);
		String firstJar = getFilename(uri.substring(0, firstDotJar + JAR_EXTENSION.length()));
		uri = uri.substring(firstDotJar + JAR_EXTENSION.length());
		int lastDotJar = uri.lastIndexOf(JAR_EXTENSION);
		if (lastDotJar == -1) {
			return firstJar;
		}
		return firstJar + uri.substring(0, lastDotJar + JAR_EXTENSION.length());
	}


                	/**
	 * Configure the provided {@link ThreadPoolTaskExecutor} instance using this builder.
	 * @param <T> the type of task executor
	 * @param taskExecutor the {@link ThreadPoolTaskExecutor} to configure
	 * @return the task executor instance
	 * @see #build()
	 * @see #build(Class)
	 */
	public <T extends ThreadPoolTaskExecutor> T configure(T taskExecutor) {
		PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
		map.from(this.queueCapacity).to(taskExecutor::setQueueCapacity);
		map.from(this.corePoolSize).to(taskExecutor::setCorePoolSize);
		map.from(this.maxPoolSize).to(taskExecutor::setMaxPoolSize);
		map.from(this.keepAlive).asInt(Duration::getSeconds).to(taskExecutor::setKeepAliveSeconds);
		map.from(this.allowCoreThreadTimeOut).to(taskExecutor::setAllowCoreThreadTimeOut);
		map.from(this.awaitTermination).to(taskExecutor::setWaitForTasksToCompleteOnShutdown);
		map.from(this.awaitTerminationPeriod).as(Duration::toMillis).to(taskExecutor::setAwaitTerminationMillis);
		map.from(this.threadNamePrefix).whenHasText().to(taskExecutor::setThreadNamePrefix);
		map.from(this.taskDecorator).to(taskExecutor::setTaskDecorator);
		if (!CollectionUtils.isEmpty(this.customizers)) {
			this.customizers.forEach((customizer) -> customizer.customize(taskExecutor));
		}
		return taskExecutor;
	}

	private <T> Set<T> append(Set<T> set, Iterable<? extends T> additions) {
		Set<T> result = new LinkedHashSet<>((set != null) ? set : Collections.emptySet());
		additions.forEach(result::add);
		return Collections.unmodifiableSet(result);
	}


			private static URL[] getExtensionURLs() {
		List<URL> urls = new ArrayList<>();
		String home = SystemPropertyUtils.resolvePlaceholders("${spring.home:${SPRING_HOME:.}}");
		File extDirectory = new File(new File(home, "lib"), "ext");
		if (extDirectory.isDirectory()) {
			for (File file : extDirectory.listFiles()) {
				if (file.getName().endsWith(".jar")) {
					try {
						urls.add(file.toURI().toURL());
					}
					catch (MalformedURLException ex) {
						throw new IllegalStateException(ex);
					}
				}
			}
		}
		return urls.toArray(new URL[0]);
	}

	 */
	public static List<String> getUrls(String path, ClassLoader classLoader) {
		if (classLoader == null) {
			classLoader = ClassUtils.getDefaultClassLoader();
		}
		path = StringUtils.cleanPath(path);
		try {
			return getUrlsFromWildcardPath(path, classLoader);
		}
		catch (Exception ex) {
			throw new IllegalArgumentException("Cannot create URL from path [" + path + "]", ex);
		}
	}

	private static List<String> getUrlsFromWildcardPath(String path, ClassLoader classLoader) throws IOException {
		if (path.contains(":")) {
			return getUrlsFromPrefixedWildcardPath(path, classLoader);
		}
		Set<String> result = new LinkedHashSet<>();
		try {
			result.addAll(getUrls(FILE_URL_PREFIX + path, classLoader));
		}
		catch (IllegalArgumentException ex) {
			// ignore
		}
		path = stripLeadingSlashes(path);
		result.addAll(getUrls(ALL_CLASSPATH_URL_PREFIX + path, classLoader));
		return new ArrayList<>(result);
	}

	private static List<String> getUrlsFromPrefixedWildcardPath(String path, ClassLoader classLoader)
			throws IOException {
		Resource[] resources = new PathMatchingResourcePatternResolver(new FileSearchResourceLoader(classLoader))
				.getResources(path);
		List<String> result = new ArrayList<>();
		for (Resource resource : resources) {
			if (resource.exists()) {
				if ("file".equals(resource.getURI().getScheme()) && resource.getFile().isDirectory()) {
					result.addAll(getChildFiles(resource));
					continue;
				}
				result.add(absolutePath(resource));
			}
		}
		return result;
	}

/**
	 * Create a new {@link SpringApplicationRunner} instance.
	 * @param configuration the configuration
	 * @param sources the files to compile/watch
	 * @param args input arguments
	 */
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
		
		

}

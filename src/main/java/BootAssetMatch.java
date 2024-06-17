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
		
		/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.cli.command.run;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.springframework.boot.cli.app.SpringApplicationLauncher;
import org.springframework.boot.cli.compiler.GroovyCompiler;
import org.springframework.boot.cli.util.ResourceUtils;

/**
 * Compiles Groovy code running the resulting classes using a {@code SpringApplication}.
 * Takes care of threading and class-loading issues and can optionally monitor sources for
 * changes.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @since 1.0.0
 */
public class SpringApplicationRunner {

	private static int watcherCounter = 0;

	private static int runnerCounter = 0;

	private final Object monitor = new Object();

	private final SpringApplicationRunnerConfiguration configuration;

	private final String[] sources;

	private final String[] args;

	private final GroovyCompiler compiler;

	private RunThread runThread;

	private FileWatchThread fileWatchThread;

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

	/**
	 * Compile and run the application.
	 * @throws Exception on error
	 */
	public void compileAndRun() throws Exception {
		synchronized (this.monitor) {
			try {
				stop();
				Class<?>[] compiledSources = compile();
				monitorForChanges();
				// Run in new thread to ensure that the context classloader is setup
				this.runThread = new RunThread(compiledSources);
				this.runThread.start();
				this.runThread.join();
			}
			catch (Exception ex) {
				if (this.fileWatchThread == null) {
					throw ex;
				}
				else {
					ex.printStackTrace();
				}
			}
		}
	}

	public void stop() {
		synchronized (this.monitor) {
			if (this.runThread != null) {
				this.runThread.shutdown();
				this.runThread = null;
			}
		}
	}

	private Class<?>[] compile() throws IOException {
		Class<?>[] compiledSources = this.compiler.compile(this.sources);
		if (compiledSources.length == 0) {
			throw new RuntimeException("No classes found in '" + Arrays.toString(this.sources) + "'");
		}
		return compiledSources;
	}

	private void monitorForChanges() {
		if (this.fileWatchThread == null && this.configuration.isWatchForFileChanges()) {
			this.fileWatchThread = new FileWatchThread();
			this.fileWatchThread.start();
		}
	}

	/**
	 * Thread used to launch the Spring Application with the correct context classloader.
	 */
	private class RunThread extends Thread {

		private final Object monitor = new Object();

		private final Class<?>[] compiledSources;

		private Object applicationContext;

		/**
		 * Create a new {@link RunThread} instance.
		 * @param compiledSources the sources to launch
		 */
		RunThread(Class<?>... compiledSources) {
			super("runner-" + (runnerCounter++));
			this.compiledSources = compiledSources;
			if (compiledSources.length != 0) {
				setContextClassLoader(compiledSources[0].getClassLoader());
			}
			setDaemon(true);
		}

		@Override
		public void run() {
			synchronized (this.monitor) {
				try {
					this.applicationContext = new SpringApplicationLauncher(getContextClassLoader())
							.launch(this.compiledSources, SpringApplicationRunner.this.args);
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		/**
		 * Shutdown the thread, closing any previously opened application context.
		 */
		void shutdown() {
			synchronized (this.monitor) {
				if (this.applicationContext != null) {
					try {
						Method method = this.applicationContext.getClass().getMethod("close");
						method.invoke(this.applicationContext);
					}
					catch (NoSuchMethodException ex) {
						// Not an application context that we can close
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
					finally {
						this.applicationContext = null;
					}
				}
			}
		}

	}

	private SourceOptions(List<?> nonOptionArguments, ClassLoader classLoader) {
		List<String> sources = new ArrayList<>();
		int sourceArgCount = 0;
		for (Object option : nonOptionArguments) {
			if (option instanceof String) {
				String filename = (String) option;
				if ("--".equals(filename)) {
					break;
				}
				List<String> urls = new ArrayList<>();
				File fileCandidate = new File(filename);
				if (fileCandidate.isFile()) {
					urls.add(fileCandidate.getAbsoluteFile().toURI().toString());
				}
				else if (!isAbsoluteWindowsFile(fileCandidate)) {
					urls.addAll(ResourceUtils.getUrls(filename, classLoader));
				}
				for (String url : urls) {
					if (isSource(url)) {
						sources.add(url);
					}
				}
				if (isSource(filename)) {
					if (urls.isEmpty()) {
						throw new IllegalArgumentException("Can't find " + filename);
					}
					else {
						sourceArgCount++;
					}
				}
			}
		}
		this.args = Collections.unmodifiableList(nonOptionArguments.subList(sourceArgCount, nonOptionArguments.size()));
		Assert.isTrue(!sources.isEmpty(), "Please specify at least one file");
		this.sources = Collections.unmodifiableList(sources);
	}

	private boolean isAbsoluteWindowsFile(File file) {
		return isWindows() && file.isAbsolute();
	}

	private boolean isWindows() {
		return File.separatorChar == '\\';
	}

	private boolean isSource(String name) {
		return name.endsWith(".java") || name.endsWith(".groovy");
	}

	public List<?> getArgs() {
		return this.args;
	}

	public String[] getArgsArray() {
		return this.args.stream().map(this::asString).toArray(String[]::new);
	}

	private String asString(Object arg) {
		return (arg != null) ? String.valueOf(arg) : null;
	}

	public List<String> getSources() {
		return this.sources;
	}

	public String[] getSourcesArray() {
		return StringUtils.toStringArray(this.sources);
	}

}

# integration-tests

The local CI environment composer developed on top of io.fabric8::docker-maven-plugin

# Build phases

## prerequisite 

- set __JAVA_OPTS__ environment variable. You may left it empty if nothing specific is required to be provided to JVM.

## build

During this phase following images are being built:

- __openexchange/mysql__ - MySQL image with users / schema initialization scripts.
- __rabbitmq__ - A popular AMPQ implementation serves as a service communication bus 
- __openexchange/registry:latest__ - Eureka based Service Registry.
- __openexchange/configserver:latest__ - Spring Cloud based configuration service integrated with GitHub configuration. 
- __openexchange/sms-producer:latest__ - SMS messages producer.
- __openexchange/sms-consumer:latest__ - SMS messages consumer.


## start

The containers runs in the following order: mysql -> rabbitmq -> registry -> configserver -> sms-producer -> sms-consumer.

## stop / remove
                                 
Stop and remove all running containers launched by the plugin.

# Known issues

## Incompatibility with JDK9 (Jigsaw)

- Install last JDK 9: `$ java -version 
java version "9-ea"
Java(TM) SE Runtime Environment (build 9-ea+143)
Java HotSpot(TM) 64-Bit Server VM (build 9-ea+143, mixed mode)`

- Install Apache Maven: `$ mvn -version
Apache Maven 3.3.9 ... `

- Install _Docker Tools_ and create default docker machine: `$ docker-machine ls
 NAME      ACTIVE   DRIVER       STATE     URL                         SWARM   DOCKER    ERRORS
 default   *        virtualbox   Running   tcp://192.168.99.100:2376           v1.12.3`

- Run docker build goal: `$ mvn docker:build`

- Run docker start goal: `$ mvn docker:build`

Having this configuration the run produces the following exception's stack trace:

`org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal io.fabric8:docker-maven-plugin:0.17.2:start (default-cli) on project integration-tests: Cannot create docker access object 
 	at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:212)
 	at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:153)
 	at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:145)
 	at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject(LifecycleModuleBuilder.java:116)
 	at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject(LifecycleModuleBuilder.java:80)
 	at org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder.build(SingleThreadedBuilder.java:51)
 	at org.apache.maven.lifecycle.internal.LifecycleStarter.execute(LifecycleStarter.java:128)
 	at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:307)
 	at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:193)
 	at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:106)
 	at org.apache.maven.cli.MavenCli.execute(MavenCli.java:863)
 	at org.apache.maven.cli.MavenCli.doMain(MavenCli.java:288)
 	at org.apache.maven.cli.MavenCli.main(MavenCli.java:199)
 	at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(java.base@9-ea/Native Method)
 	at jdk.internal.reflect.NativeMethodAccessorImpl.invoke(java.base@9-ea/NativeMethodAccessorImpl.java:62)
 	at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(java.base@9-ea/DelegatingMethodAccessorImpl.java:43)
 	at java.lang.reflect.Method.invoke(java.base@9-ea/Method.java:537)
 	at org.codehaus.plexus.classworlds.launcher.Launcher.launchEnhanced(Launcher.java:289)
 	at org.codehaus.plexus.classworlds.launcher.Launcher.launch(Launcher.java:229)
 	at org.codehaus.plexus.classworlds.launcher.Launcher.mainWithExitCode(Launcher.java:415)
 	at org.codehaus.plexus.classworlds.launcher.Launcher.main(Launcher.java:356)
 Caused by: org.apache.maven.plugin.MojoExecutionException: Cannot create docker access object 
 	at io.fabric8.maven.docker.AbstractDockerMojo.createDockerAccess(AbstractDockerMojo.java:289)
 	at io.fabric8.maven.docker.AbstractDockerMojo.execute(AbstractDockerMojo.java:191)
 	at org.apache.maven.plugin.DefaultBuildPluginManager.executeMojo(DefaultBuildPluginManager.java:134)
 	at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:207)
 	... 20 more
 Caused by: io.fabric8.maven.docker.access.DockerAccessException: Cannot extract API version from server https://192.168.99.100:2376
 	at io.fabric8.maven.docker.access.hc.DockerAccessWithHcClient.getServerApiVersion(DockerAccessWithHcClient.java:129)
 	at io.fabric8.maven.docker.AbstractDockerMojo.createDockerAccess(AbstractDockerMojo.java:282)
 	... 23 more
 Caused by: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
 	at sun.security.ssl.Alerts.getSSLException(java.base@9-ea/Alerts.java:198)
 	at sun.security.ssl.SSLSocketImpl.fatal(java.base@9-ea/SSLSocketImpl.java:1962)
 	at sun.security.ssl.Handshaker.fatalSE(java.base@9-ea/Handshaker.java:309)
 	at sun.security.ssl.Handshaker.fatalSE(java.base@9-ea/Handshaker.java:303)
 	at sun.security.ssl.ClientHandshaker.checkServerCerts(java.base@9-ea/ClientHandshaker.java:1843)
 	at sun.security.ssl.ClientHandshaker.serverCertificate(java.base@9-ea/ClientHandshaker.java:1652)
 	at sun.security.ssl.ClientHandshaker.processMessage(java.base@9-ea/ClientHandshaker.java:260)
 	at sun.security.ssl.Handshaker.processLoop(java.base@9-ea/Handshaker.java:1061)
 	at sun.security.ssl.Handshaker.processRecord(java.base@9-ea/Handshaker.java:995)
 	at sun.security.ssl.SSLSocketImpl.processInputRecord(java.base@9-ea/SSLSocketImpl.java:1132)
 	at sun.security.ssl.SSLSocketImpl.readRecord(java.base@9-ea/SSLSocketImpl.java:1069)
 	at sun.security.ssl.SSLSocketImpl.readRecord(java.base@9-ea/SSLSocketImpl.java:968)
 	at sun.security.ssl.SSLSocketImpl.performInitialHandshake(java.base@9-ea/SSLSocketImpl.java:1395)
 	at sun.security.ssl.SSLSocketImpl.startHandshake(java.base@9-ea/SSLSocketImpl.java:1422)
 	at sun.security.ssl.SSLSocketImpl.startHandshake(java.base@9-ea/SSLSocketImpl.java:1406)
 	at org.apache.http.conn.ssl.SSLConnectionSocketFactory.createLayeredSocket(SSLConnectionSocketFactory.java:394)
 	at org.apache.http.conn.ssl.SSLConnectionSocketFactory.connectSocket(SSLConnectionSocketFactory.java:353)
 	at org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:141)
 	at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.connect(PoolingHttpClientConnectionManager.java:353)
 	at org.apache.http.impl.execchain.MainClientExec.establishRoute(MainClientExec.java:380)
 	at org.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:236)
 	at org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:184)
 	at org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:88)
 	at org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:110)
 	at org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:184)
 	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:71)
 	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:220)
 	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:164)
 	at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:139)
 	at io.fabric8.maven.docker.access.hc.ApacheHttpClientDelegate.get(ApacheHttpClientDelegate.java:67)
 	at io.fabric8.maven.docker.access.hc.DockerAccessWithHcClient.getServerApiVersion(DockerAccessWithHcClient.java:125)
 	... 24 more
 Caused by: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
 	at sun.security.validator.PKIXValidator.doBuild(java.base@9-ea/PKIXValidator.java:386)
 	at sun.security.validator.PKIXValidator.engineValidate(java.base@9-ea/PKIXValidator.java:291)
 	at sun.security.validator.Validator.validate(java.base@9-ea/Validator.java:264)
 	at sun.security.ssl.X509TrustManagerImpl.validate(java.base@9-ea/X509TrustManagerImpl.java:343)
 	at sun.security.ssl.X509TrustManagerImpl.checkTrusted(java.base@9-ea/X509TrustManagerImpl.java:237)
 	at sun.security.ssl.X509TrustManagerImpl.checkServerTrusted(java.base@9-ea/X509TrustManagerImpl.java:124)
 	at sun.security.ssl.ClientHandshaker.checkServerCerts(java.base@9-ea/ClientHandshaker.java:1822)
 	... 50 more
 Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
 	at sun.security.provider.certpath.SunCertPathBuilder.build(java.base@9-ea/SunCertPathBuilder.java:141)
 	at sun.security.provider.certpath.SunCertPathBuilder.engineBuild(java.base@9-ea/SunCertPathBuilder.java:126)
 	at java.security.cert.CertPathBuilder.build(java.base@9-ea/CertPathBuilder.java:297)
 	at sun.security.validator.PKIXValidator.doBuild(java.base@9-ea/PKIXValidator.java:381)
 	... 56 more` 

This bug might corresponds to: [JDK-8154732](https://bugs.openjdk.java.net/browse/JDK-8154732) 

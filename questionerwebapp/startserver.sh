mvn gwt:run -Dgwt.codeServerPort=9997 -Dgwt.superDevMode=false -Dgwt.module=com.ap.questinerwebapp.questionerwebapp -X

#
#Name: Maven GWT Plugin
#Description: Maven plugin for the Google Web Toolkit.
#Group Id: org.codehaus.mojo
#Artifact Id: gwt-maven-plugin
#Version: 2.7.0
#Goal Prefix: gwt

#This plugin has 16 goals:

#gwt:clean
#  Description: Cleanup the webapp directory for GWT module compilation output

#gwt:compile
#  Description: Invokes the GWT Compiler for the project source. See compiler
#    options :
#    http://www.gwtproject.org/doc/latest/DevGuideCompilingAndDebugging.html#DevGuideCompilerOptions

#gwt:compile-report
#  Description: see
#    http://code.google.com/webtoolkit/doc/latest/DevGuideCompileReport.html#Usage

#gwt:css
#  Description: Creates CSS interfaces for css files. Will use the utility
#    tool provided in gwt sdk which create a corresponding Java interface for
#    accessing the classnames used in the file.

#gwt:debug
#  Description: Runs the project with a debugger port hook (optionally
#    suspended).

#gwt:eclipse
#  Description: Goal which creates Eclipse lauch configurations for GWT
#    modules.

#gwt:eclipseTest
#  Description: Goal which creates Eclipse lauch configurations for
#    GWTTestCases.
#  Deprecated. use google eclipse plugin
#  http://code.google.com/intl/fr-FR/eclipse/docs/users_guide.html

#gwt:generateAsync
#  Description: Goal which generate Async interface.

#gwt:help
#  Description: Display help information on gwt-maven-plugin.
#    Call mvn gwt:help -Ddetail=true -Dgoal=<goal-name> to display parameter
#    details.

#gwt:i18n
#  Description: Creates I18N interfaces for constants and messages files.

#gwt:mergewebxml
#  Description: Merges GWT servlet elements into deployment descriptor (and
#    non GWT servlets into shell).
#    If you use scanRemoteServiceRelativePathAnnotation you must bind this mojo
#    to at least compile phase Because the classpath scanner need to see compile
#    classes

#gwt:resources
#  Description: Copy GWT java source code and module descriptor as resources
#    in the build outputDirectory. Alternative to declaring a <resource> in the
#    POM with finer filtering as the module descriptor is read to detect sources
#    to be copied.

#gwt:run
#  Description: Runs the project in the GWT (Classic or Super) Dev Mode for
#    development.

#gwt:run-codeserver
#  Description: Runs GWT modules with Super Dev Mode.

#gwt:source-jar
#  Description: Add GWT java source code and module descriptor as resources to
#   project jar. Alternative to gwt:resources for better Eclipse projects
#    synchronization.

#gwt:test
#  Description: Mimic surefire to run GWTTestCases during integration-test
#    phase, until SUREFIRE-508 is fixed

#For more information, run 'mvn help:describe [...] -Ddetail'


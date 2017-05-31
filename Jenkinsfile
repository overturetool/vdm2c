node {
  try
  {
    // Mark the code checkout 'stage'....
    stage 'Checkout'
    checkout scm
    sh 'git submodule update --init'

    step([$class: 'GitHubSetCommitStatusBuilder'])

    stage ('Clean'){
      withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {

        // Run the maven build
        sh "mvn clean -PWith-IDE -Dtycho.mode=maven -fn"
      }}

    stage ('Compile core'){
      withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {

        // Run the maven build
        sh "mvn compile"
      }}

    stage ('Install IDE'){
      withMaven(mavenLocalRepo: '.repository', mavenSettingsFilePath: "${env.MVN_SETTINGS_PATH}") {

        // Run the maven build
        sh "mvn install -PWith-IDE -Pall-platforms -P!linux64 -DexternalTestsPath=$OVERTURE_EXTERNAL_TEST_ROOT -P!ui-tests -Pforce-download-externals -Pcodesigning"
        step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
        step([$class: 'JacocoPublisher', exclusionPattern: '**/org/overture/ast/analysis/**/*.*, **/org/overture/ast/expressions/**/*.*, **/org/overture/ast/modules/**/*.*, **/org/overture/ast/node/**/*.*,**/org/overture/ast/patterns/**/*.*, **/org/overture/ast/statements/**/*.*, **/org/overture/ast/types/**/*.*, **/org/overture/codegen/ir/**/*, **/org/overture/ide/**/*'])

        step([$class: 'TasksPublisher', canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', high: 'FIXME', ignoreCase: true, low: '', normal: 'TODO', pattern: '', unHealthy: ''])
      }}
        
  } catch (any) {
    currentBuild.result = 'FAILURE'
    throw any //rethrow exception to prevent the build from proceeding
  } finally {

    stage ('Clean up workspace'){

      step([$class: 'WsCleanup'])
    }
        
    stage('Reporting'){

      step([$class: 'GitHubCommitStatusSetter'])

      // Notify on build failure using the Email-ext plugin
      emailext(body: '${DEFAULT_CONTENT}', mimeType: 'text/html',
               replyTo: '$DEFAULT_REPLYTO', subject: '${DEFAULT_SUBJECT}',
               to: emailextrecipients([[$class: 'CulpritsRecipientProvider'],
                                       [$class: 'RequesterRecipientProvider']]))
    }}
}

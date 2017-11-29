node {
  try
  {
    // Mark the code checkout 'stage'....
    stage ('Checkout'){
      checkout scm
      sh 'git submodule update --init'
    }

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


				stage ('Publish Artifactory'){

						if (env.BRANCH_NAME.endsWith('development')) {

								def server = Artifactory.server "-844406945@1404457436085"
								def buildInfo = Artifactory.newBuildInfo()
								buildInfo.env.capture = true
                buildInfo.env.filter.addExclude("org/overturetool/vdm2c/ide/**")
				        
								def rtMaven = Artifactory.newMavenBuild()
								rtMaven.tool = "Maven 3.1.1" // Tool name from Jenkins configuration
								rtMaven.opts = "-Xmx1024m -XX:MaxPermSize=256M"

                def repo = 'vdm2c'
                if (env.BRANCH_NAME == 'vpb/development')
                {
                    repo = 'vpb-vdm2c'
                }
                else if(env.BRANCH_NAME == 'pvj/development')
                {
                    repo = 'pvj-vdm2c'
                }
								rtMaven.deployer releaseRepo:repo, snapshotRepo:repo, server: server
				        
								rtMaven.run pom: 'pom.xml', goals: 'install', buildInfo: buildInfo

								//get rid of old snapshots only keep then for a short amount of time
								buildInfo.retention maxBuilds: 5, maxDays: 7, deleteBuildArtifacts: true
		            
								// Publish build info.
								server.publishBuildInfo buildInfo
						}
        }

        stage ('Deploy'){
				
				    if (env.BRANCH_NAME.endsWith('development')) {

								sh "echo branch is now ${env.BRANCH_NAME}"
			          
								DEST = sh script: "echo /home/jenkins/web/vdm2c/${env.BRANCH_NAME}/Build-${BUILD_NUMBER}_`date +%Y-%m-%d_%H-%M`", returnStdout:true
								REMOTE = "jenkins@overture.au.dk"

								sh "echo The remote dir will be: ${DEST}"
								sh "ssh ${REMOTE} mkdir -p ${DEST}"
								sh "scp -r ide/repository/target/repository/* ${REMOTE}:${DEST}"
								sh "ssh ${REMOTE} /home/jenkins/update-latest.sh web/vdm2c/${env.BRANCH_NAME}"
						}
        }
        
  } catch (any) {
    currentBuild.result = 'FAILURE'
    throw any //rethrow exception to prevent the build from proceeding
  } finally {

    stage ('Clean up workspace'){

      step([$class: 'WsCleanup'])
    }
        
    stage('Reporting'){

      // Notify on build failure using the Email-ext plugin
      emailext(body: '${DEFAULT_CONTENT}', mimeType: 'text/html',
               replyTo: '$DEFAULT_REPLYTO', subject: '${DEFAULT_SUBJECT}',
               to: emailextrecipients([[$class: 'CulpritsRecipientProvider'],
                                       [$class: 'RequesterRecipientProvider']]))
    }}
}

node {
   stage 'Checkout'

   checkout scm

   stage 'Build'

   sh "chmod +x gradlew"
   sh "./gradlew clean build publish curseTools --stacktrace"

   stage "Archive artifacts"

   archive 'build/libs/*'
}
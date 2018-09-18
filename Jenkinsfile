node {
    stage('Prepare') {
        properties([pipelineTriggers([githubPush()])])
        checkout scm
        sh 'mvn clean'
    }

    stage('Build') {
        sh 'mvn compile'
        sh 'mvn hpi:hpi'
    }

    stage('Archive') {
        archiveArtifacts 'target/jenkins-discord.hpi'
    }
}
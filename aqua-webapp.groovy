#!/usr/bin/env groovy
pipeline {
    
    agent any
    
    environment {
        DOCKER_HUB_REPO = "pddhamdhe/aqua-web-app"
        registryCredential = 'dockercred'
        CONTNAME = "aqua-webapp"
        CONPORT = "8080"
        dockerImage = ""
        }
        
    stages {
        stage('Checkout Source') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/pddhamdhe/aqua-webapp.git']]])
            }
        }
        
        stage('Build Image') {
            steps{
                script {
                   dockerImage = docker.build("$DOCKER_HUB_REPO:${env.BUILD_ID}")
                }
            }
        }
    
        stage('Deploy and Verify Image') {
            steps{
                script {
                    sh "docker image tag $DOCKER_HUB_REPO:latest $DOCKER_HUB_REPO:$BUILD_NUMBER"
                    sh "docker run -d -p $CONPORT:$CONPORT --name $CONTNAME $DOCKER_HUB_REPO:$BUILD_NUMBER"
                    
                    CONTIP = sh( script: "docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $CONTNAME",
                                    returnStdout: true).trim()
                    sh '''curl -s http://'''+CONTIP+''':$CONPORT'''
                    sh '''
                    docker stop $CONTNAME
                    docker rm $CONTNAME
                    '''
                }
            }
        }

        stage('Push Image') {
            steps{
                script {
                    REGISTRY_URL = "https://index.docker.io/v1/"
                    dockerImage = docker.withRegistry("$REGISTRY_URL", 'dockercred') {
                    dockerImage.push()
                    }
                }
            }
        }
    
        stage('Remove Unused Docker image') {
            steps{
                script{
                    sh "docker rmi $DOCKER_HUB_REPO:$BUILD_NUMBER"
                }
            }
        }
    }
}

pipeline {
  agent {
    kubernetes {
      inheritFrom 'aqua-web-app' 
      yamlFile 'docker-pod.yaml'   
      defaultContainer 'docker'  
    }
  }
  stages {
    stage('Build Docker Image') {
      steps {
        container('docker') {  
          sh "docker build -t pddhamdhe/aqua-web-app:latest ." 
          sh "docker images"
          sh "docker push pddhamdhe/aqua-web-app:latest"   
        }
      }
    }
  }
}

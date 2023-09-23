// ed08f3a9e0d646b9b0f6adddb5cb6429
final String url = "34.85.236.100"
pipeline {
    agent { docker { image 'docker:latest' } }
    stages {
        stage('build') {
            steps {
                sh 'docker build -t  einavl/magshimim-web:0.0.1 web-server'
            }
        }
         stage('push') {
            steps {
                withCredentials([string(credentialsId: 'DockerHubPwd', variable: 'dockerpwd')]) {
                sh """
                    docker login -u einavl -p ${dockerpwd} 
                    docker push  einavl/magshimim-web:0.0.1
                """
                }

            }
    }
    stage('redeploy'){
        steps{
            sshagent(credentials : ['ssh-einav-inst']) {
            sh "ssh -o StrictHostKeyChecking=no einav.leybsohor@${url} sudo docker stop magshimim-einav | true"
            sh "ssh -o StrictHostKeyChecking=no einav.leybsohor@${url} sudo docker run --pull=always --name=magshimim-einav --rm -d -p 8081:80  einavl/magshimim-web:0.0.1"
    
        }
        }
    }

    stage('Tests'){
         agent { docker { image 'curlimages/curl' } }
        steps{
            script {
                    final String response = sh(script: "curl -s ${url}:8081", returnStdout: true).trim()
                    echo response
                }
        }
    }
    }
}